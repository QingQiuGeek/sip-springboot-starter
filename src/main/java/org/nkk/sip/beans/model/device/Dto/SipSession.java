package org.nkk.sip.beans.model.device.Dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SipSession implements Serializable {


    private static final long serialVersionUID = -5180126749814202678L;
    /**
     * 用户id（国标ID）
     */
    private String userId;

    /**
     * IP
     */
    private String ip;

    /**
     * 端口
     */
    private int port;

    /**
     * 传输方式
     */
    private String transport = "UDP";

    /**
     * 到期时间
     */
    private Integer expires;

    /**
     * 会话信息
     */
    private SipTransactionInfo transactionInfo;
}
