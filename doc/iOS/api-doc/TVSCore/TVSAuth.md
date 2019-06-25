## 账号管理 <TVSCore/TVSAuth.h>

### 常量

| 名称 | 类型 | 描述 |
| ------ | ------ | ------ |
| TVSInvalidClientId | NSString* | 非法的 ClientId |
| TVSInvalidRefreshToken | NSString* | 非法的 RefreshToken |

### TVSAccountType 账号登录类型枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSAccountTypeQQNumber | QQ 号 |
| TVSAccountTypeQQOpenId | QQ 登录  |
| TVSAccountTypeWechatOpenId | 微信登录 |
| TVSAccountTypePhoneNumber | 手机号 |
| TVSAccountTypeQbId | QQ 浏览器 id |
| TVSAccountTypeIOSVistor | iOS 访客 |
| TVSAccountTypeUnknown | 未知类型 |

### TVSAuthResult 账号授权结果枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSAuthResultSuccess | 授权成功 |
| TVSAuthResultFailedNetwork | 授权失败（网络原因） |
| TVSAuthResultFailedNotLogin | 授权失败（未登录） |
| TVSAuthResultFailedOther | 授权失败（其他原因） |

### TVSAccountInfo 账号信息类

#### 成员

| 名称 | 类型 | 描述 |
| ------ | ------ | ------ |
| accountType | TVSAccountType | 账号类型 |
| appId | NSString* | QQ/微信开放平台申请的 appId |
| openId | NSString* | QQ/微信登录返回的 openId |
| accessToken | NSString* | QQ/微信登录返回的 accessToken |
| refreshToken | NSString* | 微信用于刷票的 refreshToken |
| unionId | NSString* | 微信登录返回的 unionId |
| expireTime | NSInteger | QQ/微信 token 过期时间 |
| tvsId | NSString* | TVS 平台返回的账号 id |
| userId | NSString* | TVS 平台返回的 userId |

#### 方法

##### `+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn;`

  **描述**:

  获取 TVS 平台 ClientId；（适用于使用本 SDK 登录的场景）
  必须确保已登录！

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | productId | NSString* | TVS 后台申请的 appid/accessToken 信息 | 是 |
  | dsn | NSString* | 设备序列号 | 是 |

  **返回**：

  TVS 平台 ClientId;

##### `+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn accountId:(NSString*)accountId;`

  **描述**:

  获取 TVS 平台 ClientId；（适用于自己做登录的场景）
  其中 productId、dsn、openId 必传！！

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | productId | NSString* | TVS 后台申请的 appid/accessToken 信息 | 是 |
  | dsn | NSString* | 设备序列号 | 是 |
  | accountId | NSString* | 账号唯一ID | 是 |

  **返回**：

  TVS 平台 ClientId;

##### `+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn openId:(NSString*)openId accessToken:(NSString*)accessToken refreshToken:(NSString*)refreshToken;`

  **描述**:

  获取 TVS 平台 ClientId；（适用于自己做登录的场景）
  其中 productId、dsn、openId 必传！！

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | productId | NSString* | TVS 后台申请的 appid/accessToken 信息 | 是 |
  | dsn | NSString* | 设备序列号 | 是 |
  | openId | NSString* | QQ/微信 openid | 是 |
  | accessToken | NSString* |  | 否 |
  | refreshToken | NSString* |  | 否 |

  **返回**：

  TVS 平台 ClientId;

##### `-(instancetype)initWithAccountId:(NSString*)accountId;`

  **描述**:

  通过账号 ID 生成账号信息；
  仅适用于自己实现账号登录且没有token的场景！！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | accountId | NSString* | 账号唯一ID，比如手机号 | 是 |

  **返回**:

  TVSAccountInfo 实例；

### TVSUserInfo 用户资料信息类

#### 成员

| 名称 | 类型 | 描述 |
| ------ | ------ | ------ |
| idType | TVSAccountType | 账号类型 |
| nickName | NSString* | 昵称 |
| headImgUrl |  NSString*| 头像 url |
| sex | NSInteger | 性别（0：女；1：男） |
| phoneNumber | NSString* | 手机号 |

#### 方法

  无；

### TVSAuthDelegate 账号授权协议

#### 方法

##### `-(void)TVSAuthWXLoginWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  微信登录实现；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | 结果回调 |

  **返回**:

  无；

##### `-(void)TVSAuthWXTokenRefreshWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  微信刷票实现；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | 结果回调 |

  **返回**:

  无；

##### `-(void)TVSAuthQQLoginWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  QQ 登录实现；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | 结果回调 |

  **返回**:

  无；

##### `-(void)TVSAuthQQTokenVerifyWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  QQ 验票实现；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | 结果回调 |

  **返回**:

  无；

##### `-(void)TVSAuthLogout;`

  **描述**:

  账号注销实现；

  **参数**:

  无；

  **返回**:

  无；

##### `-(TVSAccountInfo*)TVSAuthGetAccountInfo;`

  **描述**:

  获取账号信息；

  **参数**:

  无；

  **返回**:

  | 类型 | 描述 |
  | ------ | ------ |
  | TVSAccountInfo* | 账号信息 |

##### `TVSAuthGetUser;`

  **描述**:

  获取用户资料信息；

  **参数**:

  无；

  **返回**:

  | 类型 | 描述 |
  | ------ | ------ |
  | TVSUserInfo* | 用户资料信息 |

##### `TVSAuthSetPhoneNumber:(NSString*)phoneNumber;`

  **描述**:

  保存手机号；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | phoneNumber | NSString* | 手机号 |

  **返回**:

  无；

### TVSAuthManager 账号授权管理类

#### 成员

  | 名称 | 类型 | 描述 | 备注 |
  | ------ | ------ | ------ | ------ |
  | accountInfo | TVSAccountInfo* | 账号信息 | 如果不使用本 SDK 做账号授权，必须手动注入账号信息才能使用某些模块（比如设备绑定等）!! |
  | userInfo | TVSUserInfo* | 设备信息 |  |

#### 方法

##### `+(instancetype)shared;`

  **描述**:

  获得 TVSAccount 类实例对象；

  **参数**:

  无；

  **返回**:
 
  TVSAccount 类实例；

##### `-(void)registerApp;`

  **描述**:

  初始化 (自动从 Info.plist 读取相关参数)；
  必须在 `AppDelegate` 的 `application:didFinishLaunchingWithOptions:` 方法中调用！

  **参数**:

  无；

  **返回**:

  无；
  
##### `-(BOOL)handleOpenUrl:(NSURL*)url;`

  **描述**:

  处理 URL 跳转；
  必须在 `AppDelegate` 的 `application:openURL:options:` 方法中调用!!

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | url | NSURL* | 跳转链接 | 是 |

  **返回**:

  是否成功处理 URL 跳转；
  
##### `-(BOOL)isWXTokenExist;`

  **描述**:

  检查微信 token 是否存在；

  **参数**:

  无；

  **返回**:
  
  是否存在；
  
##### `-(BOOL)isQQTokenExist;`

  **描述**:

  检查 QQ token 是否存在；

  **参数**:

  无；

  **返回**:
 
  是否存在；

##### `-(BOOL)checkWXAppWithAlert:(BOOL)alert;`

  **描述**:

  检测微信是否安装，版本是否支持；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | alert | BOOL | 是否弹窗提示 | 是 |

  **返回**:

  安装微信且版本支持返回 `YES`;
  
##### `-(void)wxLoginWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  微信登录；
  如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | 回调，TVSAuthResult 表示授权结果 | 是 |

  **返回**:

  无；
  
##### `-(void)wxLoginWithViewController:(UIViewController*)vc handler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  微信登录(支持未安装微信的情况，会跳转 H5 短信登录)；
  如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | viewController | UIViewController* | 发起微信登录的页面 | 是 |
  | handler | void(^)(TVSAuthResult) | 回调，TVSAuthResult 表示授权结果 | 是 |

  **返回**:

  无；

##### `-(void)wxTokenRefreshWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  刷新微信 token；
  如果微信 token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | TVSAuthResult 表示授权结果 | 是 |

  **返回**:

  无；
  
##### `-(void)qqLoginWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  QQ 登录；
  如果 QQ token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | TVSAuthResult 表示授权结果 | 是 |

  **返回**:

  无；
  
##### `-(void)qqTokenVerifyWithHandler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  验证 QQ token;
  如果 QQ token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息!

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | handler | void(^)(TVSAuthResult) | TVSAuthResult 表示授权结果 | 是 |

  **返回**:

  无；
  
##### `-(void)tvsAuthWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken accountType:(TVSAccountType)accountType handler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  通过微信/QQ 账号信息直接到 TVS 后台授权（换取tvsId）；
  仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | openId | NSString* | QQ/微信登录返回的 openId | 是 |
  | accessToken | NSString* | QQ/微信登录返回的 accessToken | 是 |
  | accountType | TVSAccountType | 账号类型 | 是 |
  | handler | void(^)(TVSAuthResult) | TVSAuthResult 表示授权结果，成功后即可通过 TVSAccountInfo 读取 tvsId | 是 |

  **返回**:

  无；
  
##### `-(void)verifyQQTokenWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken handler:(void(^)(TVSAuthResult))handler;`

  **描述**:

  验证 QQ token；
  仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | openId | NSString* | QQ/微信登录返回的 openId | 是 |
  | accessToken | NSString* | QQ/微信登录返回的 accessToken | 是 |
  | handler | void(^)(TVSAuthResult) | TVSAuthResult 表示授权结果，成功后即可通过 TVSAccountInfo 读取 tvsId | 是 |

  **返回**:

  无；

  **返回**:

  TVSUserInfo* 用户资料信息；
  
##### `-(void)logout;`

  **描述**:

  注销登录（清除本地保存的账户信息）；

  **参数**:

  无；

  **返回**:
 
  无；
  
##### `-(void)getCaptchaWithPhoneNumber:(NSString*)phoneNumber handler:(void(^)(BOOL))handler;`

  **描述**:

  获取用于绑定手机号的验证码；
  必须确保已登录！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | phoneNumber | NSString* | 手机号 | 是 |
  | handler | void(^)(BOOL) | 回调，BOOL 表示验证码是否发送成功 | 是 |

  **返回**:

  无；
  
##### `-(void)bindPhoneNumber:(NSString*)phoneNumber captcha:(NSString*)captcha handler:(void(^)(BOOL))handler;`

  **描述**:

  绑定手机号；
  必须确保已登录！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | phoneNumber | NSString* | 手机号 | 是 |
  | captcha | NSString* | 验证码 | 是 |
  | handler | void(^)(BOOL) | 回调，BOOL 表示是否绑定成功 | 是 |

  **返回**:

  否；
  
##### `-(void)syncUserInfo:(TVSUserInfo*)userInfo handler:(void(^)(BOOL))handler;`

  **描述**:

  保存用户信息到 TVS 后台(暂时只支持头像昵称)；
  必须确保已登录！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | userInfo | TVSUserInfo* | 用户信息 | 是 |
  | handler | void(^)(BOOL) | 回调，BOOL 表示是否设置成功 | 是 |

  **返回**:

  无；

  ##### `-(void)queryUserInfoWithOpenId:(NSString*)openId handler:(void(^)(TVSUserInfo*))handler;`

  **描述**:

  通过 openId 查询用户信息(暂时只支持头像昵称);

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | openId | NSString* | 用户 OpenId | 是 |
  | handler | void(^)(TVSUserInfo*) | 回调，用于接收返回的用户信息 | 是 |

  **返回**:

  无；