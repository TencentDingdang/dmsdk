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

/*
 * @brief 非法的 ClientId 常量
 */
extern NSString* const TVSInvalidClientId;

/*
 * @brief 非法的 RefreshToken 常量
 */
extern NSString* const TVSInvalidRefreshToken;

/*
 * @brief TVS 账号类型
 */
typedef NS_ENUM(NSInteger,TVSAccountType) {
    /*
     * @brief
     */
    TVSAccountTypeUnknown,
    /*
     * @brief
     */
    TVSAccountTypeQQNumber,
    
    /*
     * @brief
     */
    TVSAccountTypeQQOpenId,
    
    /*
     * @brief
     */
    TVSAccountTypeWechatOpenId,
    
    /*
     * @brief
     */
    TVSAccountTypePhoneNumber,
    
    /*
     * @brief
     */
    TVSAccountTypeQbId,
    
    /*
     * @brief
     */
    TVSAccountTypeIOSVistor
};



/*
 * @brief TVS 授权结果
 */
typedef NS_ENUM(NSInteger,TVSAuthResult) {
    /*
     * @brief 授权成功
     */
    TVSAuthResultSuccess,
    /*
     * @brief 授权失败（网络原因）
     */
    TVSAuthResultFailedNetwork,
    /*
     * @brief 授权失败（未登录）
     */
    TVSAuthResultFailedNotLogin,
    /*
     * @brief 授权失败（其他原因）
     */
    TVSAuthResultFailedOther
};



/*
 * @class TVSAccountInfo
 * @brief 账号信息
 */
@interface TVSAccountInfo : NSObject

/*
 * @brief 账号类型
 */
@property(nonatomic,assign) TVSAccountType accountType;

/*
 * @brief 微信/QQ登录的 openID
 */
@property(nonatomic,copy) NSString* appId;

/*
 * @brief 微信/QQ登录的 openID
 */
@property(nonatomic,copy) NSString* openId;

/*
 * @brief 微信/QQ登录的 accessToken
 */
@property(nonatomic,copy) NSString* accessToken;

/*
 * @brief 微信登录的 refreshToken
 */
@property(nonatomic,copy) NSString* refreshToken;

/*
 * @brief  微信登录的 unionId
 */
@property(nonatomic,assign) NSString *unionId;

/*
 * @brief 微信/QQ token 的过期时间
 */
@property(nonatomic,assign) NSInteger expireTime;

/*
 * @brief TVS 平台返回的 tvsId
 */
@property(nonatomic,copy) NSString* tvsId;

/*
 * @brief userid
 */
@property(nonatomic,copy) NSString* userId;

/*
 * @brief 获取 TVS ClientId（适用于使用本 SDK 登录的场景）
 * @warning 必须确保已登录，并且 dsn 和 productId 不为空
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @return TVS ClientId
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn;

/*
 * @brief 获取 TVS ClientId（适用于自己做登录的场景）
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @param accountId 账号唯一ID
 * @return TVS ClientId
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn accountId:(NSString*)accountId;

/*
 * @brief 获取 TVS ClientId（适用于自己做登录的场景）
 * @warning 其中 productId、dsn、openId 必传！！
 * @param productId TVS 平台申请的产品 ID
 * @param dsn 设备序列号
 * @param openId
 * @param accessToken
 * @param refreshToken
 * @return TVS ClientId
 */
+(NSString*)clientIdWithProductId:(NSString*)productId dsn:(NSString*)dsn openId:(NSString*)openId accessToken:(NSString*)accessToken refreshToken:(NSString*)refreshToken;

/*
 * @brief 通过账号 ID 生成账号信息（仅适用于自己实现账号登录，且没有token的场景！！）
 * @param accountId 账号唯一 ID，比如手机号
 */
-(instancetype)initWithAccountId:(NSString*)accountId;

@end



/*
 * @class TVSUserInfo
 * @brief 用户信息
 */
@interface TVSUserInfo : NSObject

/*
 * @brief id类型
 */
@property(nonatomic,assign) TVSAccountType idType;

/*
 * @brief 昵称
 */
@property(nonatomic,copy) NSString* nickName;

/*
 * @brief 头像
 */
@property(nonatomic,copy) NSString* headImgUrl;

/*
 * @brief 性别（0：女，1：男）
 */
@property(nonatomic,assign) NSInteger sex;

/*
 * @brief 手机号
 */
@property(nonatomic,copy) NSString* phoneNumber;

@end



/*
 * @protocol TVSAuthDelegate
 * @brief 账号授权代理类
 */
@protocol TVSAuthDelegate <NSObject>

@optional
/*
 * @brief 微信登录实现
 * @param handler 结果回调
 */
-(void)TVSAuthWXLoginWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/*
 * @brief 微信刷票实现
 * @param handler 结果回调
 */
-(void)TVSAuthWXTokenRefreshWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/*
 * @brief QQ 登录实现
 * @param handler 结果回调
 */
-(void)TVSAuthQQLoginWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/*
 * @brief QQ验票实现
 * @param handler 结果回调
 */
-(void)TVSAuthQQTokenVerifyWithHandler:(void(^)(TVSAuthResult))handler;

@optional
/*
 * @brief 注销账号实现
 */
-(void)TVSAuthLogout;

@required
/*
 * @brief 获取账号信息
 * @return 账号信息
 */
-(TVSAccountInfo*)TVSAuthGetAccountInfo;

@optional
/*
 * @brief 获取用户信息
 * @return 用户信息
 */
-(TVSUserInfo*)TVSAuthGetUserInfo;

@optional
/*
 * @brief 保存手机号
 * @param phoneNumber 手机号
 */
-(void)TVSAuthSetPhoneNumber:(NSString*)phoneNumber;

@end



/*
 * @class TVSAuthManager
 * @brief TVS 账号授权接口
 */
@interface TVSAuthManager: NSObject <TVSAuthDelegate>

/*
 * @brief 账号信息
 * @warning 如果不使用本 SDK 做账号授权，必须手动注入账号信息才能使用某些模块（比如设备绑定等）!!
 */
@property(nonatomic,strong) TVSAccountInfo* accountInfo;

/*
 * @brief 用户信息
 */
@property(nonatomic,strong) TVSUserInfo* userInfo;

/*
 * @brief 获得 TVS 账号授权类单例对象
 * @return TVS 账号授权类实例
 */
+(instancetype)shared;

/*
 * @brief 初始化 (自动从 Info.plist 读取相关参数)
 * @warning 必须在 AppDelegate 的 application:didFinishLaunchingWithOptions: 方法中调用
 */
-(void)registerApp;

/*
 * @brief 处理 URL 跳转
 * @warning 必须在 AppDelegate 的 application:openURL:options: 方法中调用
 * @param url 待处理的 URL
 * @return 是否成功处理相关 URL 跳转
 */
-(BOOL)handleOpenUrl:(NSURL*)url;

/*
 * @brief 检查微信 Token 是否存在
 * @return 是否存在
 */
-(BOOL)isWXTokenExist;

/*
 * @brief 检查 QQ Token 是否存在
 * @return 是否存在
 */
-(BOOL)isQQTokenExist;

/*
 * @brief 检测微信是否安装，版本是否支持
 * @param alert 是否提示
 * @return 安装且版本支持返回 YES
 */
-(BOOL)checkWXAppWithAlert:(BOOL)alert;

/*
 * @brief 微信登录
 * @warning 如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)wxLoginWithHandler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 微信登录(支持未安装微信的情况，会跳转 H5 短信登录)
 * @warning 如果微信 token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @@param viewController 发起微信登录的界面
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)wxLoginWithViewController:(UIViewController*)vc handler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 刷新微信 Token
 * @warning 如果微信 token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)wxTokenRefreshWithHandler:(void(^)(TVSAuthResult))handler;

/*
 * @brief QQ 登录
 * @warning 如果 QQ token 不存在，则必须调用此方法，以获得 TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)qqLoginWithHandler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 验证 QQ Token
 * @warning 如果 QQ token 存在，则必须调用此方法，以获得(更新) TVS 后台返回的相关账户信息
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)qqTokenVerifyWithHandler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 通过微信/QQ 账号信息直接到 TVS 后台授权（换取tvsId）
 * @warning 仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！!
 * @param openId QQ/微信登录后返回的 openId
 * @param accessToken QQ/微信登录后返回的 accessToken
 * @param accountType 账号类型
 * @param handler 回调，BOOL 值表示是否成功，成功后即可通过 TVSAccountInfo 读取 tvsId
 */
-(void)tvsAuthWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken accountType:(TVSAccountType)accountType handler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 验证 QQ token
 * @warning 仅针对之前已经独自接入 QQ/微信 SDK，且自己维护 token 过期刷新的场景！!
 * @param openId QQ/微信登录后返回的 openId
 * @param accessToken QQ/微信登录后返回的 accessToken
 * @param handler 回调，TVSAuthResult 值表示结果
 */
-(void)verifyQQTokenWithOpenId:(NSString*)openId accessToken:(NSString*)accessToken handler:(void(^)(TVSAuthResult))handler;

/*
 * @brief 注销登录
 */
-(void)logout;

/*
 * @brief 获取用于绑定手机号的验证码
 * @warning 必须确保已登录!!
 * @param phoneNumber 手机号
 * @param handler 回调
 */
-(void)getCaptchaWithPhoneNumber:(NSString*)phoneNumber handler:(void(^)(BOOL))handler;

/*
 * @brief 绑定手机号
 * @warning 必须确保已登录!!
 * @param phoneNumber 手机号
 * @param captcha 验证码
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)bindPhoneNumber:(NSString*)phoneNumber captcha:(NSString*)captcha handler:(void(^)(BOOL))handler;

/*
 * @brief 保存用户信息到 TVS 后台(暂时只支持头像昵称)
 * @warning 必须确保已登录!!
 * @param userInfo 用户信息
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)syncUserInfo:(TVSUserInfo*)userInfo handler:(void(^)(BOOL))handler;

/*
 * @brief 通过 openId 查询用户信息(暂时只支持头像昵称)
 * @param openId 用户 openId
 * @param handler 回调，用于接收返回的用户信息
 */
-(void)queryUserInfoWithOpenId:(NSString*)openId handler:(void(^)(TVSUserInfo*))handler;

@end
