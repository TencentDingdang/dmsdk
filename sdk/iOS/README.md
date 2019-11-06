## iOS SDK 模块介绍:

* **TVSCore.framework**: 核心模块（环境配置、账号授权、设备管理），**必选**； 

* **TVSMember.framework**: 会员状态查询，根据需要可选；

* **TVSSpeech.framework**: 音色 TTS 配置，根据需要可选； 

* **TVSTSKM.framework**: 技能服务访问，根据需要可选；

* **TVSWeb.framework**: H5 页面，支持自己实现账号授权（须实现 `TVSAuthDelegate` 协议），根据需要可选；
