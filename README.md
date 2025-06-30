### 说明
**该SDK适用于GB28181 2016协议**
1. zlmedia必须开启鉴权，如果不开启，那么就算流推过来，mediaPlayInfo也没有播放的url。详见：org.nkk.sip.core.session.impl.InviteManager.processAuth
2. 经测试，180路以下基本没问题，继续增加后就会出现拉流失败，这个纯代码的并发时序问题，与硬件无关。详见：org.nkk.media.hook.service.impl.DefaultZlmHookServiceImpl.onPublish
3. 原项目 https://gitee.com/waimifeier/sip-spring-boot-starter 本项目在此基础上作了修改增强
4. 更完善强大的项目 https://gitee.com/xingshuang/iot-communication
5. 开箱即用的视频流平台 https://doc.wvp-pro.cn/#/
6. zlmedia流媒体：https://docs.zlmediakit.com/zh/
___
### 一、配置

#### 1.配置sip服务
```yaml
sip:
  logs: 'OFF'
  server:
    ip: 127.0.0.1
    port: 5060
    id: 44010200492000000001
    domain: 4401020049
    password: admin123
  subscribe:
    catalog: true
    alarm: false
    location: false
  media:
    ip: 8.32.184.22
    port: 8880
    media-id: ~
    secret: ~
  stream:
    autoClose: true
    enableRtsp: true
    enableRtmp: true
    enableHlsFmp4: true
    enableHls: true
    enableFmp4: true
    enableTs: true
```

#### 二、核心API
> 封装了常用请求操作
开发主要用sipMessageTemplate即可实现推拉流等操作
#### 1.sip
- sipMessageTemplate
#### 2.zlmedia
- zlmRestTemplate
