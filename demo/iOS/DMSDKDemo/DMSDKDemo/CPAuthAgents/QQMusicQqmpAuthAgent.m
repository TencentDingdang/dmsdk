//
//  NSObject+QQMusicWxmpAuthAgent.m
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/1/8.
//  Copyright © 2020 tencent. All rights reserved.
//

#import <CommonCrypto/CommonDigest.h>
#import <TVSCore/TVSTSKMProxy.h>
#import "QQMusicQqmpAuthAgent.h"
#import "UrlUtil.h"
#import "WXApi.h"
#import "WXApiObject.h"
#import "RSA.h"
#import "QQMusicUtils.h"

#define DOMAIN_QQ_MUSIC_CUSTOM @"fcg_music_custom"
#define INTENT_GET_MP_URL @"sdk_get_qr_code"
#define INTENT_GET_AUTH_RESULT @"qrcode_auth_poll"
#define AUTH_RESULT_POLLING_INTERVAL 1
#define AUTH_RESULT_POLLING_TIMEOUT 60

static NSString * const kScheme_Nonce = @"nonce";
static NSString * const kScheme_Sign = @"sign";
static NSString * const kScheme_OpenId= @"openId";
static NSString * const kScheme_OpenToken= @"openToken";
static NSString * const QQMusic_PubKey = @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCrp4sMcJjY9hb2J3sHWlwIEBrJlw2Cimv+rZAQmR8V3EI+0PUK14pL8OcG7CY79li30IHwYGWwUapADKA01nKgNeq7+rSciMYZv6ByVq+ocxKY8az78HwIppwxKWpQ+ziqYavvfE5+iHIzAc8RvGj9lL6xx1zhoPkdaA0agAyuMQIDAQAB";

@interface QQMusicQqmpAuthAgent ()

@property(nonatomic,readonly) NSString * appId;

@property(nonatomic,readonly) NSString * secretKey;

@property(nonatomic,readonly) NSString * devName;

@property(nonatomic, assign) BOOL pendingLaunch;

@property(nonatomic,readonly) NSString * authCode;

@property(nonatomic,strong) NSDate * startTime;

@property(nonatomic,copy) void(^requestCPCredentialHandler)(BOOL success, NSInteger errCode, NSString * displayMessage, TVSCPCredential * credential);

@end

@implementation QQMusicQqmpAuthAgent

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey andDevName:(NSString *)devName {
    if (self = [super init]) {
        _appId = appId;
        _secretKey = secretKey;
        _pendingLaunch = NO;
        _devName = devName;
    }
    return self;
}

- (BOOL)checkCPInstalled {
    // We can't check whether the URL can be handled by QQ app here, because the URL is sent by the backend
    return YES;
}

- (NSString *)getAppId {
    return _appId;
}

- (void)jumpToAppStore {
    DDLogDebug(@"QQMusicQqmpAuthAgent.jumpToAppStore");
    [UrlUtil openUrl:[NSURL URLWithString:[NSString stringWithFormat:@"itms-appss://itunes.apple.com/us/app/apple-store/id%@?mt=8", @"444934666"]].absoluteString];
}

- (void)requestCPCredentialWithHandler:(void (^)(BOOL, NSInteger, NSString *, TVSCPCredential *))handler {
    DDLogDebug(@"QQMusicQqmpAuthAgent.requestCPCredentialWithHandler");
    _requestCPCredentialHandler = handler;
    // Send request for QR Code
    [self getQqmpUrl];
}

- (void)getQqmpUrl {
    DDLogDebug(@"QQMusicQqmpAuthAgent.getQqmpUrl");
    NSMutableDictionary * blobInfo = [NSMutableDictionary dictionary];
    blobInfo[@"qqmusic_open_appid"] = _appId;
    blobInfo[@"qqmusic_package_name"] = [[NSBundle mainBundle] bundleIdentifier];
    blobInfo[@"qqmusic_dev_name"] = _devName;
    blobInfo[@"qqmusic_encrypt_auth"] = [self getEncryptedString];
    blobInfo[@"qqmusic_qrcode_type"] = @"qq";
    [[[TVSTSKMProxy alloc]initWithDeviceInfo:_deviceInfo]uniAccessWithDomain:DOMAIN_QQ_MUSIC_CUSTOM intent:INTENT_GET_MP_URL blobInfo:blobInfo handler:^(BOOL successful, NSInteger retCode, NSDictionary * _Nullable resultDict) {
        if (retCode != 0) {
            [self onAuthFailed:TVSCPErrorQqmpGetUrlRequest ErrorMsg:[NSString stringWithFormat:@"code:%ld", (long)retCode]];
            return;
        }
        NSNumber * retNumber = resultDict[@"ret"];
        if (retNumber.integerValue != 0) {
            NSNumber * subRetNumber = resultDict[@"sub_ret"];
            NSString * msg = resultDict[@"msg"];
            [self onAuthFailed:TVSCPErrorQqmpGetUrlResponse ErrorMsg:[NSString stringWithFormat:@"ret:%ld sub_ret:%ld msg:%@", retNumber.integerValue, subRetNumber.integerValue, msg]];
            return;
        }
        self->_authCode = resultDict[@"auth_code"];
        NSString * url = resultDict[@"sdk_qr_code"];
        DDLogDebug(@"QQMusicQqmpAuthAgent.getQqmpUrl sdk_qr_code:%@", url);
        [[UIApplication sharedApplication]openURL:[NSURL URLWithString:url] options:@{} completionHandler:^(BOOL success) {
            if (!success) {
                [self onAuthFailed:TVSCPErrorNotInstalled ErrorMsg:[NSString stringWithFormat:@"Fail to open %@", url]];
                return;
            }
            self->_pendingLaunch = YES;
        }];
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
    DDLogDebug(@"QQMusicQqmpAuthAgent.onAuthSuccess openID:%@", openID);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(YES, 0, nil, [[TVSCPCredential alloc]initWithCP:TVSCPQQMusic andAppId:_appId andOpenId:openID andOpenToken:openToken andExpireTime:-1]);
    }
}

- (void)onAuthFailed:(NSInteger)errorCode ErrorMsg:(NSString *)errorMsg {
    DDLogDebug(@"QQMusicQqmpAuthAgent.onAuthFailed errorCode:%ld errorMsg:%@", (long)errorCode, errorMsg);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, errorCode, [NSString stringWithFormat:@"Failed with %@", errorMsg], nil);
    }
}

- (BOOL)willEnterForeground {
    DDLogDebug(@"QQMusicQqmpAuthAgent.willEnterForeground");
    if (!_pendingLaunch) {
        DDLogDebug(@"QQMusicQqmpAuthAgent.willEnterForeground _pendingLaunch:NO");
        return NO;
    }
    _pendingLaunch = NO;
    if (_dialogDelegate) {
        [_dialogDelegate showLoadingDialog];
    }
    // Start polling
    _startTime = [NSDate date];
    [self pollAuthResult];
    return YES;
}

- (void)pollAuthResult {
    DDLogDebug(@"QQMusicQqmpAuthAgent.pollAuthResult");
    NSMutableDictionary * blobInfo = [NSMutableDictionary dictionary];
    blobInfo[@"qqmusic_openid_appId"] = _appId;
    blobInfo[@"qqmusic_openid_authCode"] = _authCode;
    [[[TVSTSKMProxy alloc]initWithDeviceInfo:_deviceInfo]uniAccessWithDomain:DOMAIN_QQ_MUSIC_CUSTOM intent:INTENT_GET_AUTH_RESULT blobInfo:blobInfo handler:^(BOOL successful, NSInteger retCode, NSDictionary * _Nullable resultDict) {
        if (retCode != 0) {
            [self onPollAuthResultError:TVSCPErrorQqmpGetResultRequest errorMsg:[NSString stringWithFormat:@"code:%ld", (long)retCode]];
            return;
        }
        NSNumber * retNumber = resultDict[@"ret"];
        if (retNumber.integerValue != 0) {
            NSNumber * subRetNumber = resultDict[@"sub_ret"];
            NSString * msg = resultDict[@"msg"];
            [self onPollAuthResultError:TVSCPErrorQqmpGetResultResponse errorMsg:[NSString stringWithFormat:@"ret:%ld sub_ret:%ld msg:%@", retNumber.integerValue, subRetNumber.integerValue, msg]];
            return;
        }
        [self onPollAuthResultSuccess:resultDict[@"encryptString"]];
    }];
}

- (void)onPollAuthResultSuccess:(NSString *)encryptString {
    DDLogDebug(@"QQMusicQqmpAuthAgent.onPollAuthResultSuccess");
    // Disable polling
    _startTime = nil;
    if (_dialogDelegate) {
        [_dialogDelegate hideLoadingDialog];
    }
    // Start standard decoding
    if ([encryptString length] == 0) {
        [self onAuthFailed:TVSCPErrorQqmpGetResultDecrypt ErrorMsg:@"空返回"];
        return;
    }
    NSString *decryptString = [RSA decryptString:encryptString privateKey:_secretKey];
    if ([decryptString length] == 0) {
        [self onAuthFailed:TVSCPErrorQqmpGetResultDecrypt ErrorMsg:@"解密结果为空"];
        return;
    }
    NSDictionary *decryptDict = [QQMusicUtils objectWithJsonData:[decryptString dataUsingEncoding:NSUTF8StringEncoding] error:nil targetClass:[NSDictionary class]];
    NSString *nonce = [decryptDict objectForKey:kScheme_Nonce];
    NSString *sign = [decryptDict objectForKey:kScheme_Sign];
    if ([sign length] == 0 || [nonce length] == 0) {
        [self onAuthFailed:TVSCPErrorQqmpGetResultNoData ErrorMsg:@"缺少字段"];
        return;
    }
    if (![RSA verify:nonce signature:sign pubKey:QQMusic_PubKey]) {
        [self onAuthFailed:TVSCPErrorQqmpGetResultVerify ErrorMsg:@"校验失败"];
        return;
    }
    //验证通过
    NSString *openID = [decryptDict objectForKey:kScheme_OpenId];
    NSString *openToken = [decryptDict objectForKey:kScheme_OpenToken];
    NSLog(@"验证通过 OpenID:%@,OpenToken:%@",openID,openToken);
    [self onAuthSuccess:openID Token:openToken];
}

- (void)onPollAuthResultError:(NSInteger)errorCode errorMsg:(NSString *)errorMsg {
    DDLogDebug(@"QQMusicQqmpAuthAgent.onPollAuthResultError errorCode:%ld errorMsg:%@", (long)errorCode, errorMsg);
    // Check timeout here
    if (!_startTime) {
        // When _startTime == nil, the polling may be canceled.
        return;
    }
    if ([[NSDate date]timeIntervalSinceDate:self->_startTime] > AUTH_RESULT_POLLING_TIMEOUT) {
        // Disable polling
        DDLogDebug(@"QQMusicQqmpAuthAgent.onPollAuthResultError Finish polling due to timeout");
        _startTime = nil;
        if (_dialogDelegate) {
            [_dialogDelegate hideLoadingDialog];
        }
        // Fail
        [self onAuthFailed:errorCode ErrorMsg:errorMsg];
        return;
    }
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(AUTH_RESULT_POLLING_INTERVAL * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self pollAuthResult];
    });
}

@end
