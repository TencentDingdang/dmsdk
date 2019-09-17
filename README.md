# 腾讯叮当手机端SDK

| 文件夹  | 说明 |
| -------- | --------- |
| demo | 手机端 SDK 的 demo 参考 |
| doc | 手机端 SDK 文档 |
| sdk | 手机端 SDK |

## 更新日志：

[Android](#Android) (Latest: v2.2.0)

[iOS](#iOS) (Latest: v2.1.2)

**v1.0 到 v2.0.0+ API 变更较大，请阅读升级指南([Android 版][5] | [iOS 版][4])。**

### Android

#### v2.2.0

*   各模块提高网络请求稳定性
*   各模块移除无用资源，减小体积
*   Core模块修复LoadedApk被混淆导致在三星S9上崩溃的问题
*   原Web模块修复debug开关实现错误问题
*   原Web模块修复部分预设页面体验环境域名错误的问题
*   原Web模块修复WebView潜在的内存泄漏问题
*   原Web模块增加QQ验票的JS接口，并修复Cookie未设置问题
*   原Web模块支持离线UI模版
*   原Web模块独立为WebView SDK

#### v2.1.4

*   Core模块修复第三方账号方案时绑定关系和ClientId潜在冲突的问题；
*   AISpeech、Member、Speaker、TSKM、Core等模块的接口支持第三方账号。

#### v2.1.3

**从该版本开始，DMSDK不再提供AAR文件，您只需参考接入指南直接从Maven Central或JCenter获取依赖。**

*   Core模块优化为不强制依赖微信和QQ登录的SDK，需要微信和QQ登录时需要额外配置，参考迁移指南；
*   Core模块修复若干QQ登录失败的问题；
*   Web模块修复若干录音相关的问题；
*   Web模块新增离线缓存加载控制；
*   Web模块新增调试开关；
*   TSKM模块API变更：reqQueryThirdPartAcctBindOp改为reqQueryThirdPartyAcctBindOp；
*   若干其他优化与问题修复。

#### v2.1.2

*   Web模块修复体验环境无法获得登录态的问题；
*   TSKM模块的多端互动API发送请求后会返回请求ID；
*   TSKM模块云叮当授权API优化；
*   Core模块修复QQ登录回调随机性不被调用的问题；
*   SDK的请求超时时间缩短为10s；
*   SDK修改了生成的R文件的报名避免与旧版本腾讯IMSDK冲突；
*   移除无用资源，减小SDK体积。

#### v2.1.1

该版本是测试版本，因此仅提供更新内容和迁移指南，不提供该版本的SDK。

*   完善了TSKM模块对第三方账号的支持；
*   完善了云叮当授权的API；
*   修复了Web模块停止录音时崩溃及execJS、execJSV2调用参数错误的问题；
*   修复了Core模块QQ登录ClientID不正确的问题。

#### v2.1.0

*   移除了所有兼容1.0版本的废弃接口，请参考2.0.0版本的迁移指南迁移。如果您是直接从2.0.0及以后版本全新接入的则没有影响；
*   TSKM模块新增了针对各个业务的类用于发送相应的请求，包括闹钟、提醒、多端互动、儿童模式控制；
*   TSKM和Web模块支持第三方云叮当授权；
*   Core模块新增根据GUID查询设备信息接口。

#### v2.0.2

*   修复Web模块的ProxyData回调收到的JSONObject结构，保持与iOS端DMSDK的结果一致；
*   修复了二维码模块一个Crash；
*   修复了TAuthActivity的QQOpenAppID配置错误导致可能无法QQ登录的问题，该问题的具体影响和解决方案请见迁移文档。

#### v2.0.1

* H5 模块新增链接加载拦截回调；

* H5 模块新增智能家居页面；

* Core 模块新增接口重复登录前是否登出；

* Core 模块设备绑定相关接口修复不能获取设备businessExtra信息的问题。

#### v2.0.0

* 模块化拆分，核心模块(环境配置/账号/设备绑定)为必须，H5 等模块根据需要可选；

* H5 模块由 `TVSAssistActivity` 页面改为 `WebView` 组件形式，可定制化程度更高；

* H5 模块支持自己实现账号授权（不调用此 SDK 做登录和刷票）；

* 新增二维码模块，用于无屏设备账号授权。

#### v1.0

* 提供基本的账号授权、设备绑定、H5 等功能。 

### iOS

#### v2.1.2

*   Core模块修复第三方账号方案时绑定关系和ClientId潜在冲突的问题。

#### v2.1.1

* TVSAuthDelegate 新增 QQ 验票回调;

#### v2.1.0:

* 账号模块新增根据指定用户 openId 查询 UserInfo 接口；

* 技能服务模块新增闹钟管理、儿童模式、设备控制、第三方授权接口；

* 账号模块支持注入账号信息，支持第三方账号；

* TSKM 请求回调增加错误码；

* H5 模块新增注入额外信息接口;

* 设备模块新增数据更新接口；

* 修复设备查询 guid 被覆盖问题；

* 修复获取 TVSID 接口问题；

* 修复 H5 cookie 问题；

#### v2.0.1:

* H5 模块新增链接加载拦截回调；

* H5 模块新增智能家居页面；

* 修复日志模块重复初始化问题；

* 新增 armv7/armv7s/i386 架构;

#### v2.0.0:

* 模块化拆分，核心模块(环境配置/账号/设备绑定)为必须，H5 等模块根据需要可选；

* H5 模块由 `ViewController` 页面改为 `WebView` 组件形式，可定制化程度更高；

* H5 模块支持自己实现账号授权（不调用此 SDK 做登录和刷票）；

* 新增二维码模块，用于无屏设备账号授权；

#### v1.0:

* 提供基本的账号授权、设备绑定、H5 等功能； 

[4]: https://github.com/TencentDingdang/dmsdk/blob/master/doc/iOS/README.md#ios-sdk-v10---v200-%E5%8D%87%E7%BA%A7%E6%8C%87%E5%8D%97
[5]: https://github.com/TencentDingdang/dmsdk/blob/master/doc/Android/%E7%89%88%E6%9C%AC%E6%9B%B4%E6%96%B0%E6%97%A5%E5%BF%97.md#%E8%BF%81%E7%A7%BB%E6%8C%87%E5%8D%97-1
