# 调用UniAccess接口

[UniAccess接口](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/uniAccess%E6%8E%A5%E5%8F%A3%E8%83%BD%E5%8A%9B.md)用于访问云小微技能平台的一系列服务，如： 音乐硬件设备注册、设备闹钟数据同步、通讯录上传等。

可调用的服务，相关协议请查阅[协议文档](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/uniAccess%E6%8E%A5%E5%8F%A3%E8%83%BD%E5%8A%9B.md)。

在DM SDK中发起UniAccess请求之前必须保证已经登录主账号，并提供设备信息（Product ID和DSN）。

本文档中提供的调用示范都是示例代码，涉及的类的完整接口列表及参数说明您可以在[Javadoc](https://tencentdingdang.github.io/dmsdk/android/latest-redirect.html)上找到。

## 通用调用

一个UniAccess请求包含三个字段：

 * domain

 * intent

 * jsonBlobInfo

如果需要使用DM SDK发送请求，调用方式如下：

```java
TVSTSKM tskm = new TVSTSKM("your-product-id", "dsn");
tskm.sendUniAccessRequest("your-domain", "your-intent", "{\"someKey\": \"someValueInJsonBlobInfo\"}", callback);
```

如上例所示，使用Product ID和DSN构造TVSTSKM实例，然后调用sendUniAccessRequest接口，传入UniAccess接口技能服务协议中的domain、intent、jsonBlobInfo字段和请求结果回调，成功时可以得到UniAccess接口返回的jsonBlobInfo数据。上例中发送的domain为`your-domain`，intent为`your-intent`，jsonBlobInfo则为`{"someKey": "someValueInJsonBlobInfo"}`。

请求结果回调中会返回错误码、错误信息和返回的jsonBlobInfo，但要注意对错误信息和jsonBlobInfo判空。

## 闹钟与提醒

DM SDK在通用调用之上封装了用于访问闹钟与提醒技能数据的API：`TVSAlarm`和`TVSReminder`。

以添加闹钟为例，调用示例如下：

```java
TVSAlarm alarm = new TVSAlarm("your-product-id", "dsn");
// Add alarm
alarm.manage("{\n" +
        "  \"eType\": 0,\n" +
        "  \"stCloudAlarmReq\": {\n" +
        "    \"stAccountBaseInfo\": {\"strAcctId\":\"\"},\n" +
        "    \"eCloud_type\":1,\n" +
        "    \"vCloudAlarmData\": [\n" +
        "      {\n" +
        "        \"eRepeatType\": 1,\n" +
        "        \"lStartTimeStamp\": 4722659904351\n" +
        "      }\n" +
        "    ]\n" +
        "  }\n" +
        "}", callback);
```

其中`manage`的第一个参数即为jsonBlobInfo字段。

闹钟的UniAccess文档可以在下面的链接中查阅：

 * [tvs-tools/闹钟 云端管理接入指引.md at master · TencentDingdang/tvs-tools](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/%E9%97%B9%E9%92%9F%20%E4%BA%91%E7%AB%AF%E7%AE%A1%E7%90%86%E6%8E%A5%E5%85%A5%E6%8C%87%E5%BC%95.md)
 * [tvs-tools/uniAccess接口能力.md at master · TencentDingdang/tvs-tools](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/uniAccess%E6%8E%A5%E5%8F%A3%E8%83%BD%E5%8A%9B.md#3-%E8%AE%BE%E5%A4%87%E9%97%B9%E9%92%9F%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5)

提醒的UniAccess文档可以在下面的链接中查阅：

 * [tvs-tools/提醒 云端管理接入指引.md at master · TencentDingdang/tvs-tools](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/%E6%8F%90%E9%86%92%20%E4%BA%91%E7%AB%AF%E7%AE%A1%E7%90%86%E6%8E%A5%E5%85%A5%E6%8C%87%E5%BC%95.md)
 * [tvs-tools/uniAccess接口能力.md at master · TencentDingdang/tvs-tools](https://github.com/TencentDingdang/tvs-tools/blob/master/doc/uniAccess%E6%8E%A5%E5%8F%A3%E8%83%BD%E5%8A%9B.md#4-%E8%AE%BE%E5%A4%87%E6%8F%90%E9%86%92%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5)

## 设备播控

DM SDK在通用调用之上封装了用于发起设备播控指令的API：`TVSDeviceControl`。

在设备播控方案中，手机端App发出的每一条指令请求都有指令命名空间Namespace、指令名Name和指令内容Payload，而消息ID由DM SDK代为生成。

以“注册多端控制”接口的请求指令为例，示例代码为：

```java
TVSDeviceControl deviceControl = new TVSDeviceControl("your-product-id", "dsn");
String messageId = deviceControl.controlDevice("MultiControl", "Register", "{\"userPushId\":\"somePushId\"}", callback);
```

上述代码会发起Namespace为`MultiControl`，Name为`Register`，Payload为`{"userPushId":"somePushId"}`的指令。在callback中可以收到指令请求的结果，而messageId建议打印到日志中，便于调试。

在接入播控的过程中，您可能会需要向点播页面通信，例如回调正在播放的歌曲，此时可以通过WebView SDK的`TVSWebController`类中的`execJS`接口来执行JS调用。例如，当需要调用`_ddSdkCbPlaySong`这个JS函数，传入的参数是字符串`12`的时候，可以这样调用：

```java
mController.execJS("_ddSdkCbPlaySong", "'12'", "");
```

设备播控的UniAccess可以在下面的链接中查阅：[solution/TVS设备播控 at master · TencentDingdang/solution](https://github.com/TencentDingdang/solution/tree/master/TVS%E8%AE%BE%E5%A4%87%E6%92%AD%E6%8E%A7)。