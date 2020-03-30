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
#import "TencentOpenAPI/TencentOAuth.h"
#import "QQMusicWxmpAuthAgent.h"

#define WECHAT_APP_ID @"wxdbd76c1af795f58e"
#define WECHAT_UNIVERSAL_LINK @"https://s58vdf.coding-pages.com/wechat/"
#define QQ_APP_ID @"101470979"

@interface SdkLoginProxy()<OpenSdkLoginDelegate, WXApiDelegate, TencentSessionDelegate>

@property (nonatomic, strong) void (^weChatLoginHandler)(TVSAuthResult, NSString * _Nullable);

@property (nonatomic, strong) TencentOAuth * qqAuth;
@property (nonatomic, strong) NSString * qqOpenId;
@property (nonatomic, strong) NSString * qqAccessToken;
@property (nonatomic, strong) void (^qqLoginHandler)(TVSAuthResult, NSString * _Nullable, NSString * _Nullable, NSString * _Nullable, NSString * _Nullable);

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
        // Register QQ
        _qqAuth = [[TencentOAuth alloc]initWithAppId:QQ_APP_ID andDelegate:self];
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
    if ([TencentOAuth HandleOpenURL:url]) {
        return YES;
    }
    return NO;
}

- (BOOL)handleContinueUserActivity:(NSUserActivity *)userActivity {
    return [WXApi handleOpenUniversalLink:userActivity delegate:self];
}

#pragma mark OpenSdkLoginDelegate

- (void)doWeChatLoginForViewController:(UIViewController *)viewController withHandler:(void (^)(TVSAuthResult, NSString * _Nullable))handler {
    _weChatLoginHandler = handler;
    if (![[TVSAuthManager shared]checkWXAppWithAlert:YES]) {
        handler(TVSAuthResultFailedOther, nil);
        return;
    }
    SendAuthReq * req = [SendAuthReq new];
    req.scope = @"snsapi_userinfo";
    req.state = @"App";
    void(^completionHandler)(BOOL) = ^(BOOL success) {
        if (!success) {
            handler(TVSAuthResultFailedOther, nil);
        }
    };
    if (viewController) {
        [WXApi sendAuthReq:req viewController:viewController delegate:self completion:completionHandler];
    } else {
        [WXApi sendReq:req completion:completionHandler];
    }
}

- (BOOL)isWXAppInstalled {
    return [WXApi isWXAppInstalled];
}

- (BOOL)isWXAppSupportApi {
    return [WXApi isWXAppSupportApi];
}

- (NSString *)getWXAppInstallUrl {
    return [WXApi getWXAppInstallUrl];
}

- (void)doQqLoginWithHandler:(void (^)(TVSAuthResult, NSString * _Nullable, NSString * _Nullable, NSString * _Nullable, NSString * _Nullable))handler {
    _qqLoginHandler = handler;
    BOOL res = [_qqAuth authorize:@[@"get_user_info",@"get_simple_userinfo", @"add_t"] inSafari:NO];
    DDLogInfo(@"SdkLoginProxy.doQqLoginWithHandler authorizeResult:%d", res);
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
    // Handle WeChat Login
    if ([resp isKindOfClass:[SendAuthResp class]]) {
        NSString * code = ((SendAuthResp*)resp).code;
        if (_weChatLoginHandler) {
            _weChatLoginHandler(TVSAuthResultSuccess, code);
        }
    }
}

#pragma mark TencentSessionDelegate

- (void)tencentDidLogin {
    DDLogInfo(@"SdkLoginProxy.tencentDidLogin openId:%@ accesstoken:%@", _qqAuth.openId, _qqAuth.accessToken);
    if ([_qqAuth.openId length] != 0 && [_qqAuth.accessToken length] != 0) {
        _qqOpenId = _qqAuth.openId;
        _qqAccessToken = _qqAuth.accessToken;
        [_qqAuth getUserInfo];
    }
}

- (void)getUserInfoResponse:(APIResponse*) response {
    DDLogInfo(@"SdkLoginProxy.getUserInfoResponse response:%@", response);
    if (response && response.message) {
        NSDictionary * dict;
        NSError * err;
        NSData * data = [response.message dataUsingEncoding:NSUTF8StringEncoding];
        if (data) {
            dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&err];
        }
        DDLogInfo(@"SdkLoginProxy.getUserInfoResponse dict:%@", dict);
        NSString * nickname;
        NSString * avatarUrl;
        if (dict) {
            nickname = dict[@"nickname"];
            avatarUrl = dict[@"figureurl_qq_2"];
        }
        if (_qqLoginHandler) {
            _qqLoginHandler(TVSAuthResultSuccess, _qqOpenId, _qqAccessToken, nickname, avatarUrl);
        }
    } else {
        if (_qqLoginHandler) {
            _qqLoginHandler(TVSAuthResultFailedOther, nil, nil, nil, nil);
        }
    }
}

- (void)tencentDidNotLogin:(BOOL)cancelled {
    DDLogInfo(@"tencentDidNotLogin cancelled:%d", cancelled);
    if (_qqLoginHandler) {
        _qqLoginHandler(TVSAuthResultFailedNotLogin, nil, nil, nil, nil);
    }
}

- (void)tencentDidNotNetWork {
    DDLogInfo(@"tencentDidNotNetWork");
    if (_qqLoginHandler) {
        _qqLoginHandler(TVSAuthResultFailedNetwork, nil, nil, nil, nil);
    }
}

@end
