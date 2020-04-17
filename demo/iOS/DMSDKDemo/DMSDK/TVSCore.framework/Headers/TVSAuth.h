//
//  TVSAuth.h
//  DMSDK
//
//  Created by RincLiu on 21/08/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

/*
 * @file TVSAccount.h
 */
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <TVSCore/TVSCallback.h>

/**
 * @brief 非法的 ClientId 常量
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
extern NSString* const TVSInvalidClientId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 非法的 RefreshToken 常量
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
extern NSString* const TVSInvalidRefreshToken __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief TVS 账号类型
 */
typedef NS_ENUM(NSInteger,TVSAccountType) {
    /**
     * @brief 未知类型，保留。
     */
    TVSAccountTypeUnknown,
    /**
     * @brief 已废弃。
     */
    TVSAccountTypeQQNumber,
    
    /**
     * @brief QQ登录账号。
     */
    TVSAccountTypeQQOpenId,
    
    /**
     * @brief 微信登录账号。
     */
    TVSAccountTypeWechatOpenId,
    
    /**
     * @brief 已废弃。
     */
    TVSAccountTypePhoneNumber,
    
    /**
     * @brief 已废弃。
     */
    TVSAccountTypeQbId,
    
    /**
     * @brief 游客账号、旧版第三方账号（不再提供）
     */
    TVSAccountTypeIOSVistor,
    /// 标准化账号方案 - 厂商账号
    TVSAccountTypeVendor
};



/**
 * @brief TVS 授权结果
 */
typedef NS_ENUM(NSInteger,TVSAuthResult) {
    /**
     * @brief 授权成功
     */
    TVSAuthResultSuccess,
    /**
     * @brief 授权失败（网络原因）
     */
    TVSAuthResultFailedNetwork,
    /**
     * @brief 授权失败（未登录）
     */
    TVSAuthResultFailedNotLogin,
    /**
     * @brief 授权失败（其他原因）
     */
    TVSAuthResultFailedOther
};



/**
 * @class TVSAccountInfo
 * @brief 账号信息
 */
@interface TVSAccountInfo : NSObject

/**
 * @brief 账号类型
 */
@property(nonatomic,assign) TVSAccountType accountType;

/**
 * @brief 微信/QQ登录的 openID
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* appId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/// 当前登录账号的AppKey。
/// 仅当使用厂商账号登录时返回，其他时候返回空字符串。
@property(nonatomic, readonly) NSString * appKey;

/**
 * @brief 当前登录账号的 openID
 */
@property(nonatomic,copy) NSString* openId;

/**
 * @brief 当前登录账号的 accessToken
 */
@property(nonatomic,copy) NSString* accessToken;

/**
 * @brief 当前登录账号的 refreshToken
 */
@property(nonatomic,copy) NSString* refreshToken;

/**
 * @brief  微信登录的 unionId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,assign) NSString *unionId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 当前登录账号token的过期时间
 */
@property(nonatomic,assign) NSInteger expireTime;

/**
 * @brief TVS 平台返回的 tvsId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* tvsId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief userid
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* userId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 获取 TVS ClientId（适用于使用本 SDK 登录的场景）
 * @warning 必须确保已登录，并且 dsn 和 productId 不为空
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @return TVS ClientId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 获取 TVS ClientId（适用于自己做登录的场景）
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @param accountId 账号唯一ID
 * @return TVS ClientId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn accountId:(NSString*)accountId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 获取 TVS ClientId（适用于自己做登录的场景）
 * @warning 其中 productId、dsn、openId 必传！！
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @param openId 登录账号的Open ID
 * @param accessToken 登录账号的Access Token
 * @param refreshToken 登录账号的Refresh Token
 * @return TVS ClientId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn openId:(NSString*)openId accessToken:(NSString*)accessToken refreshToken:(NSString*)refreshToken __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 通过账号 ID 生成账号信息（仅适用于自己实现账号登录的场景！！）
 * @param appId 传 productId 的前半部分（即appKey）
 * @param accountId 账号Open ID
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(instancetype)initWithAppId:(NSString*)appId accountId:(NSString *)accountId __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

@end



/**
 * @class TVSUserInfo
 * @brief 用户信息
 */
__attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")))
@interface TVSUserInfo : NSObject

/**
 * @brief id类型
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,assign) TVSAccountType idType;

/**
 * @brief 昵称
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* nickName;

/**
 * @brief 头像
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* headImgUrl;

/**
 * @brief 性别（0：女，1：男）
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,assign) NSInteger sex;

/**
 * @brief 手机号
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,copy) NSString* phoneNumber;

@end

/// 当前登录账号的用户资料。
@interface TVSAccountProfile : NSObject

/// 昵称。
@property(nonatomic, copy) NSString * nickname;

/// 头像的URL。
@property(nonatomic, copy) NSString * avatarUrl;

/// 自定义的额外数据。
@property(nonatomic, copy) NSString * extra;

@end

/**
 * @protocol TVSAuthDelegate
 * @brief 账号授权代理类
 */
__attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")))
@protocol TVSAuthDelegate <NSObject>

@optional
/**
 * @brief 微信登录实现
 * @param handler 结果回调
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthWXLoginWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/**
 * @brief 微信刷票实现
 * @param handler 结果回调
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthWXTokenRefreshWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/**
 * @brief QQ 登录实现
 * @param handler 结果回调
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthQQLoginWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/**
 * @brief QQ验票实现
 * @param handler 结果回调
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthQQTokenVerifyWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/**
 * @brief 注销账号实现
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthLogout;

@required
/**
 * @brief 获取账号信息
 * @return 账号信息
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(TVSAccountInfo*)TVSAuthGetAccountInfo;

@optional
/**
 * @brief 获取用户信息
 * @return 用户信息
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(TVSUserInfo*)TVSAuthGetUserInfo;

@optional
/**
 * @brief 保存手机号
 * @param phoneNumber 手机号
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)TVSAuthSetPhoneNumber:(NSString*)phoneNumber;

@end

/// SDK登录代理。当需要微信或QQ登录时需要实现该协议。
@protocol OpenSdkLoginDelegate <NSObject>

@optional
/// 微信登录。当需要DM SDK支持拉起微信登录时，在该接口中实现与微信SDK的交互，取得code后回调handler。
/// @param viewController 拉起微信登录的UIViewController。请参考WXApi中sendReq:和sendAuthReq:viewController:delegate:接口的区别。
/// @param handler DM SDK接受code的回调，result表示是否登录结果，成功时需要传递后续字段。
- (void)doWeChatLoginForViewController:(nullable UIViewController *)viewController withHandler:(nonnull void(^)(TVSAuthResult result, NSString * _Nullable code))handler;

@optional
/// 检查微信是否已被用户安装。当需要DM SDK支持拉起微信登录时，在该接口中调用WXApi的isWXAppInstalled接口。
/// @return 微信已安装返回YES，未安装返回NO。
- (BOOL)isWXAppInstalled;

@optional
/// 判断当前微信的版本是否支持OpenApi。当需要DM SDK支持拉起微信登录时，在该接口中调用WXApi的isWXAppSupportApi接口。
/// @return 支持返回YES，不支持返回NO。
- (BOOL)isWXAppSupportApi;

@optional
/// 获取微信的itunes安装地址。当需要DM SDK支持拉起微信登录时，在该接口中调用WXApi的getWXAppInstallUrl接口。
/// @return 微信的安装地址字符串。
- (nullable NSString *)getWXAppInstallUrl;

@optional
/// QQ登录。当需要DM SDK支持拉起QQ登录时，在该接口中实现与QQ SDK的交互，取得OpenID、AccessToken、昵称和头像。
/// @param handler DM SDK接受账号信息的回调，result表示是否登录结果，成功时需要传递后续字段。
- (void)doQqLoginWithHandler:(nonnull void(^)(TVSAuthResult result, NSString * _Nullable openId, NSString * _Nullable accessToken, NSString * _Nullable nickname, NSString * _Nullable avatarUrl))handler;

@end

/**
 * @class TVSAuthManager
 * @brief TVS 账号授权接口
 */
@interface TVSAuthManager: NSObject <TVSAuthDelegate>

/// SDK登录代理。当需要微信或QQ登录时需要设置该协议。
@property(nonatomic, strong, nullable) id<OpenSdkLoginDelegate> openSdkLoginDelegate;

/**
 * @brief 账号信息
 * @warning 如果您接入的账号方案是3.0.0起支持的厂商账号方案，那么该属性应该为只读，请不要再写入；对旧版账号方案，如果不使用本 SDK 做账号授权，必须手动注入账号信息才能使用某些模块（比如设备绑定等）!!
 */
@property(nonatomic,strong) TVSAccountInfo* accountInfo;

/**
 * @brief 用户信息
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
@property(nonatomic,strong) TVSUserInfo* userInfo __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 获得 TVS 账号授权类单例对象
 * @return TVS 账号授权类实例
 */
+(instancetype)shared;

/// 使用App Key初始化SDK，厂商账号方案适用。
/// @param appKey 在云小微开放平台申请的App Key。
/// @warning 必须在 AppDelegate 的 application:didFinishLaunchingWithOptions: 方法中调用
- (void)registerAppWithAppKey:(NSString *)appKey;

/**
 * @brief 初始化 (自动从 Info.plist 读取相关参数)
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 * @warning 如果您接入的账号方案是3.0.0起支持的厂商账号方案，请不要调用这个接口，您应该选择调用 registerAppWithAppKey: ；必须在 AppDelegate 的 application:didFinishLaunchingWithOptions: 方法中调用
 */
-(void)registerApp __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/// 使用厂商账号登录。
/// @param sig 需要登录的账号的签名。该签名由厂商后台向云小微后台获取并返回给应用，并在此传入。
/// @param handler 登录成功与否的回调。
- (void)vendorLoginWithSig:(nonnull NSString *)sig andHandler:(nonnull TVSCallback)handler;

/// 刷新AccessToken。
/// @param handler 刷票成功与否的回调。
- (void)refreshTokenWithHandler:(nonnull TVSCallback)handler;

/// 验证AccessToken是否有效，但不刷新。
/// @param handler 票据是否有效的回调。
- (void)verifyTokenWithHandler:(nonnull TVSCallback)handler;

/// AccessToken是否存在；在厂商账号、微信、QQ或Visitor账号登录的情况下均会返回YES。
- (BOOL)isTokenExist;

/// 检查厂商账号AccessToken是否存在。返回YES时仅表示Token存在，但不保证Token的有效性。可以通过verifyTokenWithHandler验证有效性。
- (BOOL)isVendorTokenExist;

/// 获取当前登录账号的用户资料。
/// @param handler 结果回调。第一个参数表示错误码，0为成功，非0为失败；当且仅当成功时，第二个参数为当前登录账号的用户资料。
- (void)getAccountProfileWithHandler:(nonnull void(^)(NSInteger, TVSAccountProfile * _Nonnull))handler;

/// 设置当前登录账号的用户资料。
/// @param accountProfile 用户资料，该字段的所有属性会被存储到云小微后台，使用 getAccountProfileWithHandler: 可以获取这里设置的用户资料。
/// @param handler 结果回调。第一个参数表示错误码，0为成功，非0为失败。
- (void)setAccountProfile:(nonnull TVSAccountProfile *)accountProfile withHandler:(nonnull TVSCallback)handler;

/**
 * @brief 检查微信 Token 是否存在
 * @return 是否存在
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(BOOL)isWXTokenExist __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 检查 QQ Token 是否存在
 * @return 是否存在
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(BOOL)isQQTokenExist __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 是否第三方登录
 * @return 是否
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(BOOL)isVisitorTokenExist __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 检测微信是否安装，版本是否支持
 * @param alert 是否提示
 * @return 安装且版本支持返回 YES
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(BOOL)checkWXAppWithAlert:(BOOL)alert __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 微信登录
 * @warning 如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)wxLoginWithHandler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 微信登录(支持未安装微信的情况，会跳转 H5 短信登录)
 * @warning 如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @param vc 发起微信登录的界面
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)wxLoginWithViewController:(UIViewController*)vc handler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 刷新微信 Token
 * @warning 如果微信 token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)wxTokenRefreshWithHandler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief QQ 登录
 * @warning 如果 QQ token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)qqLoginWithHandler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 验证 QQ Token
 * @warning 如果 QQ token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)qqTokenVerifyWithHandler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 通过微信/QQ 账号信息直接到 TVS 后台授权（换取tvsId）
 * @warning 仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！!
 * @param openId QQ/微信登录后返回的 openId
 * @param accessToken QQ/微信登录后返回的 accessToken
 * @param accountType 账号类型
 * @param handler 回调，BOOL 值表示是否成功，成功后即可通过 TVSAccountInfo 读取 tvsId
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)tvsAuthWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken accountType:(TVSAccountType)accountType handler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 验证 QQ token
 * @warning 仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！!
 * @param openId QQ/微信登录后返回的 openId
 * @param accessToken QQ/微信登录后返回的 accessToken
 * @param handler 回调，TVSAuthResult 值表示结果
 * @deprecated 仅用于旧版账号方案，新版账号方案不需要使用该接口。
 */
-(void)verifyQQTokenWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken handler:(nonnull void(^)(TVSAuthResult))handler __attribute__ ((deprecated("仅用于旧版账号方案，新版账号方案不需要使用该接口。")));

/**
 * @brief 注销登录
 */
-(void)logout;

/**
 * @brief 获取用于绑定手机号的验证码
 * @warning 必须确保已登录!!
 * @param phoneNumber 手机号
 * @param handler 回调
 * @deprecated 您应该不需要使用该接口，该接口会在下一个版本移除
 */
-(void)getCaptchaWithPhoneNumber:(NSString*)phoneNumber handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("您应该不需要使用该接口，该接口会在下一个版本移除")));

/**
 * @brief 绑定手机号
 * @warning 必须确保已登录!!
 * @param phoneNumber 手机号
 * @param captcha 验证码
 * @param handler 回调，BOOL 值表示是否成功
 * @deprecated 您应该不需要使用该接口，该接口会在下一个版本移除
 */
-(void)bindPhoneNumber:(NSString*)phoneNumber captcha:(NSString*)captcha handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("您应该不需要使用该接口，该接口会在下一个版本移除")));

/**
 * @brief 保存用户信息到 TVS 后台(暂时只支持头像昵称)
 * @warning 必须确保已登录!!
 * @param userInfo 用户信息
 * @param handler 回调，BOOL 值表示是否成功
 * @deprecated 您应该不需要使用该接口，该接口会在下一个版本移除
 */
-(void)syncUserInfo:(TVSUserInfo*)userInfo handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("您应该不需要使用该接口，该接口会在下一个版本移除")));

/**
 * @brief 通过 openId 查询用户信息(暂时只支持头像昵称)
 * @param openId 用户 openId
 * @param handler 回调，用于接收返回的用户信息
 * @deprecated 您应该不需要使用该接口，该接口会在下一个版本移除
 */
-(void)queryUserInfoWithOpenId:(NSString*)openId handler:(nonnull void(^)(TVSUserInfo*))handler __attribute__ ((deprecated("您应该不需要使用该接口，该接口会在下一个版本移除")));

@end
