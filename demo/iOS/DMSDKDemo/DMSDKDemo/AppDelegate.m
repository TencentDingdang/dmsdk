//
//  AppDelegate.m
//  TvsLoginDemo
//
//  Created by Rinc Liu on 2019/1/28.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "AppDelegate.h"
#import <TVSCore/TVSCore.h>
#import "BrowserVC.h"
#import "CPAuthAgents/QQMusicAuthAgent.h"
#import <TencentOpenAPI/TencentOAuth.h>

#define QQ_MUSIC_SECRET_KEY @""
#define QQ_MUSIC_APP_ID @""
#define QQ_MUSIC_CALLBACK_URL @"qqmusic://"

@implementation AppDelegate

//SDK 初始化
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // DM SDK默认启用了异常上报，若有需要可以关闭
//    [TVSEnvironment shared].enableDiagnosis = NO;
    [[TVSEnvironment shared]enableLog];//开启日志
    [[TVSAuthManager shared]registerApp];//读取配置信息
    // 在这里注入QQ音乐授权实现到DMSDK，注意参数中填入您申请的QQ音乐AppID、密钥和配置对应的回调URL
    [[TVSCPAuthAgentManager shared]setAgent:[[QQMusicAuthAgent alloc]initWithAppId:QQ_MUSIC_APP_ID andSecretKey:QQ_MUSIC_SECRET_KEY andCallbackUrl:QQ_MUSIC_CALLBACK_URL] ofCP:TVSCPQQMusic];
    return YES;
}

//处理 微信/QQ 等 URL 跳转
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    // 处理微信/QQ 登录跳转
    if ([[TVSAuthManager shared] handleOpenUrl:url]) return YES;
    // 处理云叮当 APP 授权后的 URL 回跳
    /*if ([url.host isEqualToString:@"tvs-auth"] && [url.query isEqualToString:@"result=0"]) {
        // 打开第三方授权网页
        BrowserVC* bv = [BrowserVC new];
        bv.pageType = TVSWebPageTypeThirdPartyAuth;
        [(UINavigationController*)(self.window.rootViewController) pushViewController:bv animated:YES];
    }*/
    if ([QQMusicOpenSDK handleOpenURL:url]) return YES;
    return NO;
}


- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
}


- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}


- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> *restorableObjects))restorationHandler {
    // Copied from QQ Open SDK's demo code
    // Demo处理手Q UniversalLink回调的示例代码
    if([userActivity.activityType isEqualToString:NSUserActivityTypeBrowsingWeb]) {
        NSURL *url = userActivity.webpageURL;
        if(url && [TencentOAuth CanHandleUniversalLink:url]) {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"UniversalLink" message:url.description delegate:nil cancelButtonTitle:@"ok" otherButtonTitles:nil, nil];
            [alertView show];
#if BUILD_QQAPIDEMO
            // 兼容[QQApiInterface handleOpenURL:delegate:]的接口回调能力
            [QQApiInterface handleOpenUniversallink:url delegate:(id<QQApiInterfaceDelegate>)[QQApiShareEntry class]];
#endif
            return [TencentOAuth HandleUniversalLink:url];
        }
    }
    return YES;
}

@end
