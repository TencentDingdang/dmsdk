//
//  SdkLoginProxy.m
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/2/28.
//  Copyright © 2020 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSAuth.h>
#import <TVSCore/TVSThirdPartyAuth.h>
#import "SdkLoginProxy.h"
#import "WechatAuthSDK.h"
#import "WXApi.h"
#import "QQMusicWxmpAuthAgent.h"

// 这里的微信sdk app id和link，均为demo专用，您需要填写自己申请的id和link
#define WECHAT_APP_ID @"wxdbd76c1af795f58e"
#define WECHAT_UNIVERSAL_LINK @"https://s58vdf.coding-pages.com/wechat/"

@interface SdkLoginProxy()<WXApiDelegate>

@end

@implementation SdkLoginProxy

+ (instancetype)shared {
    static id instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (instance == nil) {
            instance = [self new];
        }
    });
    return instance;
}

- (instancetype)init {
    if (self = [super init]) {
    }
    return self;
}

- (void)registerApp {
    // Register WeChat
    if ([WXApi registerApp:WECHAT_APP_ID universalLink:WECHAT_UNIVERSAL_LINK]) {
        DDLogDebug(@"SdkLoginProxy.registerApp Fail to register WXApi");
    }
}

- (BOOL)handleOpenUrl:(NSURL *)url {
    if ([WXApi handleOpenURL:url delegate:self]) {
        return YES;
    }
    return NO;
}

- (BOOL)handleContinueUserActivity:(NSUserActivity *)userActivity {
    return [WXApi handleOpenUniversalLink:userActivity delegate:self];
}

#pragma mark WXApiDelegate

- (void)onReq:(BaseReq *)req {}

- (void)onResp:(BaseResp *)resp {
    DDLogDebug(@"SdkLoginProxy.onResp");
    if (!resp) {
        DDLogDebug(@"SdkLoginProxy.onResp resp:nil");
        return;
    }
    // Handle QQ Music WeChat MiniProgram Auth
    id agent = [[TVSCPAuthAgentManager shared]getAgentOfCP:TVSCPQQMusic andAuthType:QQ_MUSIC_AUTH_TYPE_WEIXIN];
    if (agent && [agent isKindOfClass:[QQMusicWxmpAuthAgent class]]) {
        QQMusicWxmpAuthAgent * wxmpAgent = agent;
        if ([wxmpAgent onResp:resp]) {
            DDLogDebug(@"SdkLoginProxy.onResp Handled by QQMusicWxmpAuthAgent");
            return;
        }
    }
}

@end
