//
//  NSObject+QQMusicWxmpAuthAgent.m
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/1/8.
//  Copyright © 2020 tencent. All rights reserved.
//

#import "QQMusicWxmpAuthAgent.h"
#import "WXApi.h"
#import "WXApiObject.h"
#import "RSA.h"
#import "QQMusicUtils.h"
#import "UrlUtil.h"

static NSString * const kScheme_Nonce = @"nonce";
static NSString * const kScheme_Sign = @"sign";
static NSString * const kScheme_OpenId= @"openId";
static NSString * const kScheme_OpenToken= @"openToken";
static NSString * const QQMusic_PubKey = @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrp4sMcJjY9hb2J3sHWlwIEBrJlw2Cimv+rZAQmR8V3EI+0PUK14pL8OcG7CY79li30IHwYGWwUapADKA01nKgNeq7+rSciMYZv6ByVq+ocxKY8az78HwIppwxKWpQ+ziqYavvfE5+iHIzAc8RvGj9lL6xx1zhoPkdaA0agAyuMQIDAQAB";

@interface QQMusicWxmpAuthAgent ()

@property(nonatomic,readonly) NSString * appId;

@property(nonatomic,readonly) NSString * secretKey;

@property(nonatomic,copy) void(^requestCPCredentialHandler)(BOOL success, NSInteger errCode, NSString * displayMessage, TVSCPCredential * credential);

@end

@implementation QQMusicWxmpAuthAgent

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey {
    if (self = [super init]) {
        _appId = appId;
        _secretKey = secretKey;
    }
    return self;
}

- (BOOL)checkCPInstalled {
    DDLogDebug(@"QQMusicWxmpAuthAgent.checkCPInstalled");
    return [WXApi isWXAppInstalled] && [WXApi isWXAppSupportApi];
}

- (NSString *)getAppId {
    return _appId;
}

- (void)jumpToAppStore {
    DDLogDebug(@"QQMusicWxmpAuthAgent.jumpToAppStore");
    [UrlUtil openUrl:[WXApi getWXAppInstallUrl]];
}

- (void)requestCPCredentialWithHandler:(void (^)(BOOL, NSInteger, NSString *, TVSCPCredential *))handler {
    DDLogDebug(@"QQMusicWxmpAuthAgent.requestCPCredentialWithHandler");
    _requestCPCredentialHandler = handler;
    WXLaunchMiniProgramReq * launchMiniProgramReq = [WXLaunchMiniProgramReq object];
    launchMiniProgramReq.userName = @"gh_1dac5028a5dd";
    launchMiniProgramReq.path = [NSString stringWithFormat:@"pages/auth/auth?appId=%@&packageName=%@&encryptString=%@", _appId, [[NSBundle mainBundle]bundleIdentifier], [self getEncryptedString]];
    launchMiniProgramReq.miniProgramType = WXMiniProgramTypeRelease;
    [WXApi sendReq:launchMiniProgramReq completion:^(BOOL success) {
        if (!success) {
            [self onAuthFailed:TVSCPErrorConnectingToAppFailure ErrorMsg:@"拉起小程序失败"];
        }
    }];
}

- (NSString *)getEncryptedString {
    NSTimeInterval time = [[NSDate date]timeIntervalSince1970];
    NSString * nonce = [NSString stringWithFormat:@"%.3f", time];
    //1.签名
    NSString * sign = [RSA signString:nonce privateKey:_secretKey];

    NSDictionary * signDict = @{
        kScheme_Nonce:nonce,
        kScheme_Sign:sign,
    };
    NSString * sourceString = [QQMusicUtils strWithJsonObject:signDict];
    //2.加密
    return [RSA encryptString:sourceString publicKey:QQMusic_PubKey];
}

- (void)onAuthSuccess:(NSString *)openID Token:(NSString *)openToken {
    DDLogDebug(@"QQMusicWxmpAuthAgent.onAuthSuccess openID:%@", openID);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(YES, 0, nil, [[TVSCPCredential alloc]initWithCP:TVSCPQQMusic andAppId:_appId andOpenId:openID andOpenToken:openToken andExpireTime:-1]);
    }
}

- (void)onAuthFailed:(NSInteger)errorCode ErrorMsg:(NSString *)errorMsg {
    DDLogDebug(@"QQMusicWxmpAuthAgent.onAuthFailed errorCode:%ld errorMsg:%@", (long)errorCode, errorMsg);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, errorCode, [NSString stringWithFormat:@"Failed with %@", errorMsg], nil);
    }
}
                          
- (BOOL)onResp:(BaseResp*)resp {
    DDLogDebug(@"QQMusicWxmpAuthAgent.onResp");
    if (!resp) {
        DDLogDebug(@"QQMusicWxmpAuthAgent.onResp resp:nil");
        return NO;
    }
    if (![resp isKindOfClass:[WXLaunchMiniProgramResp class]]) {
        DDLogDebug(@"QQMusicWxmpAuthAgent.onResp resp:![WXLaunchMiniProgramResp]");
        return NO;
    }
    DDLogDebug(@"QQMusicWxmpAuthAgent.onResp resp:[WXLaunchMiniProgramResp]");
    WXLaunchMiniProgramResp * rsp = (WXLaunchMiniProgramResp *)resp;
    [self handleEncryptString:rsp.extMsg];
    return YES;
}

- (void)handleEncryptString:(NSString *)encryptString {
    DDLogDebug(@"QQMusicWxmpAuthAgent.handleEncryptString encryptString:%@", encryptString);
    if ([encryptString length] == 0) {
        [self onAuthFailed:TVSCPErrorWxmpDecrypt ErrorMsg:@"空返回"];
        return;
    }
    NSString *decryptString = [RSA decryptString:encryptString privateKey:_secretKey];
    DDLogDebug(@"QQMusicWxmpAuthAgent.handleEncryptString decryptString:%@", decryptString);
    if ([decryptString length] == 0) {
        [self onAuthFailed:TVSCPErrorWxmpDecrypt ErrorMsg:@"解密结果为空"];
        return;
    }
    NSDictionary *decryptDict = [QQMusicUtils objectWithJsonData:[decryptString dataUsingEncoding:NSUTF8StringEncoding] error:nil targetClass:[NSDictionary class]];
    DDLogDebug(@"QQMusicWxmpAuthAgent.handleEncryptString decryptDict:%@", decryptDict);
    NSString *nonce = [decryptDict objectForKey:kScheme_Nonce];
    NSString *sign = [decryptDict objectForKey:kScheme_Sign];
    if ([sign length] == 0 || [nonce length] == 0) {
        [self onAuthFailed:TVSCPErrorWxmpNoData ErrorMsg:@"缺少字段"];
        return;
    }
    if (![RSA verify:nonce signature:sign pubKey:QQMusic_PubKey]) {
        [self onAuthFailed:TVSCPErrorWxmpVerify ErrorMsg:@"校验失败"];
        return;
    }
    //验证通过
    NSString *openID = [decryptDict objectForKey:kScheme_OpenId];
    NSString *openToken = [decryptDict objectForKey:kScheme_OpenToken];
    DDLogDebug(@"QQMusicWxmpAuthAgent.handleEncryptString OpenID:%@ OpenToken:%@",openID,openToken);
    [self onAuthSuccess:openID Token:openToken];
}

@end
