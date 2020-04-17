//
//  TVSThirdPartyAuth.h
//  TVSCore
//
//  Created by Rinc Liu on 28/3/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSAuth.h>
#import <TVSCore/TVSDevice.h>
#import <TVSCore/TVSTSKMProxy.h>

/// 定义目前DM SDK支持的CP（内容提供商）类型。
typedef NS_ENUM(NSInteger,TVSCP) {
    /// 未知类型，保留使用。
    TVSCPUnknown,
    /// QQ音乐
    TVSCPQQMusic
};

/// CP账号凭证信息，在拉起第三方CP应用后构造成，并传给DM SDK完成后续的授权绑定操作。
@interface TVSCPCredential : NSObject

/// CP账号类型
@property(nonatomic,readonly) TVSCP cp;

/// CP账号的App ID
@property(nonatomic,readonly) NSString* appId;

/// CP账号的Open ID
@property(nonatomic,readonly) NSString* openId;

/// CP账号的Open Token
@property(nonatomic,readonly) NSString* openToken;

/// CP账号的过期时间
@property(nonatomic,readonly) NSInteger expireTime;

/// 初始化一个实例对象
/// @param cp CP账号类型
/// @param appId CP账号的App ID
/// @param openId CP账号的Open ID
/// @param openToken CP账号的Open Token
/// @param expireTime CP账号的过期时间
-(instancetype)initWithCP:(TVSCP)cp andAppId:(NSString*)appId andOpenId:(NSString*)openId andOpenToken:(NSString*)openToken andExpireTime:(NSInteger)expireTime;

@end

/// 定义授权过程中的通用错误，前端Web页面可以根据这些预定的错误码作出对应的提示。
typedef NS_ENUM(NSInteger,TVSCPError) {
    // Common
    /// 不支持的CP类型
    TVSCPErrorNotSupported = 1,
    /// 指定CP的应用未安装
    TVSCPErrorNotInstalled = 2,
    // QQ Music App
    /// 无法和CP应用通信
    TVSCPErrorConnectingToAppFailure = 3,
    /// 用户在CP应用中取消了授权
    TVSCPErrorUserCancellation = 4,
    /// 获取用户的授权信息失败
    TVSCPErrorRequestingAuthFailure = 5,
    /// 将用户的CP账号与云小微账号授权绑定失败
    TVSCPErrorBindingFailure = 6,
    // QQ Mini Program
    TVSCPErrorQqmpGetUrlRequest = 1001,
    TVSCPErrorQqmpGetUrlResponse = 1002,
    TVSCPErrorQqmpGetUrlInvalid = 1003,
    TVSCPErrorQqmpGetResultRequest = 1101,
    TVSCPErrorQqmpGetResultResponse = 1102,
    TVSCPErrorQqmpGetResultInvalid = 1103,
    TVSCPErrorQqmpGetResultDecrypt = 1104,
    TVSCPErrorQqmpGetResultNoData = 1105,
    TVSCPErrorQqmpGetResultVerify = 1106,
    // WeChat Mini Program
    TVSCPErrorWxmpLaunch = 1201,
    TVSCPErrorWxmpResponse = 1202,
    TVSCPErrorWxmpDecrypt = 1203,
    TVSCPErrorWxmpNoData = 1204,
    TVSCPErrorWxmpVerify = 1205,
};

/// CP代理实现协议。对任意支持的CP，接入方需要实现该协议，并将实现了的对象
@protocol TVSCPAuthAgent <NSObject>

/// 获取CP账号的App ID，该App ID由接入方向CP方申请获得。
-(NSString *)getAppId;

/// 检查CP应用是否已经安装，接入方需要判断是否当前设备能够响应拉起CP授权的行为。
-(BOOL)checkCPInstalled;

/// 请求CP应用授权，接入方需要实现拉起CP应用授权的行为，并返回授权结果
/// @param handler 授权结果回调，第一个BOOL参数表示是否成功，第二个参数表示错误码，第三个参数表示可在UI上展示的错误信息，最后一个参数返回授权账号信息，若授权失败则传入nil。
-(void)requestCPCredentialWithHandler:(void(^)(BOOL,NSInteger,NSString *,TVSCPCredential *))handler;

/// 跳转到应用商店，接入方需要在这里实现引导用户前往商店安装CP应用的逻辑。
-(void)jumpToAppStore;

@end

/// CP账号代理对象管理器，用于注入和获取各CP账号的代理实现对象。
@interface TVSCPAuthAgentManager : NSObject

/// 获取单例。
+(instancetype)shared;

/// 获取指定CP、指定授权类型的代理实现对象。
/// @param cp 指定CP的枚举类型
/// @param authType 指定授权类型的字符串，支持的字符串常量请参考QQ音乐授权文档
-(id<TVSCPAuthAgent>)getAgentOfCP:(TVSCP)cp andAuthType:(NSString *)authType;

/// 获取指定CP的代理实现对象。
/// @param cp 指定CP的枚举类型
-(id<TVSCPAuthAgent>)getAgentOfCP:(TVSCP)cp;

/// 为指定CP设置实现代理对象，以供DM SDK内部使用。
/// @param agentMap 实现代理对象映射表，其key为授权类型的字符串，支持的字符串常量请参考QQ音乐授权文档
/// @param cp 指定CP枚举类型
-(void)setAgentMap:(NSDictionary<NSString *, id<TVSCPAuthAgent>> *)agentMap ofCP:(TVSCP)cp;

/// 为指定CP设置实现代理对象，以供DM SDK内部使用。
/// @param agent 实现代理对象
/// @param cp 指定CP枚举类型
-(void)setAgent:(id<TVSCPAuthAgent>)agent ofCP:(TVSCP)cp;

@end

/**
 * @class TVSThirdPartyAuth
 * @brief TVS 第三方授权
 */
@interface TVSThirdPartyAuth : NSObject

/**
 * @brief 跳转到云叮当 APP 进行第三方授权
 * @param accountInfo 账号信息，使用本 SDK 做账号登录的传 nil
 * @param deviceInfo 设备信息，其中 productId、dsn、guid 必填！！
 * @param handler 回调，BOOL 表示是否成功
 * @deprecated 云叮当账号方案即将下线，请迁移到QQ音乐应用方案
 */
+(void)gotoAuthWithAccountInfo:(nullable TVSAccountInfo*)accountInfo deviceInfo:(TVSDeviceInfo*)deviceInfo handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/**
 * @brief TVSThirdPartyAuth 类实例化
 * @param tskmProxy TSKMProxy 对象
 * @param deviceInfo 设备信息
 * @return TVSThirdPartyAuth 实例
 */
-(instancetype)initWithTSKMProxy:(nonnull TVSTSKMProxy*)tskmProxy deviceInfo:(TVSDeviceInfo*)deviceInfo;

/**
 * @brief 查询绑定的云叮当账号信息
 * @param handler 回调
 * @deprecated 云叮当账号方案即将下线，请迁移到QQ音乐应用方案
 */
-(void)getBindedAccountInfoWithHandler:(nonnull void(^)(TVSAccountInfo* _Nullable))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/**
 * @brief 解绑云叮当账号
 * @param accountInfo 账号信息
 * @param handler 回调
 * @deprecated 云叮当账号方案即将下线，请迁移到QQ音乐应用方案
 */
-(void)unbindWithAccountInfo:(TVSAccountInfo*)accountInfo handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/// 查询已获授权的CP账号信息。
/// 该接口对应的UniAccess接口文档为https://github.com/TencentDingdang/tvs-tools/blob/master/Tsk%20Protocol/domains_V3/TSKOAuth.md。
/// @param handler 请求结果回调，回调第一个参数为错误码，0为成功，非0为失败；当且仅当错误码为0且授权账号存在时，第二个参数为账号类型，第三个参数为账号的OpenID和AppID。
- (void)getBoundCPAccountWithHandler:(nonnull void(^)(NSInteger, NSString * _Nullable, TVSCPCredential * _Nullable))handler;

@end
