# Android厂商APP接入配置指南

以下流程（从注册AppID到Manifest配置）适用于新接入本SDK，如果您需要从原有的SDK迁移到新版本，请参阅[版本更新日志](版本更新日志.md)，其中有针对每个版本的API变更和就版本迁移指南。**新版本会带来更多特性、修复缺陷，请尽量保持使用最新的SDK版本。**

## 1. 配置Gradle脚本

打开工程app目录下的build.gradle。

配置依赖：

```groovy
dependencies {
    // ...
    def tvsVer = '2.1.3'
    // 核心模块，被其他组件依赖
    implementation "com.tencent.yunxiaowei.dmsdk:core:$tvsVer"
    // AI Speech模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:aispeech:$tvsVer"
    // QQ音乐会员模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:member:$tvsVer"
    // 二维码业务模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:qrcode:$tvsVer"
    // 音箱配置模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:speaker:$tvsVer"
    // TSKM技能服务模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:tskm:$tvsVer"
    // HTML5 WebView模块，可选
    implementation "com.tencent.yunxiaowei.dmsdk:web:$tvsVer"
    // 微信登录，如果需要微信登录功能则必须
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-with-mta:5.4.0'
    // QQ登录，如果需要QQ登录功能则必须
    implementation files('libs/open_sdk_r6052_lite.jar')
}
```

确保minSdkVersion大于等于15。

DMSDK已经通过ConsumerProguard自动支持ProGuard配置，不需要您为DMSDK额外配置ProGuard文件。

## 2. 接入登录

如果需要使用DMSDK的微信登录和QQ登录功能，则需要进行以下的配置。

### 2.1. 准备App ID

在[微信开放平台](https://open.weixin.qq.com/)和[QQ互联平台](https://connect.qq.com/index.html)注册AppId。注意确保申请时填写的包名和应用包名一致、并确保signingConfigs目录下storeFile.file参数路径正确，keyAlias、keyPassword、storePassword均与微信开放平台下签名参数一致。

### 2.2. 配置项目

打开工程AndroidManifest.xml。

如果要接入微信，需要在application标签下添加以下内容：

```xml
<activity
    android:name="com.tencent.ai.tvs.WXEntryActivity"
    android:exported="true"
    android:launchMode="singleTask" />
<activity-alias
    android:name="${applicationId}.wxapi.WXEntryActivity"
    android:exported="true"
    android:launchMode="singleTask"
    android:targetActivity="com.tencent.ai.tvs.WXEntryActivity" />
```

`com.tencent.ai.tvs.WXEntryActivity`是DMSDK提供的WXEntryActivity默认实现。

如果需要自定义该Activity的实现，则需要在onReq和onResp中调用DMSDK的相关接口，如下所示：

```java
@Override
public void onReq(BaseReq baseReq) {
    if (!LoginProxy.getInstance().onReq(baseReq)) {
        // 在此处添加您的自定义实现
        Toast.makeText(this, "DMSDK didn't handle onReq", Toast.LENGTH_SHORT).show();
    }
}

@Override
public void onResp(BaseResp baseResp) {
    if (!LoginProxy.getInstance().onResp(baseResp)) {
        // 在此处添加您的自定义实现，如响应微信分享回调
        Toast.makeText(this, "DMSDK didn't handle onResp", Toast.LENGTH_SHORT).show();
    }
}
```

详细请参考demo工程中提供的示例WXEntryActivity类。

如果要接入QQ登录，则需要在Manifest中的application标签下添加以下内容：

```xml
<activity
    android:name="com.tencent.tauth.AuthActivity"
    android:launchMode="singleTask"
    android:noHistory="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <!-- 注意：此处您需要按照QQ互联的文档配置您申请的QQ登录API -->
        <data android:scheme="tencent101470979" />
    </intent-filter>
</activity>
<activity
    android:name="com.tencent.connect.common.AssistActivity"
    android:configChanges="orientation|keyboardHidden"
    android:screenOrientation="behind"
    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```

注意需要将示例中的AppID改为自己在QQ互联平台注册的AppID。

以上接入流程基于微信和QQ的登录SDK的官方接入文档，详细请参考对应的文档。

## 3. Application配置

将Manifest中的application的name改为自定义的Application类，然后在其中的onCreate中加入如下初始化代码：

```java
public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化TVS账号体系模块，依次传入应用上下文、微信AppID和QQ互联AppID；如果只需要支持一种登录平台，则另一个平台的AppID直接传入空字符串即可
        // 如果需要使用第三方账号体系，或Web模块需要接入非DMSDK账号体系，可以传递到第四个参数，TVSWeb的init方法已经被废弃
        LoginProxy.getInstance().registerApp(this, "YOUR_WEIXIN_APPID", "YOUR_QQ_OPEN_APPID");
    }
}
```

## 4. Q&A

1. 必须接入手机SDK吗？

   是的。无论设备端接的是SDK方案还是云端API的方案，在伴随APP上都需要接入手机SDK，来做帐号授权、设备绑定、音乐服务授权、云端闹钟管理、音色控制选择等操作。

2. 什么时机需要主动触发刷票？

   1. APP启动时；

   2. 从APP给设备传递ClientId之前；

   3. APP需要Token操作的时候。

3. 云端绑定有何作用？

   云端绑定可以作为服务请求合法性校验的一个重要维度，并可以实现APP上设备管理绑定解绑查看等功能，并为推送功能做好基础。

4. APP和设备如何建立连接？

   目前APP的SDK支持三种连接方式，推荐SmartLink方式，在设备SDK端有完整方案

   1. SmartLink

   2. SoftAP

   3. BLE


## 5. 附言：Android厂商APP设备管理接入

### 5.1 开发指南

#### 5.1.1 设备绑定逻辑图

![](image/devicebind.png)

#### 5.1.2 厂商APP接入设备管理系统配置步骤

1. aar配置

   将tvsdevicelib-release.aar，放入app\libs目录下

2. gradle配置

   在build.gradle将以下参数配置在dependencies属性下

   ```groovy
   compile(name:'tvsdevicelib-release', ext:'aar')
   compile 'com.google.code.gson:gson:2.8.1'
   compile 'com.squareup.okhttp3:okhttp:3.8.1'
   ```

3. manifest配置

   1. 确保package名称与微信开放平台下注册的包名一致

   2. 让应用的Application类继承`com.tencent.ai.tvsdevice.DeviceApplication`，添加android:exported=”true”属性。

### 5.2 名词解释

#### 5.2.1 UPnP

通用即插即用 （UPnP） 是一种用于 PC 机和智能设备（或仪器）的常见对等网络连接的体系结构，尤其是在家庭中。UPnP 以 Internet 标准和技术（例如 TCP/IP、HTTP 和 XML）为基础，使这样的设备彼此可自动连接和协同工作，从而使网络（尤其是家庭网络）对更多的人成为可能。

#### 5.2.2 ProductId

产品系列Id，通常在Bot平台生成时即已指定，由AppKey和AppAccessToken组成。

#### 5.2.3 DSN

设备序列号，保证唯一性。
