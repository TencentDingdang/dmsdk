## Web 页面 <TVSWeb/TVSWeb.h>

### TVSWebPageType Web 页面类型枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSWebPageTypeSmartHome | 智能家居页面 |
| TVSWebPageTypeMusic | QQ 音乐页面 |
| TVSWebPageTypeThirdPartyAuth | 第三方账号授权页面 |
| TVSWebPageTypeAuth | 授权页面 |
| TVSWebPageTypeMember | 会员个人中心页面 |
| TVSWebPageTypeVIP | 会员领取页面 |
| TVSWebPageTypeRecharge | 会员充值页面 |
| TVSWebPageTypePhoneAddress | 手机号地址页面 |
| TVSWebPageTypeFeedback | 用户反馈页面 |

### TVSWebUniversalDelegate Web 页面通用回调协议

##### `-(void)TVSWebLoadStart;`

  **描述**:

  Web 页面加载开始；

  **参数**:

  无;

  **返回**:

  无;

##### `-(void)TVSWebLoadProgress:(double)progress;`

  **描述**:

  Web 页面加载进度更新；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | progress | double | 页面加载进度 |

  **返回**:

  无;

##### `-(void)TVSWebLoadStop;`

  **描述**:

  Web 页面加载开始；

  **参数**:

  无;

  **返回**:

  无;

##### `-(void)TVSWebLoadError:(NSError*)error;`

  **描述**:

  Web 页面加载错误；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | error | NSError* | 错误 |

  **返回**:

  无;

##### `-(void)TVSWebGotTitle:(NSString*)title;`

  **描述**:

  Web 页面拉取到网页标题；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | title | NSString* | 网页标题 |

  **返回**:

  无;

##### `-(BOOL)TVSWebShouldOpenScheme:(NSString*)scheme;`

  **描述**:

  Web 页面是否允许打开指定 scheme 的链接；
  默认只允许打开 http、https、wexin、wtloginm、itms、itms-apps、dingdang 等常规 scheme；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | scheme | NSString* | scheme |

  **返回**:

  是否允许打开；

##### `-(BOOL)TVSWebShouldLoadUrl:(NSString*)url;`

  **描述**:

  Web 页面是否允许加载指定链接；
  默认都允许加载；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | url | NSString* | 网页链接 |

  **返回**:

  是否允许加载；

### TVSWebBusinessDelegate Web 页面业务回调协议

##### `-(void)TVSWebQQLoginResult:(TVSAuthResult)result;`

  **描述**:

  Web 页面 QQ 登录回调；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | result | TVSAuthResult | QQ 登录结果 |

  **返回**:

  无;

##### `-(void)TVSWebWXLoginResult:(TVSAuthResult)result;`

  **描述**:

  Web 页面微信登录回调；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | result | TVSAuthResult | 微信登录结果 |

  **返回**:

  无;

##### `-(void)TVSWebVerifyQQTokenResult:(TVSAuthResult)result;`

  **描述**:

  Web 页面 QQ 验票回调；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | result | TVSAuthResult | QQ 验票是否成功 |

  **返回**:

  无;

##### `-(void)TVSWebRefreshWXTokenResult:(TVSAuthResult)result;`

  **描述**:

  Web 页面微信刷票回调；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | result | TVSAuthResult | 微信刷票是否成功 |

  **返回**:

  无;

##### `-(void)TVSWebRequestExit;`

  **描述**:

  Web 请求关闭当前页面；

  **参数**:

  无;

  **返回**:

  无;

##### `-(void)TVSWebProxyData:(NSDictionary*)data;`

  **描述**:

  Web 透传参数；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | data | NSDictionary* | 透传的参数字典 |

  **返回**:

  无;

-(void)TVSWebReceivedJSMessage:(NSString*)msg data:(id)data;`

  **描述**:

  Web 页面收到 JS 消息；

  **参数**:

  | 名称 | 类型 | 描述 |
  | ------ | ------ | ------ |
  | msg | NSDictionary* | JS 消息名 |
  | data | id | JS 消息数据 |

  **返回**:

  无;

### TVSWebView TVS Web 组件

#### 成员

  | 名称 | 类型 | 描述 | 备注 |
  | ------ | ------ | ------ | ------ |
  | webUniversalDelegate | TVSWebUniversalDelegate | Web 页面通用回调 |  |
  | webBusinessDelegate | TVSWebBusinessDelegate | Web 页面业务回调 |  |
  | authDelegate | TVSAuthDelegate | 账号授权协议 | 如果不使用 SDK 里面的 TVSAuthManager 授权，而是自己调用微信/QQ SDK，则必须实现此协议！ |
  | device | TVSDeviceInfo* | 设备信息 | QQ 音乐会员等页面需要此参数，其中 deviceBindType、deviceType、deviceOEM、productId、DSN 几个字段为必填! |
  | autoCheckDeviceBind | BOOL | 是否自动检查设备绑定 | LinkPlay 设备打开个人中心需要检查绑定 |
  | showDebugTool | BOOL | 是否展示网页调试工具 |  |

#### 方法

##### `-(instancetype)initWithFrame:(CGRect)frame;`

  **描述**:

  实例化 web 组件；

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | frame | CGRect | 组件位置和大小 | 是 |

  **返回**：

  Web 组件实例；

##### `-(void)loadPage:(TVSWebPageType)pageType;`

  **描述**：

  打开指定类型的 Web 页面；

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | pageType | TVSWebPageType | H5 页面类型 | 是 |

  **返回**：

  无；

##### `-(void)loadUrl:(NSString*)url;`

  **描述**：

  打开指定链接的 Web 页面；

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | url | NSString* | Web 页面链接 | 是 |

  **返回**：

  无；

##### `-(BOOL)canGoBack;`

  **描述**：

  能否回退网页；

  **参数**：

  无;

  **返回**：

  能否；

##### `-(BOOL)canGoForward;`

  **描述**：

  能否前进网页；

  **参数**：

  无;

  **返回**：

  能否；

##### `-(BOOL)goBack;`

  **描述**：

  回退网页；

  **参数**：

  无;

  **返回**：

  是否成功；

##### `-(BOOL)goForward;`

  **描述**：

  前进网页；

  **参数**：

  无;

  **返回**：

  是否成功；

##### `-(void)reload;`

  **描述**：

  刷新网页；

  **参数**：

  无;

  **返回**：

  无；

##### `-(void)stopLoading;`

  **描述**：

  停止加载网页；

  **参数**：

  无;

  **返回**：

  无；

##### `-(UIScrollView*)scrollView;`

  **描述**：

  获取 UIScrollView 实例；

  **参数**：

  无;

  **返回**：

  UIScrollView 实例；

##### `-(void)runJSCode:(NSString*)code handler:(void(^)(BOOL))handler;`

  **描述**：

  执行 JS 代码；

  **参数**：

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | code | NSString* | JS 代码 | 是 |
  | handler | void(^)(BOOL) | 回调，BOOL 表示是否执行成功 | 是 |

  **返回**：

  无；
