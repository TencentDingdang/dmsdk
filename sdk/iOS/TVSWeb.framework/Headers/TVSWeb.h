//
//  TVSWeb.h
//  DMSDK
//
//  Created by Rinc Liu on 20/10/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <TVSCore/TVSAuth.h>
#import <TVSCore/TVSDevice.h>

/**
 * @brief TVS 相关 WEB 页面类型
 */
typedef NS_ENUM(NSInteger,TVSWebPageType) {
    /**
     * @brief 智能家居页面
     */
    TVSWebPageTypeSmartHome,
    /**
     * @brief QQ 音乐页面
     */
    TVSWebPageTypeMusic,
    /**
     * @brief 第三方账号授权页面
     */
    TVSWebPageTypeThirdPartyAuth,
    /**
     * @brief 账号授权页面
     */
    TVSWebPageTypeAuth,
    /**
     * @brief 个人中心页面
     */
    TVSWebPageTypeMember,
    /**
     * @brief 会员领取页面
     */
    TVSWebPageTypeGetVIP,
    /**
     * @brief 会员充值页面
     */
    TVSWebPageTypeRecharge,
    /**
     * @brief 手机号地址页面
     */
    TVSWebPageTypePhoneAddress,
    /**
     * @brief 用户反馈页面
     */
    TVSWebPageTypeFeedback,
    /// CP授权 - 拉起QQ音乐应用授权页面
    TVSWebPageTypeCPAuthQQMusic
};



/**
 * @protocol TVSWebUniversalDelegate
 * @brief TVS WEB 页面通用回调
 */
@protocol TVSWebUniversalDelegate <NSObject>

@optional
/**
 * @brief Web 页面加载开始
 */
-(void)TVSWebLoadStart;

@optional
/**
 * @brief Web 页面加载进度更新
 * @param progress 页面加载进度
 */
-(void)TVSWebLoadProgress:(double)progress;

@optional
/**
 * @brief Web 页面加载完成
 */
-(void)TVSWebLoadFinish;

@optional
/**
 * @brief Web 页面加载错误
 */
-(void)TVSWebLoadError:(NSError*)error;

@optional
/**
 * @brief Web 页面拉取到网页标题
 * @param title 网页标题
 */
-(void)TVSWebGotTitle:(NSString*)title;

@optional
/**
 * @brief Web 页面是否允许打开指定 scheme 的链接
 * @warning 默认只允许打开 http、https、wexin、wtloginm、itms、itms-apps、dingdang 等常规 scheme
 * @param scheme
 * @return 是否允许打开
 */
-(BOOL)TVSWebShouldOpenScheme:(NSString*)scheme;

@optional
/**
 * @brief Web 页面是否允许加载链接
 * @warning 默认都允许打开
 * @param url 网页链接
 * @return 是否允许加载
 */
-(BOOL)TVSWebShouldLoadUrl:(NSString*)url;

@end



/**
 * @protocol TVSWebBusinessDelegate
 * @brief TVS WEB 页面业务回调
 */
@protocol TVSWebBusinessDelegate<NSObject>

@optional
/// Web页面刷新厂商账号票据回调
/// @param errorCode 刷新厂商账号AccessToken结果错误码，参考 [TVSAuthManager refreshTokenWithHandler:]
- (void)TVSWebRefreshAccessTokenResult:(NSInteger)errorCode;

@optional
/**
 * @brief Web 页面 QQ 登录回调
 * @param result QQ登录结果
 */
-(void)TVSWebQQLoginResult:(TVSAuthResult)result;

@optional
/**
 * @brief Web 页面微信登录回调
 * @param result 微信登录结果
 */
-(void)TVSWebWXLoginResult:(TVSAuthResult)result;

@optional
/**
 * @brief Web 页面QQ验票回调
 * @param result QQ验票结果
 */
-(void)TVSWebVerifyQQTokenResult:(TVSAuthResult)result;

@optional
/**
 * @brief Web 页面微信刷票回调
 * @param result 微信刷票结果
 */
-(void)TVSWebRefreshWXTokenResult:(TVSAuthResult)result;

@optional
/**
 * @brief Web 请求关闭当前页面
 */
-(void)TVSWebRequestExit;

@optional
/**
 * @brief Web 页面透传参数
 * @param data 透传的参数字典
 */
-(void)TVSWebProxyData:(NSDictionary*)data;

@optional
/**
 * @brief Web 页面收到 JS 消息
 * @param msg JS 消息名
 * @param data JS 消息数据
 */
-(void)TVSWebReceivedJSMessage:(NSString*)msg data:(id)data;

@optional
/**
 * @brief Web 页面注入额外数据
 * @return 需要注入的额外数据
 */
-(NSDictionary*)TVSWebRequestExtraData;

@end



/**
 * @brief TVS Web 组件
 */
@interface TVSWebView : UIView

/**
 * @brief 网页通用回调
 */
@property(nonatomic,weak) id<TVSWebUniversalDelegate> webUniversalDelegate;

/**
 * @brief 网页业务回调
 */
@property(nonatomic,weak) id<TVSWebBusinessDelegate> webBusinessDelegate;

/**
 * @brief 账号授权协议
 * @warning 如果不使用 SDK 里面的 TVSAuthManager 授权，而是自己调用微信/QQ SDK，则必须实现此协议！！
 */
@property(nonatomic,weak) id<TVSAuthDelegate> authDelegate;

/**
 * @brief 设备相关信息（QQ 音乐会员等页面需要）
 * @warning 其中 deviceBindType、deviceType、deviceOEM、productId、DSN 几个字段为必填!!
 */
@property(nonatomic,strong) TVSDeviceInfo* device;

/**
 * @brief 
 */
@property(nonatomic,strong) NSString* tvsToken;

/**
 * @brief 是否自动检查设备绑定
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 * @warning LinkPlay 设备打开个人中心页面需要检查绑定
 */
@property(nonatomic,assign) BOOL autoCheckDeviceBind __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 是否显示网页调试工具
 */
@property(nonatomic,assign) BOOL showDebugTool;

/// 获取当前环境下云小微提供的服务的URL。云小微提供的各Web服务在各自的path下，传入该path，可获得对应DM SDK当前环境的该Web服务的URL。将该URL传给[TVSWebView loadUrl]即可打开该URL。例如，QQ音乐授权的path为/v2m/cooperation/skillAuthManagerForCP。不同环境下URL的域名有所不同，因此请使用该接口获取正确的URL，避免硬编码URL。
/// @param path Web服务对应的path，会被URL编码！
/// @return 获取的URL，如果参数不合法则返回nil
+(nullable NSString *)getPresetUrlByPath:(nullable NSString *)path;

/**
 * @brief 实例化
 * @param frame 展示该TVSWebView的Frame
 * @return 实例
 */
-(instancetype)initWithFrame:(CGRect)frame;

/**
 * @brief 打开指定类型的 Web 页面
 * @param pageType Web 页面类型
 */
-(void)loadPage:(TVSWebPageType)pageType;

/**
 * @brief 打开指定链接的 Web 页面
 * @param url Web 页面链接
 */
-(void)loadUrl:(NSString*)url;

/**
 * @brief 能否回退网页
 * @return 能否
 */
-(BOOL)canGoBack;

/**
 * @brief 能否前进网页
 * @return 能否
 */
-(BOOL)canGoForward;

/**
 * @brief 回退网页
 * @return 是否成功
 */
-(BOOL)goBack;

/**
 * @brief 前进网页
 * @return 是否成功
 */
-(BOOL)goForward;

/**
 * @brief 刷新网页
 */
-(void)reload;

/**
 * @brief 停止加载网页
 */
-(void)stopLoading;

/**
 * @brief 获取 UIScrollView 实例
 * @return UIScrollView 实例
 */
-(UIScrollView*)scrollView;

/**
 * @brief 执行 JS 代码
 * @param code JS 代码
 * @param handler 回调，BOOL 表示是否成功
 */
-(void)runJSCode:(NSString*)code handler:(nonnull void(^)(BOOL))handler;

@end
