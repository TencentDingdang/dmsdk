# TVSMember

## 会员 <TVSMember/TVSMember.h>

### TVSMemberType 会员类型枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSMemmerTypeQQMusic | QQ 音乐会员 |

### TVSMemberUnit 会员时长单位枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSMemberUnitYear | 年 |
| TVSMemberUnitMonth | 月 |

### TVSMember 会员类

#### 成员

  无；

#### 方法

##### `-(instancetype)initWithDeviceProductId:(NSString*)productId dsn:(NSString*)dsn;`

  **描述**：

  返回 TVSMember 实例对象；

  **参数**：

| 名称 | 类型 | 描述 | 是否必填 |
| ------ | ------ | ------ | ------ |
| productId | NSString* | 设备 productId | 是 |
| dsn | NSString* | 设备序列号 | 是 |

  **返回**：

  TVSMember 类实例；

##### `-(void)queryDeviceStatusWithType:(TVSMemberType)type handler:(void(^)(BOOL,NSInteger,TVSMemberUnit))handler;`

  **描述**：

  查询设备是否领取过会员；
  必须确保已登录！

  **参数**：

| 名称 | 类型 | 描述 | 是否必填 |
| ------ | ------ | ------ | ------ |
| type | TVSMemberType | 会员类型 | 是 |
| handler | void(^)(BOOL,NSInteger,TVSMemberUnit | 回调，BOOL 值表示是否可以领取会员，NSInteger 和 TVSMemberUnit 分别表示可以领取的会员时长数量和单位 | 是 |

  **返回**：

  无；

##### `-(void)queryMemberStatusWithType:(TVSMemberType)type handler:(void(^)(BOOL,NSDate*,NSDate*))handler;`

  **描述**：

  查询会员状态；
  必须确保已登录！

  **参数**：

| 名称 | 类型 | 描述 | 是否必填 |
| ------ | ------ | ------ | ------ |
| type | TVSMemberType | 会员类型 | 是 |
| handler | (void(^)(BOOL,NSDate*,NSDate*) | 回调，BOOL 值表示是否是否会员，两个 NSDate 分别表示会员开始时间、会员过期时间 | 是 |

  **返回**：

  无；
