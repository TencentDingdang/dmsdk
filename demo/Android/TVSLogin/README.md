# TVSLogin

本demo用于展示DM SDK的各功能模块。

## 自定义初始化参数

DM SDK需要在`Application#onCreate`中初始化，并传入相应的初始化参数。

这些参数被指定在DemoConstant类中，如果您需要使用您自己的参数配置，则需要修改该类中的相应常量并重新编译demo。

如果您不想重新编译demo，您可以使用demo中的“使用自定义配置”页面配置部分初始化参数。由于DM SDK在应用启动时就完成了初始化，因此修改该页面的配置后，**您需要强杀demo应用并重启应用使配置生效**。

目前只有以下参数支持在demo的UI中自定义：

+ QQ音乐授权App ID
+ QQ音乐授权私钥

其他参数由于和包名关联或需要修改AndroidManifest.xml文件，因此必须修改demo代码后重新编译才能生效。
