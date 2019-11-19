//
//  QQMusicAuthAgent.m
//  DMSDKDemo
//
//  Created by ZACARDFANG on 2019/8/11.
//  Copyright © 2019年 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "QQMusicAuthAgent.h"

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
    return [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"qqmusic://qq.com"]];
}

- (NSString *)getAppId {
    return _appId;
}

- (void)jumpToAppStore {
    [self openUrl:[NSURL URLWithString:[NSString stringWithFormat:@"itms-appss://itunes.apple.com/us/app/apple-store/id%@?mt=8", @"414603431"]].absoluteString];
}

- (void)requestCPCredentialWithHandler:(void (^)(BOOL, NSInteger, NSString *, TVSCPCredential *))handler {
    _requestCPCredentialHandler = handler;
    [QQMusicOpenSDK startAuth];
}

- (void)onAuthSuccess:(NSString *)openID Token:(NSString *)openToken {
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(YES, 0, nil, [[TVSCPCredential alloc]initWithCP:TVSCPQQMusic andAppId:_appId andOpenId:openID andOpenToken:openToken andExpireTime:-1]);
    }
}

- (void)onAuthFailed:(NSInteger)errorCode ErrorMsg:(NSString *)errorMsg {
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, TVSCPErrorRequestingAuthFailure, [NSString stringWithFormat:@"Failed with %ld:%@", (long)errorCode, errorMsg], nil);
    }
}

- (void)onAuthCancel {
    if (_requestCPCredentialHandler) {
        _requestCPCredentialHandler(NO, TVSCPErrorUserCancellation, @"", nil);
    }
}

- (void)traceLog:(NSString *)log level:(QQMusicLogLevel)level {
    DDLogDebug(@"%@", log);
}

-(void)openUrl:(NSString *)urlStr {
    if (urlStr && urlStr.length > 0) {
        // DDLogDebug(@"OpenUrl:%@", urlStr);
        NSURL* url = [NSURL URLWithString:urlStr];
        UIApplication *app = [UIApplication sharedApplication];
        if ([app canOpenURL:url]) {
            if ([[[UIDevice currentDevice] systemVersion] compare:@"10.0" options:NSNumericSearch] != NSOrderedAscending) {
                if (@available(iOS 10.0, *)) {
                    [app openURL:url options:@{} completionHandler:^(BOOL success) {
                    }];
                }
            } else {
                [app openURL:url];
            }
            return;
        }
    }
}

@end
