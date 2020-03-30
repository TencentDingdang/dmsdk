# QQ音乐授权

通过DMSDK接入QQ音乐授权后，用户在登录账号后便能将该账号与其QQ音乐账号绑定，并使音箱能通过TVS服务访问用户QQ音乐账号上的音乐资产。

要接入QQ音乐授权，您需要准备好若干材料，向QQ音乐递交以获取您的应用信息。请**提前五个工作日**发送邮件向QQ音乐提交申请，邮件内容如下：
<br/>
> * 邮件标题：OpenID申请-XXXXX公司
> * 邮件收件人：shuozhao@tencent.com
> * 邮件抄送：tangotang@tencent.com;kenzimo@tencent.com
> * 邮件正文:
>   1. 组织名称：XX公司
>   2. 应用名称：XXX
>   3. 联系人名：（接口人即可）
>   4. 联系电话：（接口人即可）
>   5. 联系邮件：（接受账号开通信息）
>   6. 应用包名：（包含Android、iOS包名，如果有多个包名就用;分割）
>   7. 应用图标：（ 文件，不超过1000KB）
>   8. 业务公钥：
> 
> 其中QQ音乐OpenID授权方案使用的RSA业务密钥位数为1024位，密钥格式使用PKCS#8， 使用OpenSSL来生成的示例如下：
> 
> * 生成原始RSA私钥文件rsa_private_key.pem：`openssl genrsa -out rsa_private_key.pem 1024`
> 
> * 将原始RSA私钥转换为pkcs8格式，得到私钥文件private_key.pem：`openssl pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt -out private_key.pem`
> 
> * 生成RSA公钥文件rsa_public_key.pem：`openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem`
> 
> * 生成成功后，**将公钥文件rsa_public_key.pem给QQ音乐**，pkcs8格式的私钥**private_key.pem**合作方妥善保管
> 
> 在完成申请，得到您的应用信息后，您就可以参考下面的文档了解方案的整体流程与接入的技术细节。在接入的开发实现过程中，您需要用到以下信息：
> 
> * **QQ音乐AppID**，由QQ音乐为您分配，一般是一个数字，如“1”；
> 
> * **回调URL**，是您的应用可以响应的URI Scheme协议，用于被QQ音乐应用拉起，为了避免和其他应用冲突，建议在其中包含您的QQ音乐AppID，如“qqmusic1://”；
> 
> * **RSA私钥**，如前所述，由您在提交申请前自行生成，并在开发过程中作为参数传入。
> 
> **注意**，RSA私钥的使用方式需要关注。如前所述，您需要使用的是pkcs8格式的private_key.pem，使用文本编辑工具打开形如下图：
> 
> ![](https://3gimg.qq.com/trom_s/dingdang/upload/20191204/856372591f1acb06783688ba2f05b12d.png)
> 
> 您需要移除开头、结尾的分割线，并移除换行符，最终变为单行的字符串，如下图：
> 
> ![](https://3gimg.qq.com/trom_s/dingdang/upload/20191204/88f95b722aadf756cfc9321ab68ae10c.png)
> 
> 在使用DM SDK接入过程中，需要传入密钥参数的时候，传入该单行字符串。

本文档是DM SDK Android版接入QQ音乐拉起应用授权的开发指南，请先完整阅读[云小微账号](/doc/page/365)，以确保能够顺利理解本文档中提及的各参数和概念的含义。

## 引入SDK依赖并初始化

QQ音乐授权依赖DMSDK的Core、Web和TSKM模块，请参考[接入指南](https://dingdang.qq.com/doc/page/351)完成接入和初始化。

QQ音乐授权还需要依赖QQ音乐的QPlaySDK。将QQ音乐提供的libQQMusicOpenSDK.a和QQMusicOpenSDK.h添加到项目中即可。您可以在[我们的demo](https://github.com/TencentDingdang/dmsdk/tree/master/demo/iOS/DMSDKDemo/QQMusic)中获取这两个文件。

## 接入QQ音乐QPlaySDK

为了便于定制，第三方CP授权的流程中部分流程需要您自行实现，只需要根据下述文档实现后注入到DM SDK的TSKM模块，DM SDK就能够调用相应的逻辑完成整个授权流程。

同时，您也可以直接从demo中拷贝`DMSDKDemo/CPAuthAgents/QQMusicAuthAgent.*`提供的默认实现，其中`QQMusicAuthAgent`即为默认实现，您可以直接使用或根据需求修改。

若选择自行实现，则需要新建一个`interface`，实现`TVSCPAuthAgent`接口，以供DM SDK调用：

- 在`requestCPCredentialWithHandler`中实现拉起QQ音乐授权的逻辑，并在授权成功后通过参数中的回调返回OpenID、OpenToken和ExpireTime；
- 在`checkCPInstalled`中实现判断QQ音乐是否已经安装的逻辑；
- 在`jumpToAppStore`中实现跳转到商店页引导用户下载QQ音乐的逻辑。

您可以参考默认实现的`QQMusicAuthAgent`熟悉各接口需要实现的内容。

将实现好的类在初始化后注入到TSKM模块中：

```objective-c
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // 此处是初始化DMSDK
    [[TVSEnvironment shared]enableLog];
    [[TVSAuthManager shared]registerApp];
    // 在这里注入QQ音乐授权实现到DMSDK，注意参数中填入您申请的QQ音乐AppID、密钥和配置对应的回调URL
    [[TVSCPAuthAgentManager shared]setAgent:[[QQMusicAuthAgent alloc]initWithAppId:QQ_MUSIC_APP_ID andSecretKey:QQ_MUSIC_SECRET_KEY andCallbackUrl:QQ_MUSIC_CALLBACK_URL] ofCP:TVSCPQQMusic];
    return YES;
}
```

正确配置的参数格式应该形如下图：

![](https://3gimg.qq.com/trom_s/dingdang/upload/20191115/ca89008a3ed3f16e804dd959d679815f.png)

> 请注意参照QQ音乐提供的文档，从pkcs8格式的私钥文件private_key.pem中取出符合格式要求的私钥字符串。

支持打开QQ音乐：

```objective-c
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    // 处理微信/QQ 登录跳转
    if ([[TVSAuthManager shared] handleOpenUrl:url]) return YES;
    // 处理QQ音乐跳转
    if ([QQMusicOpenSDK handleOpenURL:url]) return YES;
    return NO;
}
```

接下来配置info.plist，首先需要允许应用拉起QQ音乐：

![](https://3gimg.qq.com/trom_s/dingdang/upload/20191106/a53335efa16d6e4bc22cc69a41e2ae7f.png)

接下来为应用配置QQ音乐回调的URL，建议参考demo配置为`qqmusic<APP_ID>://`：

![](https://3gimg.qq.com/trom_s/dingdang/upload/20191106/71acf7ecf72fff2aa12c2891dfd5fbf6.png)

## 主账号登录与设备绑定

在用户能够进行授权之前，需要用户完成登录和设备绑定流程。

调用如下接口拉起微信或QQ登录：

```objective-c
[[TVSAuthManager shared]wxLoginWithViewController:self handler:^(TVSAuthResult result){
    if (result == TVSAuthResultSuccess) {
        // 授权成功
    } else {
        // 授权失败
    }
}];
```

登录成功后，调用如下的接口完成设备绑定：

```java
TVSDeviceInfo* device = [TVSDeviceInfo new];
device.productId = @"此处填写Product ID";
device.dsn = @"此处填写DSN";
// bindType、pushIDExtra填写内容请参考DMSDK接口文档，此处仅作示例！
device.bindType = TVSDeviceBindTypeTVSSpeaker;
device.pushIdExtra = PUSH_ID_EXTRA_TVS_SPEAKER;
[[TVSDeviceManager shared]bindDevice:device handler:^(BOOL success) {
    if (success) {
        // 绑定成功
    } else {
        // 绑定失败
    }
}];
```

详细的登录和绑定流程文档请参考：[云小微账号](/doc/page/365)。

## 展示QQ音乐授权页面开始授权

QQ音乐授权的页面使用Web方式呈现，使用TVSWebView组件来展示Web页面。在展示之前，首先需要注入设备信息：

```objective-c
TVSDeviceInfo * device = [TVSDeviceInfo new];
device.productId = @"your:product-id";
device.dsn = @"1";
_webview.device = device;
```

注意这里填入的设备信息应该和前面绑定设备时传入的一致，传入需要被授权使用音乐服务的设备的信息。

注入设备信息后，通过下面的方式加载QQ音乐授权状态页面：

```objective-c
[_webview loadPage:TVSWebPageTypeCPAuthQQMusic];
```

> 打开QQ音乐授权页面前，需要事先调用刷票，以保证当前登录主账号的票据有效！

这段代码会在对应的TVSWebView中呈现QQ音乐授权状态的页面，在用户未授权时，会展示下图的授权请求UI：

![](https://3gimg.qq.com/trom_s/dingdang/upload/20191106/e7e14193b0db3b3d086668a35e9886e5.png)

用户点击授权按钮后会调用前面实现的Agent相关接口，拉起QQ音乐完成授权。授权成功回到该页面后，页面会自动刷新，呈现已经绑定的UI，用户可以看到自己的QQ音乐账号信息，如会员状态等：

![](https://3gimg.qq.com/trom_s/dingdang/upload/20191106/2987d64ef1524c66e7966ce75e5aa44e.png)

需要用户解除绑定时，也可以通过同样的方式打开该页面引导用户解绑。

## 技能账号授权管理
[查询技能的账号绑定状态](https://github.com/TencentDingdang/tvs-tools/blob/master/Tsk%20Protocol/domains_V3/TSKOAuth.md)

## 获取授权结果通知

在上述流程中，授权完成后会展示用户的QQ音乐账号的信息，若您需要执行其他操作（如关闭该页面、跳转到点播页面等），则可以通过[ProxyData协议](https://dingdang.qq.com/doc/page/351)收到授权结果的通知。在授权结束后，会收到如下的JSON字符串：

```json
{
  "action": "cpAuthResult",
  "data": {
    "cp": "qq_music",
    "code": 0
  }
}
```

您可以通过`data.code`字段判断是否授权成功，此处的错误码和TVSCPError枚举中的定义一致。


在鉴权失败(如DSN无效)时，您会收到如下的通知消息：

```json
{
    "action": "cpGetUserInfoErr",
    "data": {
        "cp": "qq_music",
        "code": 2,
        "errMsg": "DSN鉴权失败"
    }
}
```

当请求H5页面，后台通知H5设备未入库时，您会收到如下的通知消息：

```json
{
    "action": "cpGetUserInfoErr",
    "data": {
        "cp": "qq_music",
        "code": 6,
        "errMsg": "invalid cp"
    }
}
```

解除CP账号的授权时，您也会收到解除结果的通知消息：

```json
{
    "action": "cpUnbindUserDevice",
    "data": {
        "cp": "qq_music",
        "code": 0
    }
}
```