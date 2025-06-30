package org.nkk.sip.core.process.method.impl.message;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.nkk.sip.beans.annotations.SipEvent;
import org.nkk.sip.beans.enums.SipEnum;
import org.nkk.sip.beans.model.base.DeviceBase;
import org.nkk.sip.beans.model.device.Resp.DeviceCatalog;
import org.nkk.sip.beans.model.device.Resp.DeviceInfo;
import org.nkk.sip.beans.model.device.Resp.DeviceRecordList;
import org.nkk.sip.beans.model.device.Resp.DeviceStatus;
import org.nkk.sip.core.builder.SipBuilder;
import org.nkk.sip.core.process.method.AbstractSipRequestProcessor;
import org.nkk.sip.core.service.SipPublisher;
import org.nkk.sip.core.session.impl.SessionManager;
import org.nkk.sip.utils.SipUtil;
import org.nkk.sip.utils.XmlUtils;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.address.Address;
import javax.sip.message.Response;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@SipEvent(value = SipEnum.Method.MESSAGE)
public class MessageRequestProcessor extends AbstractSipRequestProcessor {

    private final Map<String, DeviceRecordList.RecordList> RECORDS = new ConcurrentHashMap<>();
    @Resource
    private SessionManager sessionManager;

    @Override
    public void request(RequestEvent event) {
        SIPRequest request = (SIPRequest) event.getRequest();
        Address address = SipUtil.getAddressFromFromHeader(request);
        // 解析xml
        byte[] content = request.getRawContent();
        DeviceBase deviceBase = XmlUtils.parse(content, DeviceBase.class);
        log.debug("请求「{}」: {}", deviceBase.getCmdType(), address);
        String key = SipUtil.genSubscribeKey(deviceBase.getCmdType(), deviceBase.getDeviceId(), deviceBase.getSn());
        if (deviceBase.getCmdType().equals(SipEnum.Cmd.Keepalive.name())) {
            sessionManager.keepalive(deviceBase);
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.Catalog.name())) {
            SipPublisher.handler(key).ofOk(XmlUtils.parse(content, DeviceCatalog.class));
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.DeviceInfo.name())) {
            SipPublisher.handler(key).ofOk(XmlUtils.parse(content, DeviceInfo.class));
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.DeviceStatus.name())) {
            SipPublisher.handler(key).ofOk(XmlUtils.parse(content, DeviceStatus.class));
        } else if (deviceBase.getCmdType().equals(SipEnum.Cmd.RecordInfo.name())) {
            DeviceRecordList list = XmlUtils.parse(content, DeviceRecordList.class);
            DeviceRecordList.RecordList recordList = list.getRecordList();
            log.info("「录制记录」{}/{}·获取中", recordList.getNum(), list.getSumNum());
            // 一页能查询完成直接返回成功
            if (Objects.equals(recordList.getNum(), list.getSumNum())) {
                SipPublisher.handler(key).ofOk(list);
                log.info("「录制记录」完成");
                RECORDS.remove(key);
            } else {
                DeviceRecordList.RecordList data = RECORDS.putIfAbsent(key, recordList);
                if (null != data) {
                    LinkedList<DeviceRecordList.RecordList.RecordItem> all = new LinkedList<>(data.getRecordList());
                    all.addAll(all.size(), recordList.getRecordList());
                    data.setRecordList(all);
                    data.setNum(data.getNum() + recordList.getNum());
                    if (Objects.equals(list.getSumNum(), data.getNum())) {
                        // 获取完成的数据重新设置到列表中
                        list.setRecordList(data);
                        SipPublisher.handler(key).ofOk(list);
                        log.info("「录制记录」完成");
                        RECORDS.remove(key);
                    }
                }
            }
        } else {
            log.info("other----> {}", XmlUtils.parseString(content));
        }
        // 发送OK
        SipBuilder.buildOKResponse(request).execute();
        log.debug("回复请求「{}」OK", deviceBase.getCmdType());
    }

    @Override
    public void response(ResponseEvent event) {
        SIPResponse response = (SIPResponse) event.getResponse();
        if (response.getStatusCode() != Response.OK) {
            return;
        }
        String callId = response.getCallIdHeader().getCallId();
        SipBuilder.callMessage(response).callOk();
        log.debug("处理「MESSAGE」200响应-OK");
    }


}
