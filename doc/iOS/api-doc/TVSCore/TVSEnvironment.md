## 环境变量 <TVSCore/TVSEnvironment.h>

### TVSServerEnv 后台环境枚举

| 名称 | 描述 |
| ------ | ------ |
| TVSServerEnvFormal | 正式环境（默认） |
| TVSServerEnvExplore | 体验环境（灰度） |
| TVSServerEnvTest | 测试环境 |

### TVSEnvironment 环境变量类

#### 成员

 无；

#### 方法

##### `+(instancetype)shared;`

  **描述**:

  获取 TVSEnvironment 实例对象；

  **参数**:

  无;

  **返回**:

  TVSEnvironment 实例；

##### `-(void)enableLog;`

  **描述**:

  开启日志；

  **参数**:

  无；

  **返回**:

  无；

##### `-(TVSServerEnv)serverEnv;`

  **描述**:

  读取 TVS 后台环境;

  **参数**:

  无；

  **返回**:

  TVS 后台环境；

##### `-(void)setServerEnv:(TVSServerEnv)env;`

  **描述**:

  设置 TVS 后台环境；

  **参数**:

  | 名称 | 类型 | 描述 | 是否必填 |
  | ------ | ------ | ------ | ------ |
  | env | TVSServerEnv | TVS 后台环境 | 是 |

  **返回**:

  无；

##### `-(NSString*)sdkVersion;`

  **描述**:

  获取 SDK 版本；

  **参数**:

  无；

  **返回**:

  SDK 版本；