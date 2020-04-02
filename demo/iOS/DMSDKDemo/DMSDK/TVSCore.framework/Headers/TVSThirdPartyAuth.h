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

typedef NS_ENUM(NSInteger,TVSCP) {
    TVSCPUnknown,
    TVSCPQQMusic
};

@interface TVSCPCredential : NSObject

@property(nonatomic,readonly) TVSCP cp;

@property(nonatomic,readonly) NSString* appId;

@property(nonatomic,readonly) NSString* openId;

@property(nonatomic,readonly) NSString* openToken;

@property(nonatomic,readonly) NSInteger expireTime;

-(instancetype)initWithCP:(TVSCP)cp andAppId:(NSString*)appId andOpenId:(NSString*)openId andOpenToken:(NSString*)openToken andExpireTime:(NSInteger)expireTime;

@end

typedef NS_ENUM(NSInteger,TVSCPError) {
    // Common
    TVSCPErrorNotSupported = 1,
    // QQ Music App
    TVSCPErrorNotInstalled = 2,
    TVSCPErrorConnectingToAppFailure = 3,
    TVSCPErrorUserCancellation = 4,
    TVSCPErrorRequestingAuthFailure = 5,
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

@protocol TVSCPAuthAgent <NSObject>

-(NSString *)getAppId;

-(BOOL)checkCPInstalled;

-(void)requestCPCredentialWithHandler:(void(^)(BOOL,NSInteger,NSString *,TVSCPCredential *))handler;

-(void)jumpToAppStore;

@end

@interface TVSCPAuthAgentManager : NSObject

+(instancetype)shared;

-(id<TVSCPAuthAgent>)getAgentOfCP:(TVSCP)cp andAuthType:(NSString *)authType;

-(id<TVSCPAuthAgent>)getAgentOfCP:(TVSCP)cp;

-(void)setAgentMap:(NSDictionary<NSString *, id<TVSCPAuthAgent>> *)agentMap ofCP:(TVSCP)cp;

-(void)setAgent:(id<TVSCPAuthAgent>)agent ofCP:(TVSCP)cp;

@end

/*
 * @class TVSThirdPartyAuth
 * @brief TVS 第三方授权
 */
@interface TVSThirdPartyAuth : NSObject

/*
 * @brief 跳转到云叮当 APP 进行第三方授权
 * @param accountInfo 账号信息，使用本 SDK 做账号登录的传 nil
 * @param deviceInfo 设备信息，其中 productId、dsn、guid 必填！！
 * @param handler 回调，BOOL 表示是否成功
 */
+(void)gotoAuthWithAccountInfo:(nullable TVSAccountInfo*)accountInfo deviceInfo:(TVSDeviceInfo*)deviceInfo handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/*
 * @brief TVSThirdPartyAuth 类实例化
 * @param tskmProxy TSKMProxy 对象
 * @param deviceInfo 设备信息
 * @return TVSThirdPartyAuth 实例
 */
-(instancetype)initWithTSKMProxy:(nonnull TVSTSKMProxy*)tskmProxy deviceInfo:(TVSDeviceInfo*)deviceInfo;

/*
 * @brief 查询绑定的云叮当账号信息
 * @param handler 回调
 */
-(void)getBindedAccountInfoWithHandler:(nonnull void(^)(TVSAccountInfo* _Nullable))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/*
 * @brief 解绑云叮当账号
 * @param accountInfo 账号信息
 * @param handler 回调
 */
-(void)unbindWithAccountInfo:(TVSAccountInfo*)accountInfo handler:(nonnull void(^)(BOOL))handler __attribute__ ((deprecated("云叮当账号方案即将下线，请迁移到QQ音乐应用方案")));

/// 查询已获授权的CP账号信息。
/// 该接口对应的UniAccess接口文档为https://github.com/TencentDingdang/tvs-tools/blob/master/Tsk%20Protocol/domains_V3/TSKOAuth.md。
/// @param handler 请求结果回调，回调第一个参数为错误码，0为成功，非0为失败；当且仅当错误码为0且授权账号存在时，第二个参数为账号类型，第三个参数为账号的OpenID和AppID。
- (void)getBoundCPAccountWithHandler:(nonnull void(^)(NSInteger, NSString * _Nullable, TVSCPCredential * _Nullable))handler;

@end
