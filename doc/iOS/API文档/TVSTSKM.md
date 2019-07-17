# TVSTSKM

## 技能服务 <TVSTSKM/TVSTSKMProxy.h>

### TVSTSKMProxy 技能服务访问代理类

#### 成员

 无；

#### 方法

##### `-(instancetype)initWithDeviceInfo:(TVSDeviceInfo*)deviceInfo;`

  **描述**:

  实例化（适用于QQ/微信登录）；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 | 备注 |
  | ------ | ------ | ------ | ------ | ------ |
  | deviceInfo | TVSDeviceInfo* | 设备信息 | 是 | 其中 productId 必填，dsn 和 guid 二选一 |

  **返回**:

  TVSTSKMProxy 实例；

## 闹钟管理 <TVSTSKM/TVSAlarmReminder.h>

### TVSAlarmReminder 类

#### TVSAlarmReminderOperation 枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSAlarmReminderOperationManage | 管理 |
| TVSAlarmReminderOperationSyncData | 同步数据 |
| TVSAlarmReminderOperationClearData | 清数据 |

#### 成员

 无；

#### 方法

##### `-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy;`

  **描述**:

  初始化方法；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | tskmProxy | TVSTSKMProxy* | 技能服务访问代理 | 是 |

  **返回**:

  TVSAlarmManager 类实例；

##### `-(void)alarmOperation:(TVSAlarmReminderOperation)op blob:(NSDictionary*)blob handler:(TVSTSKMCallback)handler;`

  **描述**:

  管理闹钟；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | op | TVSAlarmReminderOperation | 操作 | 是 |
  | blob | NSDictionary* | 请求数据 | 是 |
  | handler | TVSTSKMCallback | 回调 | 是 |

  **返回**:

  无；

##### `-(void)reminderOperation:(TVSAlarmReminderOperation)op blob:(NSDictionary*)blob handler:(TVSTSKMCallback)handler;`

  **描述**:

  管理提醒；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | op | TVSAlarmReminderOperation | 操作 | 是 |
  | blob | NSDictionary* | 请求数据 | 是 |
  | handler | TVSTSKMCallback | 回调 | 是 |

  **返回**:

  无；

## 儿童模式 <TVSTSKM/TVSChildMode.h>

### TVSChildMode 类

#### 成员

 无；

#### 方法

##### `-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy;`

  **描述**:

  初始化方法；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | tskmProxy | TVSTSKMProxy* | 技能服务访问代理 | 是 |

  **返回**:

  TVSChildMode 类实例；

##### `-(void)setConfigWithJsonBlob:(NSDictionary*)jsonBlob handler:(TVSTSKMCallback)handler;`

  **描述**:

  保存儿童模式配置；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | jsonBlob | NSDictionary* | 请求数据 | 是 |
  | handler | TVSTSKMCallback | 回调 | 是 |

  **返回**:

  无；

##### `-(void)getConfigWithJsonBlob:(NSDictionary*)jsonBlob handler:(TVSTSKMCallback)handler;`

  **描述**:

  获取儿童模式配置；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | jsonBlob | NSDictionary* | 请求数据 | 是 |
  | handler | TVSTSKMCallback | 回调 | 是 |

  **返回**:

  无；

## 第三方授权 <TVSTSKM/TVSThirdPartyAuth.h>

### TVSThirdPartyAuth 类

#### 成员

 无；

#### 方法

##### `+(void)gotoAuthWithAccountInfo:(nullable TVSAccountInfo*)accountInfo deviceInfo:(TVSDeviceInfo*)deviceInfo handler:(void(^)(BOOL))handler;`

  **描述**:

  跳转到云叮当 APP 进行第三方授权;

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 | 备注 |
  | ------ | ------ | ------ | ------ | ------ |
  | accountInfo | TVSAccountInfo* | 账号信息 | 使用本 SDK 做账号登录的传 nil |  |
  | deviceInfo | TVSDeviceInfo* | 设备信息 | 是 | 其中 productId、dsn、guid 必填！！ |
  | handler | void(^)(BOOL) | 回调，BOOL 表示是否成功 | 是 |  |

  **返回**:

  无；

##### `-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy deviceInfo:(TVSDeviceInfo*)deviceInfo;`

  **描述**:

  实例化方法;

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 | 备注 |
  | ------ | ------ | ------ | ------ | ------ |
  | tskmProxy | TVSTSKMProxy* | 账号信息 | 是 |
  | deviceinfo | TVSDeviceInfo* | 设备信息 | 是 |

  **返回**:

  TVSThirdPartyAuth 实例；

 ##### `-(void)getBindedAccountInfoWithHandler:(void(^)(TVSAccountInfo*))handler;`

  **描述**:

  查询绑定的账号信息;

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | handler | void(^)(TVSAccountInfo*) | 回调，参数为账号信息 | 是 |

  **返回**:

  无； 

##### `-(void)unbindWithAccountInfo:(TVSAccountInfo*)accountInfo handler:(void(^)(BOOL))handler;`

  **描述**:

  解绑账号信息;

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | accountInfo | TVSAccountInfo* | 账号信息 | 是 |
  | handler | void(^)(BOOL) | 回调，参数为是否成功 | 是 |

  **返回**:

  无； 

## 设备控制 <TVSTSKM/TVSDeviceControl.h>

### TVSDeviceControl 类

#### 成员

 无；

#### 方法

##### `-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy;`

  **描述**:

  初始化方法；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | tskmProxy | TVSTSKMProxy* | 技能服务访问代理 | 是 |

  **返回**:

  TVSDeviceControl 类实例；

##### `-(NSString *)controlDeviceWithNamespace:(NSString *)nameSpace name:(NSString *)name payload:(NSDictionary *)palyload handler:(TVSTSKMCallback)handler;`

  **描述**:

  设备控制；
  必须调用设备绑定后执行!! messageId 字段内部会自动生成，并返回，无需自行传入！！

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | namespace | NSString* | 操作域 | 是 |
  | name | NSString* | 操作指令 | 是 |
  | payload | NSDictionary* | 操作参数 | 是 |
  | handler | TVSTSKMCallback | 回调 | 是 |

  **返回**:

  操作唯一串；
