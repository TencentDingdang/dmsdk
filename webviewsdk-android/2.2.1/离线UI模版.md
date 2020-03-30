# 离线UI模版

智能设备端接入离线UI模版后，能够加载离线UI模版响应语义返回数据，减少设备端开发工作量。

在接入前，请确保根据接入指南完成了SDK的集成。

接入离线UI模版的主要流程为：

 * 调用初始化逻辑和内置离线UI模版资源包
 * 判断是否能用离线UI模版打开
 * 启动窗口并绑定webview
 * 同步音频和tts等状态给离线UI模版
 * 调用检查更新离线UI模版资源逻辑

注意，接入方无需自己实现url的打开逻辑，模版url加载已经封装到SDK内部，绑定到窗口的webview已经加载好模版的url和离线资源。

接下来会逐步展示各步骤的实现方法。demo中亦包含了覆盖以上步骤的完整实现。

## 调用初始化逻辑和内置离线UI模版资源包

接入方需要将离线UI模版的资源包内置到初始化时设置的目录，离线UI模版资源包为 webtemplate.zip。

初始化离线UI模版，在activity或者application的onCreate方法中调用OfflineWebManager的init方法。

示例代码
```
String sPath = "sdcard/nativeweb/";
OfflineWebManager.getInstance().init(this, sPath, mWebCallback);

```

## 判断是否能用离线UI模版打开

TVS SDK返回的json ui 数据调用OfflineWebManager.getInstance().isCanOpenByWebTemplate 接口来判断是否能够用离线UI模版打开。

示例代码

```
if (OfflineWebManager.getInstance().isCanOpenByWebTemplate(jsonUi.toString(),   sQueryStr, false, sTestDialogId)){   
	//需要打开一个装载webview的activity    
}
```

## 启动窗口并绑定webview

如果可以用离线UI模版模版打开的情况，接入方自己实现装载webview 界面的窗口，并启动窗口。

接入方自己的窗口中，将显示ui 的webview，绑定到窗口上。

示例代码

```
mWebView = OfflineWebManager.getInstance().getLoadWebView();
if (null != mWebView) {    
	mTvsHoloLightLayout.addView(mWebView, 0);
}
```

注意如果需要当前加载的ui jason 数据或者当前加载的模版id tid 信息，可以通过下面两个接口获取，并缓存在Activity 中。
OfflineWebManager.getInstance().getLoadJasonData()
OfflineWebManager.getInstance().getCurTid()

在onResume 和onDestroy 中，必须注册和反注册当前的Activity 对象。
OfflineWebManager.getInstance().setWebActivity

ITMSWebViewJsListener 的js 回调接口处理，可以参考demo 中的处理。OfflineWebConstants.JS_CMD_PROXYDATA
OfflineWebConstants.JS_CMD_FINISHACT
OfflineWebConstants.JS_CMD_SETTINGS

这三个cmd 接入方自己处理，其它的统一传给OfflineWebManager 的handleJsCallNative 方法处理。

详细的示例代码参考demo中openNativeWebActivity方法和OfflineWebActivity类

## 同步音频和tts等状态给离线UI模版

接入方通过调用下面的两个方法来传递相关状态数据，根据自身的业务实现进行调用。

OfflineWebManager.getInstance().notifyMediaIdAndPlayStatus

OfflineWebManager.getInstance().notifyTTSPlayStatus

接口详细信息，请参照接口文档

## 调用检查更新离线UI模版资源逻辑

检查更新离线UI模版资源包的接口如下。

OfflineWebManager.getInstance().checkNativeWebPkgUpdate()

<!--
接下来会逐步展示各步骤的实现方法。demo中亦包含了覆盖以上步骤的完整实现。
TODO: 反注释上述文本，并增加文档。
-->