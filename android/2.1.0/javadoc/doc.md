## LoginProxy



### setDebugHandler

设置调试用的回调，可以收到DMSDK给出的部分日志

```
public static void setDebugHandler(@Nullable com.tencent.ai.tvs.base.log.DebugHandler debugHandler)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| debugHandler | 调试回调实现类对象 |

### getInstance

获取单例。

```
public static LoginProxy getInstance()
```

#### 返回

LoginProxy 单例

### registerApp

初始化核心SDK模块，必须在 Application.onCreate() 中调用。

```
public void registerApp(android.content.Context context, java.lang.String wxAppID, java.lang.String qqOpenAppID)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| context | 应用 Context |
| wxAppID | 微信开放平台注册的 App ID |
| qqOpenAppID | QQ互联平台注册的 App ID |

### isTokenExist

是否已经登录。

```
public boolean isTokenExist()
```

#### 返回

若已经登录，返回true，否则返回false

### isTokenExist

是否登录了指定平台的账号。当查询微信平台的时候，当且仅当当前已经登录且登录的平台是微信时会返回true，查询其他平台同理。

```
public boolean isTokenExist(ELoginPlatform platform)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| platform | 需要查询登录态的平台 |

#### 返回

所查询的平台是否登录

### isLogoutBeforeRelogin

是否在再次调用tvsLogin(ELoginPlatform, Activity, TVSCallback)时先退出登录，默认为会退出。

```
public boolean isLogoutBeforeRelogin()
```

#### 返回

是否再次登录前退出登录

### setLogoutBeforeRelogin

设置是否在再次调用tvsLogin(ELoginPlatform, Activity, TVSCallback)时先退出登录。需要在应用启动的时候设置（如Application.onCreate()）。

```
public void setLogoutBeforeRelogin(boolean isLogoutAfterCancel)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| isLogoutAfterCancel | 是否再次登录前退出登录 |

### tvsLogin

发起登录流程。 若登录QQ平台，受QQ互联SDK限制，需要传入一个Activity实例，该实例会被调用 startActivityForResult 且需要在 onActivityResult 中调用 handleQQOpenIntent(int, int, Intent)。

```
public void tvsLogin(ELoginPlatform platform, android.app.Activity activityForQQ, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| platform | 登录平台 |
| activityForQQ | 若登录平台为QQ，传入用于拉起QQ授权界面的Activity实例，否则传入null即可 |
| callback | 登录结果回调 |

### tvsTokenVerify

发起微信刷票/QQ验票流程。

```
public void tvsTokenVerify(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 流程结果回调 |

### tvsAuth

换取TVS平台票据TVS ID。 该接口仅限已经接入过微信和QQ登录平台，现在要集成TVS平台帐号体系的情况下调用，详见流程文档。

```
public void tvsAuth(ELoginPlatform platform, java.lang.String acctRet, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| platform | 已 SDK 登录的平台 |
| acctRet | JSON格式的包含账号信息的字符串 |
| callback | 换取结果回调，成功时返回的参数为TVS ID |

### tvsQQOpenVerify

验证QQ票据。 该接口仅限已经接入过微信和 QQ 登录平台，现在要集成 TVS 平台帐号体系的情况下调用，详见流程文档。

```
public void tvsQQOpenVerify(java.lang.String openID, java.lang.String accessToken, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| openID | 登录账号的 Open ID |
| accessToken | 登录账号的 Access Token |
| callback | 验证结果回调 |

### logout

注销登录，立即生效。

```
public void logout()
```

### getCaptcha

当前登录账号绑定手机号过程中请求发送验证码。

```
public void getCaptcha(java.lang.String phoneNumber, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 接收验证码的待绑定手机号 |
| callback | 请求结果回调 |

### bindPhoneNumber

当前登录账号绑定手机号。

```
public void bindPhoneNumber(java.lang.String phoneNumber, java.lang.String captcha, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 待绑定手机号 |
| captcha | 用户收到并输入的验证码 |
| callback | 绑定结果回调 |

### syncUserInfo

保存用户信息到 TVS 后台(暂时只支持头像昵称)，必须确保登录。

```
public void syncUserInfo(TVSUserInfo userInfo, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| userInfo | 需要保存的用户信息 |
| callback | 请求结果回调 |

### queryUserInfoWithOpenID

通过 Open ID 查询用户信息（暂时只支持头像昵称）

```
public void queryUserInfoWithOpenID(java.lang.String openID, TVSCallback1<TVSUserInfo> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| openID | 用户 Open ID |
| callback | 请求结果回调，成功时返回参数为查询到的用户信息 |

### tvsOpenMiniProgram

已过时。 改用 tvsOpenMiniProgram(String, String, EMiniProgType, MiniProgCallback) 打开微信小程序。在集成TVS账号体系的情况下无法得到微信API实例，可以通过该方法打开小程序URI。参数格式请参考微信小程序官方API。

```
@Deprecated public void tvsOpenMiniProgram(java.lang.String username, java.lang.String path, int miniProgramType, MiniProgCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| username | 用户名 |
| path | 路径 |
| miniProgramType | 小程序类型 |
| callback | 调用结果回调 |

### tvsOpenMiniProgram

打开微信小程序。在集成TVS账号体系的情况下无法得到微信API实例，可以通过该方法打开小程序URI。参数格式请参考微信小程序官方API。

```
public void tvsOpenMiniProgram(java.lang.String username, java.lang.String path, EMiniProgType miniProgramType, MiniProgCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| username | 用户名 |
| path | 路径 |
| miniProgramType | 小程序类型 |
| callback | 调用结果回调 |

### preBindScreenDevice

有屏音箱扫码预绑定

```
public void preBindScreenDevice(TVSDevice device, boolean cancel, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 待预绑定的设备的信息，关于这个参数的字段填入方式，请阅读bindPushDevice(TVSDevice, TVSCallback)的文档 |
| cancel | 是否取消预绑定 |
| callback | 请求结果回调 |

### bindPushDevice

绑定推送设备。 对于绑定（本方法和preBindScreenDevice(TVSDevice, boolean, TVSCallback)）、解绑 （unbindPushDevice(TVSDevice, TVSCallback)）和查询绑定账号（getBoundAccount(TVSDevice, TVSCallback1)）， 这些方法传递的设备信息参数遵循下面的规则： TVSDevice.productID和TVSDevice.dsn字段不区分方案填写； 如果是TVS方案： + TVSDevice.bindType填TVSDeviceBindType.TVS_SPEAKER + TVSDevice.pushIDExtra填ConstantValues.PUSH_ID_EXTRA_TVS 如果是SDK方案： + TVSDevice.bindType填TVSDeviceBindType.SDK_SPEAKER + TVSDevice.pushIDExtra填ConstantValues.PUSH_ID_EXTRA_SDK

```
public void bindPushDevice(TVSDevice device, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 待绑定的设备的信息 |
| callback | 请求结果回调 |

### unbindPushDevice

解绑推送设备。

```
public void unbindPushDevice(TVSDevice device, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 待解绑的设备的信息，关于这个参数的字段填入方式，请阅读bindPushDevice(TVSDevice, TVSCallback)的文档 |
| callback | 请求结果回调 |

### getDeviceInfoListByDSN

通过 Product ID 和 DSN 查询设备列表

```
public void getDeviceInfoListByDSN(TVSDeviceBindType deviceBindType, java.lang.String productID, java.lang.String dsn, TVSCallback1<java.util.ArrayList<TVSDevice>> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| deviceBindType | 设备绑定类型 |
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| callback | 请求结果回调，成功时回调参数为设备信息的列表 |

### getDeviceInfoListByDSN

通过 GUID 查询设备列表

```
public void getDeviceInfoListByDSN(TVSDeviceBindType deviceBindType, java.lang.String guid, TVSCallback1<java.util.ArrayList<TVSDevice>> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| deviceBindType | 设备绑定类型 |
| guid | 设备的GUID |
| callback | 请求结果回调，成功时回调参数为设备信息的列表 |

### getDeviceInfoListByAccount

通过账号查询设备列表。

```
public void getDeviceInfoListByAccount(TVSDeviceBindType deviceBindType, TVSCallback1<java.util.ArrayList<TVSDevice>> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| deviceBindType | 设备的推送绑定类型 |
| callback | 请求结果回调，成功时回调参数为设备信息的列表 |

### getBoundAccount

获取指定设备上绑定的帐号信息

```
public void getBoundAccount(TVSDevice device, TVSCallback1<TVSAccountInfo> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 待查询账号的设备的信息，关于这个参数的字段填入方式，请阅读bindPushDevice(TVSDevice, TVSCallback)的文档 |
| callback | 请求结果回调，成功时参数返回 TVSAccountInfo 对象，其中有效字段仅有登录平台类型、App ID和Open ID |

### setEnv

设置SDK的后台环境，可选测试环境、体验环境、正式环境。 注意：Web模块中的叮当预设URL没有体验环境，使用体验环境等价于正式环境。

```
public void setEnv(ELoginEnv env)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| env | 需要切换到的后台环境 |

### getEnv

获取SDK当前的后台环境

```
public ELoginEnv getEnv()
```

#### 返回

当前的后台环境

### getSDKVersion

获取SDK版本号

```
public java.lang.String getSDKVersion()
```

#### 返回

SDK版本号，格式为“major.minor.patch_yyyyMMdd”，如“2.3.33_20191024”。

### onReq



```
public void onReq(com.tencent.mm.opensdk.modelbase.BaseReq baseReq)
```

### onResp



```
public void onResp(com.tencent.mm.opensdk.modelbase.BaseResp baseResp)
```

### handleQQOpenIntent



```
public boolean handleQQOpenIntent(int requestCode, int resultCode, android.content.Intent data)
```

## TVSTSKM

TSKM平台模块。

### TVSTSKM



```
public TVSTSKM()
```

### requestTSKMUniAccessByDSN

已过时。 改用 requestTSKMUniAccess(String, String, String, String, String, String, TVSCallback1)，其中GUID参数可以传null 发送TSKM平台的通用请求。云端闹钟管理旧版接口被废弃，改用该接口。

```
@Deprecated public static void requestTSKMUniAccessByDSN(java.lang.String productID, java.lang.String dsn, java.lang.String domain, java.lang.String intent, java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| domain | 请求的TSKM服务领域 |
| intent | 请求的TSKM服务意图 |
| blobInfo | 请求的额外JSON格式参数 |
| callback | 请求结果回调，成功时返回JSON格式的请求结果 |

### requestTSKMUniAccessByGUID

已过时。 改用 requestTSKMUniAccess(String, String, String, String, String, String, TVSCallback1)，其中DSN参数可以传null 发送TSKM平台的通用请求。云端闹钟管理旧版接口被废弃，改用该接口。

```
@Deprecated public static void requestTSKMUniAccessByGUID(java.lang.String productID, java.lang.String guid, java.lang.String domain, java.lang.String intent, java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| guid | 设备的GUID |
| domain | 请求的TSKM服务领域 |
| intent | 请求的TSKM服务意图 |
| blobInfo | 请求的额外JSON格式参数 |
| callback | 请求结果回调，成功时返回JSON格式的请求结果 |

### requestTSKMUniAccess

发送TSKM平台的通用请求。

```
public static void requestTSKMUniAccess(java.lang.String productID, java.lang.String dsn, java.lang.String guid, java.lang.String domain, java.lang.String intent, java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| guid | 设备的GUID |
| domain | 请求的TSKM服务领域 |
| intent | 请求的TSKM服务意图 |
| blobInfo | 请求的额外JSON格式参数 |
| callback | 请求结果回调，成功时返回JSON格式的请求结果 |

## TVSChildMode



### TVSChildMode

创建一个对应指定设备和第三方账号的新实例用于儿童模式控制。

```
public TVSChildMode(java.lang.String productId, java.lang.String dsn, java.lang.String accountId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |
| accountId | 第三方账号ID |

### TVSChildMode

创建一个对应指定设备和叮当账号体系的新实例用于儿童模式控制。

```
public TVSChildMode(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |

### getConfig



```
public void getConfig(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

### setConfig



```
public void setConfig(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

## TVSThirdPartyAuth



### TVSThirdPartyAuth

创建一个对应指定设备和第三方账号的新实例用于第三方授权。

```
public TVSThirdPartyAuth(java.lang.String productId, java.lang.String dsn, java.lang.String accountId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |
| accountId | 第三方账号ID |

### TVSThirdPartyAuth

创建一个对应指定设备和叮当账号体系的新实例用于第三方授权。

```
public TVSThirdPartyAuth(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |

### requestCloudDDAuth

拉起云叮当登录，必须保证已经登录！

```
public static void requestCloudDDAuth(android.content.Context context, TVSDevice tvsDevice, java.lang.String actFullName)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| context | 用于启动Activity |
| tvsDevice | 设备信息 |
| actFullName | 启动的activity的完整类名 |

### setAccountDeviceBound

第三方账号和设备信息绑定开关。

```
public void setAccountDeviceBound(boolean bound, TVSAccountInfo account, TVSDevice device, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| bound | 是否绑定 |
| account | 第三方账号信息，必填登录平台、App ID、Open ID和Access Token字段 |
| device | 设备信息，必填Product ID、DSN和GUID字段 |
| callback | 请求结果回调 |

### getBoundAccountByDevice

通过设备信息查询绑定的第三方账号信息。

```
public void getBoundAccountByDevice(TVSDevice device, TVSCallback1<TVSAccountInfo> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 设备信息，必填Product ID、DSN和GUID字段 |
| callback | 请求结果回调，成功时第一个返回参数为查询到的第三方账号信息 |

## TVSReminder



### TVSReminder

创建一个对应指定设备和第三方账号的新实例用于闹钟管理。

```
public TVSReminder(java.lang.String productId, java.lang.String dsn, java.lang.String accountId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |
| accountId | 第三方账号ID |

### TVSReminder

创建一个对应指定设备和叮当账号体系的新实例用于闹钟管理。

```
public TVSReminder(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |

### manage



```
public void manage(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

### sync



```
public void sync(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

### clear



```
public void clear(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

## TVSDeviceControl

多端互动模块。App接入该模块后可以向绑定设备发送控制指令，详细能力请参阅多端互动接入文档。

### TVSDeviceControl

创建一个对应指定设备和第三方账号的新实例用于多端交互设备控制。

```
public TVSDeviceControl(java.lang.String productId, java.lang.String dsn, java.lang.String accountId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |
| accountId | 第三方账号ID |

### TVSDeviceControl

创建一个对应指定设备和叮当账号体系的新实例用于多端交互设备控制。

```
public TVSDeviceControl(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |

### bindDevice

将当前实例指定的设备和账号关联。关联后，该账号可以控制该设备。

```
public void bindDevice(TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### controlDevice

发送指令控制设备。可支持的控制操作和参数请查阅多端互动接入文档。

```
public void controlDevice(java.lang.String namespace, java.lang.String name, java.lang.String payload, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| namespace | 控制指令的命名空间 |
| name | 控制指令的名称 |
| payload | 控制指令的额外参数 |
| callback | 请求结果回调 |

## TVSAlarm

闹钟管理模块。

### TVSAlarm

创建一个对应指定设备和第三方账号的新实例用于闹钟管理。

```
public TVSAlarm(java.lang.String productId, java.lang.String dsn, java.lang.String accountId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |
| accountId | 第三方账号ID |

### TVSAlarm

创建一个对应指定设备和叮当账号体系的新实例用于闹钟管理。

```
public TVSAlarm(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备的Product ID |
| dsn | 设备的DSN |

### manage



```
public void manage(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

### sync



```
public void sync(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

### clear



```
public void clear(java.lang.String blobInfo, TVSCallback1<java.lang.String> callback)
```

## TVSAuthDelegate

使用SDK实现的TVS账号体系。通过SDK完成登录、刷票和认证信息缓存等功能。

### TVSAuthDelegate

默认构造器。

```
public TVSAuthDelegate(android.content.Context context, LoginProxy loginProxy)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| context | 上下文。 |
| loginProxy | 使用的Core模块的LoginProxy实例。可以通过LoginProxy.getInstance()获得。 |

### tvsWXLogin

从接口复制的说明: AuthDelegate 请求微信登录。

```
public void tvsWXLogin(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsWXTokenRefresh

从接口复制的说明: AuthDelegate 请求微信刷票。

```
public void tvsWXTokenRefresh(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsQQOpenLogin

从接口复制的说明: AuthDelegate 请求QQ登录。

```
public void tvsQQOpenLogin(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsLogout

从接口复制的说明: AuthDelegate 请求退出登录，立即生效。

```
public void tvsLogout()
```

### getAccountInfo

从接口复制的说明: AuthDelegate 获取账号信息。 其中的TVSAccountInfo.tvsID可以通过LoginProxy.tvsAuth(ELoginPlatform, String, TVSCallback1)获取。

```
public TVSAccountInfo getAccountInfo()
```

#### 返回

账号信息对象

### getUserInfo

从接口复制的说明: AuthDelegate 获取用户信息。 其中的手机号字段不是必须的，可以不赋值，Web模块目前不会用到该字段。

```
public TVSUserInfo getUserInfo()
```

#### 返回

用户信息对象

### tvsSetPhoneNumber

从接口复制的说明: AuthDelegate 用户信息中的手机号被Web中的页面更新，SDK无法立刻收到推送，因此这里由Web页面主动通知。 接入方实现时可以为空。

```
public void tvsSetPhoneNumber(java.lang.String phoneNumber)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 新的手机号 |

## AuthDelegate

Created by perqinxie on 2019/01/30.

### tvsWXLogin

请求微信登录。

```
void tvsWXLogin(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsWXTokenRefresh

请求微信刷票。

```
void tvsWXTokenRefresh(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsQQOpenLogin

请求QQ登录。

```
void tvsQQOpenLogin(TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调 |

### tvsLogout

请求退出登录，立即生效。

```
void tvsLogout()
```

### getAccountInfo

获取账号信息。 其中的TVSAccountInfo.tvsID可以通过LoginProxy.tvsAuth(ELoginPlatform, String, TVSCallback1)获取。

```
TVSAccountInfo getAccountInfo()
```

#### 返回

账号信息对象

### getUserInfo

获取用户信息。 其中的手机号字段不是必须的，可以不赋值，Web模块目前不会用到该字段。

```
TVSUserInfo getUserInfo()
```

#### 返回

用户信息对象

### tvsSetPhoneNumber

用户信息中的手机号被Web中的页面更新，SDK无法立刻收到推送，因此这里由Web页面主动通知。 接入方实现时可以为空。

```
void tvsSetPhoneNumber(java.lang.String phoneNumber)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 新的手机号 |

## TVSUserInfo

代表用户信息的简单POJO类。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| ID_TYPE_WX | 微信用户信息类型。 |
| ID_TYPE_QQ_OPEN | QQ用户信息类型。 |

### TVSUserInfo



```
public TVSUserInfo()
```

### getIdType

获取用户信息类型。

```
public int getIdType()
```

#### 返回

用户信息类型，可以是ID_TYPE_WX或ID_TYPE_QQ_OPEN

### setIdType

设置用户信息类型。

```
public void setIdType(int idType)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| idType | 用户信息类型，可以是ID_TYPE_WX或ID_TYPE_QQ_OPEN |

### getNickName

获取用户昵称。

```
public java.lang.String getNickName()
```

#### 返回

用户昵称

### setNickName

设置用户昵称。

```
public void setNickName(java.lang.String nickName)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| nickName | 用户昵称 |

### getHeadImgUrl

获取用户头像URL。

```
public java.lang.String getHeadImgUrl()
```

#### 返回

头像URL

### setHeadImgUrl

设置用户头像URL。

```
public void setHeadImgUrl(java.lang.String headImgUrl)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| headImgUrl | 头像URL |

### getSex

获取用户性别。

```
public int getSex()
```

#### 返回

用户性别，返回UserInfoManager.MALE或UserInfoManager.FEMALE

### setSex

设置用户性别。

```
public void setSex(int sex)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| sex | 用户性别，有效值为UserInfoManager.MALE或UserInfoManager.FEMALE |

### getPhoneNumber

获取用户手机号。

```
public java.lang.String getPhoneNumber()
```

#### 返回

手机号

### setPhoneNumber

设置用户手机号。

```
public void setPhoneNumber(java.lang.String phoneNumber)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 手机号 |

## UserInfoManager

用户信息管理模块单例，可以获取当前登录用户的用户信息。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| MALE | 性别男性。 |
| FEMALE | 性别女性。 |

### UserInfoManager



```
public UserInfoManager()
```

### getInstance

获取该模块单例。

```
public static UserInfoManager getInstance()
```

#### 返回

该模块单例

### getPhoneNumber

获取用户手机号。

```
public java.lang.String getPhoneNumber()
```

#### 返回

手机号

### setPhoneNumber

设置用户手机号。

```
public void setPhoneNumber(java.lang.String phoneNumber)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| phoneNumber | 手机号 |

### getSex

获取用户性别。

```
public int getSex()
```

#### 返回

用户性别，返回MALE或FEMALE

### getHeadImgUrl

获取用户头像URL。

```
public java.lang.String getHeadImgUrl()
```

#### 返回

头像URL

### getNickname

获取用户昵称。

```
public java.lang.String getNickname()
```

#### 返回

用户昵称

## AccountInfoManager

账号信息模块。在使用SDK提供的账号认证体系的时候，该模块可以获取AppID、登录状态等信息。

### getPlatformType

获取当前登录的平台。

```
public ELoginPlatform getPlatformType()
```

#### 返回

当未登录时返回null，否则返回登录的平台

### getAccessToken

获取当前登录账号的Access Token。

```
public java.lang.String getAccessToken()
```

#### 返回

如果已登录则返回登录帐号的Access Token

### getRefreshToken

获取当前登录账号的Refresh Token

```
public java.lang.String getRefreshToken()
```

#### 返回

如果已登录则返回登录帐号的Refresh Token

### getOpenID

获取当前登录账号的Open ID

```
public java.lang.String getOpenID()
```

#### 返回

如果已登录则返回登录帐号的Open ID

### getExpireTime

获取当前登录账号的票据超时时间

```
public long getExpireTime()
```

#### 返回

如果已登录则返回登录帐号的票据超时时间

### getTvsID

获取当前登录账号的TVS ID

```
public java.lang.String getTvsID()
```

#### 返回

如果已登录则返回登录帐号的TVS ID

### getAppID

获取当前登录账号的App ID

```
public java.lang.String getAppID()
```

#### 返回

如果已登录则返回登录帐号的App ID，否则返回空字符串

### getAppID

获取指定平台的App ID

```
public java.lang.String getAppID(ELoginPlatform platform)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| platform | 需要获取的App ID的平台类型 |

#### 返回

返回指定平台的App ID

### getUnionID

获取当前登录微信账号的Union ID

```
public java.lang.String getUnionID()
```

#### 返回

如果已登录微信则返回登录帐号的Union ID

### getScope

获取当前登录微信账号的Scope

```
public java.lang.String getScope()
```

#### 返回

如果已登录微信则返回登录帐号的Scope

### getUserId

获取当前登录账号的用户ID

```
public java.lang.String getUserId()
```

#### 返回

如果已登录则返回登录帐号的用户ID

### getClientId

获取当前登录账号的Client ID

```
public java.lang.String getClientId(java.lang.String productId, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productId | 设备Product ID |
| dsn | 设备DSN |

#### 返回

如果已登录则返回登录帐号的Client ID

### getClientIdForThirdParty



```
public static java.lang.String getClientIdForThirdParty(java.lang.String openID, java.lang.String accessToken, java.lang.String refreshToken, java.lang.String productId, java.lang.String dsn)
```

### getAccountInfo



```
public TVSAccountInfo getAccountInfo()
```

### getInstance

获取该模块的单例。

```
public static AccountInfoManager getInstance()
```

#### 返回

该模块的单例

## TVSAccountInfo

代表获取到的账户认证信息的简单POJO类。

### TVSAccountInfo



```
public TVSAccountInfo()
```

### getPlatform

当前登录的平台。当且仅当代表未登录状态的时候返回null。

```
public ELoginPlatform getPlatform()
```

### setPlatform

设置当前登录的平台。

```
public void setPlatform(ELoginPlatform platform)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| platform | 当前登录的平台 |

### getAccessToken

获取Access Token。

```
public java.lang.String getAccessToken()
```

#### 返回

Access Token

### setAccessToken

设置Access Token。

```
public void setAccessToken(java.lang.String accessToken)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| accessToken | Access Token |

### getRefreshToken

获取Refresh Token。

```
public java.lang.String getRefreshToken()
```

#### 返回

Refresh Token

### setRefreshToken

设置Refresh Token。

```
public void setRefreshToken(java.lang.String refreshToken)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| refreshToken | Refresh Token |

### getOpenID

获取Open ID。

```
public java.lang.String getOpenID()
```

#### 返回

Open ID

### setOpenID

设置Open ID。

```
public void setOpenID(java.lang.String openID)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| openID | Open ID |

### getExpireTime

获取票据超时时间。

```
public long getExpireTime()
```

#### 返回

票据超时时间。

### setExpireTime

设置票据超时时间。

```
public void setExpireTime(long expireTime)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| expireTime | 票据超时时间 |

### getTvsID

获取TVS ID。

```
public java.lang.String getTvsID()
```

#### 返回

TVS ID

### setTvsID

设置TVS ID。

```
public void setTvsID(java.lang.String tvsID)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| tvsID | TVS ID |

### getAppId

获取当前登录平台的App ID。

```
public java.lang.String getAppId()
```

#### 返回

App ID

### setAppId

设置当前登录平台的App ID。

```
public void setAppId(java.lang.String appId)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| appId | App ID |

### getQbGuid



```
public java.lang.String getQbGuid()
```

### setQbGuid



```
public void setQbGuid(java.lang.String qbGuid)
```

### getUnionID

获取当前已登录微信平台时的Union ID。

```
public java.lang.String getUnionID()
```

#### 返回

Union ID

### setUnionID

设置当前已登录微信平台时的Union ID。

```
public void setUnionID(java.lang.String unionID)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| unionID | Union ID |

### getScope

获取当前已登录微信平台时的Scope。

```
public java.lang.String getScope()
```

#### 返回

Scope

### setScope

设置当前已登录微信平台时的Scope。

```
public void setScope(java.lang.String scope)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| scope | Scope |

## TVSCallback

通用的请求结果回调接口。成功时没有结果返回。

### onSuccess

接口调用成功。

```
void onSuccess()
```

### onError

接口调用失败。

```
void onError(int code)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| code | 失败错误码 |

## TVSSpeakerInfo

代表音箱信息的简单类。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| nickname | 音箱昵称。 |

### TVSSpeakerInfo



```
public TVSSpeakerInfo()
```

## TVSTTSConfig

代表TTS配置的简单POJO类。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| speed | 语音语速。 |
| volume | 语音音量。 |

### TVSTTSConfig



```
public TVSTTSConfig()
```

## TVSDevice

用来表示设备信息的简单类。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| productID | 后台申请的Product ID。 |
| dsn | 设备序列号。 |
| pushID | Push ID。 |
| pushIDExtra | Push ID Extra。 |
| guid | GUID。 |
| deviceID | 设备ID。 |
| deviceName | 设备名称。 |
| deviceType | 设备类型。 |
| deviceSerial | 设备系列。 |
| deviceOEM | 设备厂商。 |
| deviceOEMURL | 设备品牌标志URL。 |
| deviceOEMToken | 设备厂商Token（用于PushKit）。 |
| deviceRemark | 设备备注。 |
| imei | IMEI。 |
| qua | QUA。 |
| lc | LC。 |
| mac | MAC地址。 |
| qimei | QIMEI。 |
| enrollTime | 注册时间。 |
| bindTime | 绑定时间。 |
| bindType | 绑定类型。 |
| extra | 扩展信息。 |
| businessExtra | 业务扩展信息。 |
| accountInfo | 设备关联的账户信息。 |

### TVSDevice



```
public TVSDevice()
```

### toString



```
public java.lang.String toString()
```

## TVSDeviceBindType

设备绑定类型的枚举类型。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| SDK_APP | SDK 接入方案的 App |
| SDK_SPEAKER | SDK 接入方案的音箱 |
| TVS_APP | 云端 API 接入方案的 App |
| TVS_SPEAKER | 云端 API 接入方案的音箱 |

## QRCodeState

二维码扫描状态枚举类型。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| NOT_SCANNED | 未被扫描。 |
| SCANNED | 已经被扫描但没有后续操作。 |
| SCANNED_AND_CONFIRMED | 用户扫描后确认。 |
| SCANNED_AND_CANCELED | 用户扫描后取消。 |

### toInternalEnum



```
public static int toInternalEnum(QRCodeState qrCodeState)
```

### fromInternalEnum



```
public static QRCodeState fromInternalEnum(int bindingState)
```

## TVSAISpeechItem

代表音色信息的简单POJO类。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| speechID | 音色ID。 |
| speechName | 音色名称。 |
| speechEnum | 音色枚举类型。 |
| isDefaultOption | 是否是默认选项。 |
| ttsConfig | TTS配置。 |

### TVSAISpeechItem



```
public TVSAISpeechItem()
```

## TVSCallback1<R1>

通用的请求结果回调接口。成功时有1个结果返回。

### onSuccess

接口调用成功。

```
void onSuccess(R1 result)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| result | 返回的结果 |

### onError

接口调用失败。

```
void onError(int code)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| code | 失败错误码 |

## QRCodeInfo

当前查询到的二维码状态。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| qrCodeState | 二维码状态。 |
| accountInfo | 帐号信息。 |
| userInfo | 用户信息。 |

### QRCodeInfo



```
public QRCodeInfo()
```

## TVSCallback3<R1,R2,R3>

通用的请求结果回调接口。成功时有3个结果返回。

### onSuccess

接口调用成功。

```
void onSuccess(R1 result1, R2 result2, R3 result3)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| result1 | 第1个结果 |
| result2 | 第2个结果 |
| result3 | 第3个结果 |

### onError

接口调用失败。

```
void onError(int code)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| code | 失败错误码 |

## ErrCode

SDK预定义的错误码。 在这个错误码表中没有列举的错误是由TVS后台定义的错误，请查阅日志中的错误码、错误信息。

### 字段列表

| 名称 | 说明 |
|:---|:---|
| ERR_UNKNOWN | 未分类错误，请查阅日志。 |
| ERR_SDK_FAILED | 登录SDK错误。调用微信/QQ登录SDK返回错误，请查阅日志。 |
| ERR_TVS_FAILED | TVS后台返回的未分类错误，请查阅日志。 |
| ERR_USER_CANCEL | 用户取消了登录授权。 |
| ERR_LOGIN_REQUIRED | 未登录错误。请检查调用的接口是否在已经登录的前提下调用。 |
| ERR_MALFORMED_DATA | 数据格式错误。可能是传递的参数（JSON等限制格式的参数）的解析错误，也可能是解析TVS后台返回的数据时解析错误，请查阅日志。 |
| ERR_INVALID_PARAMETERS | 参数非法错误。请检查传递给API的参数是否合法，如是否传递null给限制非空的参数。 |
| ERR_QR_INVALID_URL | 二维码URL非法错误。请检查扫描到的是否是合法URL字符串。 |
| ERR_QR_INVALID_RESPONSE | 二维码响应错误。请检查URL是否是可用的URL。 |

## ELoginPlatform

登录平台。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| WX | 微信开放平台登录。 |
| QQOpen | QQ互联平台登录。 |
| ThirdParty | 第三方帐号。 |
| Unknown | 未知，仅备用。 |

## EUserAttrType

HTML5 WebView模块预设网页的枚举类型。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| HOMEPAGE | 用户个人中心。 |
| CPOPERATION | 会员领取页面。 |
| INFOSETTING | 手机号信息页面。 |
| AGREEMENT | 用户协议页面。 |
| PRIVACY | 隐私策略页面。 |
| FEEDBACK | 反馈页面。 |
| RECHARGE | 会员充值页面。 |
| TSKCENTER | TSKM中心页面。 |
| AGREEMENT_V1 | V1版本用户协议页面。 |
| AUTH | 账号授权页面。 |
| QQ_MUSIC | QQ音乐页面。 |
| IOT | 智能家居页面。 |
| TSKAUTHMGR | TSK授权管理页面 |

## ELoginEnv

登录环境，对整个SDK有效。不同的登录环境使用不同的TVS后台服务，开发时切换环境便于调试。 注意：HTML5 WebView模块内的预设网页没有体验环境，体验环境使用正式环境的URL。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| FORMAL | 正式环境（生产环境）。 |
| TEST | 测试环境。 |
| EX | 体验环境。 |

## TVSWebController

HTML5 WebView模块中TVSWebView对象的控制类，用于控制WebView的内容展示等。

### setDeviceInfo

已过时。 改用 setDeviceInfo(TVSDevice) 设置当前设备信息。

```
@Deprecated public void setDeviceInfo(TVSDeviceBindType deviceBindType, java.lang.String deviceType, java.lang.String deviceOEM, java.lang.String productID, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| deviceBindType | 设备绑定类型 |
| deviceType | 设备类型 |
| deviceOEM | 设备OEM |
| productID | 设备的Product ID |
| dsn | 设备的DSN |

### setDeviceInfo

设置当前设备信息。

```
public void setDeviceInfo(TVSDevice device)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| device | 设备信息 |

### loadURL

加载指定URL。

```
public void loadURL(java.lang.String url)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| url | 需要加载的URL |

### loadURL

API internal API.

```
public void loadURL(java.lang.String url, boolean withTVSToken)
```

### toPresetURL

打开预设页面。

```
public void toPresetURL(EUserAttrType type)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| type | 预设页面的类型，详见 EUserAttrType |

### goForward

前进到下一个网页。

```
public boolean goForward()
```

#### 返回

是否能否前进

### goBack

后退到前一个网页。

```
public boolean goBack()
```

#### 返回

是否能够后退

### stop

停止加载。

```
public void stop()
```

### reload

刷新（重新加载）当前URL。

```
public void reload()
```

### execJS

执行指定的JS函数。

```
public void execJS(java.lang.String funcName, java.lang.String funcParam, java.lang.String h5Id)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| funcName | 函数名 |
| funcParam | 函数参数，字符串类型，不需要转义，也不需要用引号扩起来 |
| h5Id | 该次请求的ID，不需要转义，也不需要用引号扩起来 |

### execJSV2

执行指定的JS函数。

```
public void execJSV2(java.lang.String funcName, int retCode, java.lang.String funcParam, java.lang.String h5Id)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| funcName | 函数名 |
| retCode | 请求码 |
| funcParam | 函数参数，字符串类型，不需要转义，也不需要用引号扩起来 |
| h5Id | 该次请求的ID，不需要转义，也不需要用引号扩起来 |

### onPickFileResult

用户完成文件选择后的回调。具体流程参考 TVSWebController.BusinessEventListener.onPickFile(Intent)。

```
public void onPickFileResult(int resultCode, android.content.Intent data)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| resultCode | 结果码 |
| data | 数据 |

### setUIEventListener

设置UI相关事件的回调。

```
public void setUIEventListener(TVSWebController.UIEventListener uiEventListener)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| uiEventListener | UI相关事件的回调 |

### setBusinessEventListener

设置业务相关事件的回调。

```
public void setBusinessEventListener(TVSWebController.BusinessEventListener businessEventListener)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| businessEventListener | 业务相关事件的回调 |

### onDestroy

在容器Activity的onDestroy中需要调用该方法，回收相关资源。

```
public void onDestroy()
```

## TVSWebView

WebView类，封装了TVS的业务逻辑，可以通过getController()获取控制类进行操作。

### TVSWebView



```
public TVSWebView(@NonNull android.content.Context context)
```

### TVSWebView



```
public TVSWebView(@NonNull android.content.Context context, @Nullable android.util.AttributeSet attrs)
```

### TVSWebView



```
public TVSWebView(@NonNull android.content.Context context, @Nullable android.util.AttributeSet attrs, int defStyleAttr)
```

### getController

获取用于控制当前WebView内容展示相关接口的控制类实例。

```
public TVSWebController getController()
```

#### 返回

控制类实例。

## TVSWeb

HTML5 WebView模块。
要集成本模块，需要在 Application.onCreate() 中初始化，详见 init(AuthDelegate)。

### init

初始化Web模块。必须在 Application.onCreate() 中的 LoginProxy.registerApp(Context, String, String) 之后调用。

```
public static void init(AuthDelegate authDelegate)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| authDelegate | 账号认证实现类实例，已经自行集成QQ、微信登录后需要集成TVS账号体系时传入自己的实现类实例，否则传入 TVSAuthDelegate 实例。 |

### getAuthDelegate

获取账号认证实现类实例。

```
public static AuthDelegate getAuthDelegate()
```

#### 返回

账号认证实现类实例

### getConfiguration

获取Web模块配置实例。

```
public static TVSWeb.Configuration getConfiguration()
```

#### 返回

Web模块配置实例

## TVSQRCode

二维码业务模块。

### newInstance

构造用于单个设备的实例，该实例的API调用都作用于该设备。

```
public static TVSQRCode newInstance(java.lang.String productID, java.lang.String dsn, QRCodeType qrCodeType)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| qrCodeType | 二维码业务类型 |

#### 返回

指定单个设备的 TVSQRCode 实例

### getQRCodeInfo

查询二维码状态信息。

```
public void getQRCodeInfo(TVSCallback1<QRCodeInfo> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调，成功时返回的参数包含二维码状态和帐号信息、用户信息 |

### setQRCodeState

更新二维码状态。

```
public void setQRCodeState(QRCodeState qrCodeState, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| qrCodeState | 新的二维码状态 |
| callback | 请求结果回调 |

### queryQRScanResult

通过二维码扫描结果，查询相关信息。

```
public static void queryQRScanResult(java.lang.String scanResult, TVSCallback1<java.lang.String> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| scanResult | 二维码扫描结果 |
| callback | 请求结果回调，成功时返回JSON格式的请求结果 |

## QRCodeType



### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| UNKNOWN |  |
| BIND |  |
| JOIN_RELATIONSHIP |  |

### toInternalEnum



```
public static java.lang.String toInternalEnum(QRCodeType qrCodeType)
```

## TVSSpeaker

音箱信息设置模块。

### newInstance

构造用于单个设备的实例，该实例的API调用都作用于该设备。

```
public static TVSSpeaker newInstance(java.lang.String productID, java.lang.String dsn)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |

#### 返回

指定单个设备的 TVSSpeaker 实例

### getSpeakerInfo

获取音箱信息。

```
public void getSpeakerInfo(TVSCallback1<TVSSpeakerInfo> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| callback | 请求结果回调，成功时返回的 TVSSpeakerInfo 参数包含音箱信息 |

### setSpeakerInfo

设置音箱信息。

```
public void setSpeakerInfo(TVSSpeakerInfo speakerInfo, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| speakerInfo | 要设置为的音箱信息 |
| callback | 请求结果回调 |

## MiniProgCallback



### onReceiveExtMsg



```
void onReceiveExtMsg(java.lang.String msg)
```

## EMiniProgType

小程序类型。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| RELEASE | 正式版。 |
| TEST | 测试（开发）版。 |
| PREVIEW | 预览版。 |

## TVSMember

会员模块。

### getDeviceStatus

获取设备QQ音乐会员领取状态。

```
public static void getDeviceStatus(java.lang.String productID, java.lang.String dsn, TVSCallback3<java.lang.Boolean,java.lang.Integer,DateUnit> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| callback | 请求结果回调，成功时三个参数依次表示是否能够领取会员、能够领取的时长的数值、能够领取的时长的单位 |

### getMemberStatus

获取QQ会员领取状态。

```
public static void getMemberStatus(java.lang.String productID, java.lang.String dsn, TVSCallback3<java.lang.Boolean,java.util.Date,java.util.Date> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| callback | 请求结果回调 |

## DateUnit

时间单位枚举类型，用于会员相关接口的回调。

### 枚举值列表

| 名称 | 说明 |
|:---|:---|
| MONTH | 一个月。 |
| YEAR | 一年。 |

## TVSAISpeech

AI Speech模块。

### getBotAISpeechOption

获取当前ProductID下可支持的TTS音色列表

```
public static void getBotAISpeechOption(java.lang.String productID, TVSCallback1<java.util.ArrayList<TVSAISpeechItem>> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| callback | 请求结果回调，成功时参数返回为该设备的BOT支持的音色列表 |

### getDeviceAISpeech

获取当前ProductID下音色信息

```
public static void getDeviceAISpeech(java.lang.String productID, java.lang.String dsn, TVSCallback1<TVSAISpeechItem> callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| callback | 请求结果回调，成功时参数返回为该设备的音色信息 |

### setDeviceAISpeech

设置当前ProductID下的TTS

```
public static void setDeviceAISpeech(java.lang.String productID, java.lang.String dsn, java.lang.String speechID, @Nullable TVSTTSConfig ttsConfig, TVSCallback callback)
```

#### 参数

| 名称 | 说明 |
|:---|:---|
| productID | 设备的Product ID |
| dsn | 设备的DSN |
| speechID | 要设置为的音色ID |
| ttsConfig | 要设置为的TTS音量和语速，传null则表示不设置 |
| callback | 请求结果回调 |

