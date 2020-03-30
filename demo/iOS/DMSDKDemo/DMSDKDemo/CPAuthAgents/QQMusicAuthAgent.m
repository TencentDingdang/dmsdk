//
//  QQMusicAuthAgent.m
//  DMSDKDemo
//
//  Created by ZACARDFANG on 2019/8/11.
//  Copyright © 2019年 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QQMusicAuthAgent.h"
#import "UrlUtil.h"

@interface QQMusicAuthAgent ()

@property(nonatomic,readonly) NSString * appId;

@property(nonatomic,readonly) NSString * secretKey;

@property(nonatomic,readonly) NSString * callbackUrl;

@property(nonatomic,copy) void(^requestCPCredentialHandler)(BOOL success, NSInteger errCode, NSString * displayMessage, TVSCPCredential * credential);

@end

@implementation QQMusicAuthAgent

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey andCallbackUrl:(NSString *)callbackUrl {
    if (self = [super init]) {
        _appId = appId;
        _secretKey = secretKey;
        _callbackUrl = callbackUrl;
        [QQMusicOpenSDK registerAppID:_appId packageName:[[NSBundle mainBundle] bundleIdentifier] SecretKey:_secretKey callbackUrl:_callbackUrl delegate:self];
    }
    return self;
}

- (BOOL)checkCPInstalled {
    DDLogDebug(@"QQMusicAuthAgent.checkCPInstalled");
    return [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"qqmusic://qq.com"]];
}

- (NSString *)getAppId {
    return _appId;
}

- (void)jumpToAppStore {
    DDLogDebug(@"QQMusicAuthAgent.jumpToAppStore");
    [UrlUtil openUrl:[NSURL URLWithString:[NSString stringWithFormat:@"itms-appss://itunes.apple.com/us/app/apple-store/id%@?mt=8", @"414603431"]].absoluteString];
}

- (void)requestCPCredentialWithHandler:(void (^)(BOOL, NSInteger, NSString *, TVSCPCredential *))handler {
    DDLogDebug(@"QQMusicAuthAgent.requestCPCredentialWithHandler");
    _requestCPCredentialHandler = handler;
    [QQMusicOpenSDK startAuth];
}

- (void)onAuthSuccess:(NSString *)openID Token:(NSString *)openToken {
    DDLogDebug(@"QQMusicAuthAgent.onAuthSuccess openID:%@", openID);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(YES, 0, nil, [[TVSCPCredential alloc]initWithCP:TVSCPQQMusic andAppId:_appId andOpenId:openID andOpenToken:openToken andExpireTime:-1]);
    }
}

- (void)onAuthFailed:(NSInteger)errorCode ErrorMsg:(NSString *)errorMsg {
    DDLogDebug(@"QQMusicAuthAgent.onAuthFailed errorCode:%ld errorMsg:%@", (long)errorCode, errorMsg);
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, TVSCPErrorRequestingAuthFailure, [NSString stringWithFormat:@"Failed with %ld:%@", (long)errorCode, errorMsg], nil);
    }
}

- (void)onAuthCancel {
    DDLogDebug(@"QQMusicAuthAgent.onAuthCancel");
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, TVSCPErrorUserCancellation, @"", nil);
    }
}

- (void)traceLog:(NSString *)log level:(QQMusicLogLevel)level {
    DDLogDebug(@"%@", log);
}

@end
