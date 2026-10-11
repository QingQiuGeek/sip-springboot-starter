# sip-springboot-starter

基于 Spring Boot 的 GB28181 SIP 协议 SDK，与 ZLMediaKit 结合使用，实现视频流推拉流。

## 版本说明

| 版本           | Spring Boot | JDK        | 说明     |
| -------------- | ----------- | ---------- | -------- |
| `1.0-SNAPSHOT` | 2.7.3       | JDK 8      | 当前版本 |
| `2.0-SNAPSHOT` | **4.0.6**   | **JDK 21** | 新版本   |

**⚠️ 注意：请根据主项目的 Spring Boot 版本选择对应的 SDK 版本，避免版本冲突！**

---

## 使用方法（推荐）

### 1. 安装到本地 Maven 仓库

```bash
# 克隆项目
git clone https://github.com/QingQiuGeek/sip-springboot-starter.git

# 进入项目目录
cd sip-springboot-starter

# 安装到本地仓库（跳过测试）
mvn clean install -DskipTests
```

### 2. 主项目引入依赖

在项目的 `pom.xml` 中添加：

```xml
<!-- 配置额外的仓库地址 -->
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>

<!-- Spring Boot 4.0.6 -->
<dependency>
	    <groupId>com.github.QingQiuGeek</groupId>
	    <artifactId>sip-springboot-starter</artifactId>
	    <version>v2.0</version>
</dependency>

<!-- Spring Boot 2.7.3 -->
<dependency>
	    <groupId>com.github.QingQiuGeek</groupId>
	    <artifactId>sip-springboot-starter</artifactId>
	    <version>v1.0</version>
</dependency>
```

或

```xml
<!-- 无需配置额外的仓库地址和认证，Maven 会自动从本地仓库加载。 -->

<dependency>
    <groupId>org.nkk</groupId>
    <artifactId>sip-springboot-starter</artifactId>
    <version>2.0-SNAPSHOT</version> 
</dependency>
```
---

## 功能特性

- ✅ GB28181 2016 协议支持
- ✅ 实时预览（点播）
- ✅ 历史回放
- ✅ 云台控制
- ✅ 设备目录查询
- ✅ 设备状态查询
- ✅ 录像管理
- ✅ 与 ZLMediaKit 流媒体服务器集成

---

## 注意事项

1. **ZLMediaKit 必须开启鉴权**，否则推流成功后 `MediaPlayInfo` 没有播放地址
2. **并发限制**：经测试 180 路以下基本没问题，超过后可能出现拉流失败（时序问题）
3. **适用协议**：GB28181 2016

---

## 配置说明

```yaml
sip:
  logs: 'OFF' # 日志级别：ON/OFF
  server:
    ip: 127.0.0.1 # SIP 服务 IP
    port: 5060 # SIP 服务端口
    id: 44010200492000000001 # SIP 服务器 ID
    domain: 4401020049 # SIP 域
    password: admin123 # SIP 密码
  subscribe:
    catalog: true # 订阅设备目录
    alarm: false # 订阅报警
    location: false # 订阅位置
  media:
    ip: 8.32.184.22 # ZLMediaKit 服务 IP
    port: 8880 # ZLMediaKit HTTP 端口
    media-id: ~ # ZLMediaKit ID（可选）
    secret: ~ # ZLMediaKit 密钥（可选）
  stream:
    autoClose: true # 无人观看自动关闭
    enableRtsp: true # 启用 RTSP
    enableRtmp: true # 启用 RTMP
    enableHlsFmp4: true # 启用 HLS-FMP4
    enableHls: true # 启用 HLS
    enableFmp4: true # 启用 FMP4
    enableTs: true # 启用 TS
```

---

## 核心 API

### 1. SipMessageTemplate - SIP 消息模板

封装了常用 SIP 请求操作，开发主要用此类实现视频流推拉流。

| 方法                                          | 说明             |
| --------------------------------------------- | ---------------- |
| `openRtpRealTime(toDevice)`                   | 实时预览（点播） |
| `openRtpRecord(toDevice, beginTime, endTime)` | 历史回放         |
| `closeRtp(toDevice, streamId)`                | 关闭点播         |
| `getDeviceInfo(toDevice)`                     | 获取设备信息     |
| `getDeviceStatus(toDevice)`                   | 获取设备状态     |
| `getDeviceCatalog(toDevice)`                  | 获取设备目录     |
| `startRecord(app, streamId)`                  | 开启录制         |
| `closeRecord(app, streamId)`                  | 关闭录制         |

### 2. ZlmRestTemplate - ZLMediaKit REST API

封装了 ZLMediaKit 的 HTTP 接口调用。

---

## 相关项目

| 项目                                                                | 说明                       |
| ------------------------------------------------------------------- | -------------------------- |
| [原项目](https://gitee.com/waimifeier/sip-spring-boot-starter)      | 本项目基于此修改增强       |
| [iot-communication](https://gitee.com/xingshuang/iot-communication) | 更完善强大的物联网通信项目 |
| [WVP-PRO](https://doc.wvp-pro.cn/#/)                                | 开箱即用的视频流平台       |
| [ZLMediaKit](https://docs.zlmediakit.com/zh/)                       | 流媒体服务器               |

---

## GitHub Maven Registry

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Maven Packages</name>
        <url>https://maven.pkg.github.com/QingQiuGeek/sip-springboot-starter</url>
    </repository>
</repositories>
```

---

## 开发使用示例

### 实时预览

```java
@Resource
private SipMessageTemplate sipMessageTemplate;

public void playCamera(String sipId, String channelNo) {
    // 构建设备信息
    ToDevice toDevice = new ToDevice();
    toDevice.setDeviceId(sipId);
    toDevice.setChannelId(channelNo);
    toDevice.setTransport("UDP");

    // 发起实时点播
    FutureEvent<MediaPlayInfo> futureEvent = sipMessageTemplate.openRtpRealTime(toDevice);
    MediaPlayInfo playInfo = futureEvent.get(10, TimeUnit.SECONDS);

    // 获取播放地址
    System.out.println("HLS: " + playInfo.getPlayUrl().getHls());
    System.out.println("FLV: " + playInfo.getPlayUrl().getFlv());
    System.out.println("FMP4: " + playInfo.getPlayUrl().getFmp4());
}
```
