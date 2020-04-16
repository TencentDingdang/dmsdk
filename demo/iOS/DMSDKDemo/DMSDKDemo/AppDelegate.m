//
//  AppDelegate.m
//  TvsLoginDemo
//
//  Created by Rinc Liu on 2019/1/28.
//  Copyright © 2019 tencent. All rights reserved.
//

#import <TVSCore/TVSCore.h>
#import "AppDelegate.h"
#import "BrowserVC.h"
#import "SdkLoginProxy.h"
#import "CPAuthAgents/QQMusicAuthAgent.h"
#import "CPAuthAgents/QQMusicWxmpAuthAgent.h"
#import "CPAuthAgents/QQMusicQqmpAuthAgent.h"

// 请输入您在qq音乐部门申请的私钥
#define QQ_MUSIC_SECRET_KEY @""
// 请输入您在qq音乐部门申请的appid
#define QQ_MUSIC_APP_ID @""
// 请配置回调URL
#define QQ_MUSIC_CALLBACK_URL @"qqmusiciosdemo://"
// 请设置应用名，将在Q音授权时显示
#define QQ_MUSIC_DEV_NAME @"腾讯云小微"

@implementation AppDelegate

//SDK 初始化
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // 初始化微信SDK，为QQ音乐微信小程序授权做准备，如果不需要微信小程序授权，可以删除
    [[SdkLoginProxy shared] registerApp];
    
    // DM SDK默认启用了异常上报，若有需要可以关闭
//    [TVSEnvironment shared].enableDiagnosis = NO;
    [[TVSEnvironment shared]enableLog];//开启日志
    [[TVSAuthManager shared]registerAppWithAppKey:YXW_APP_KEY];//读取配置信息
    
    NSLog(@"YOUR Product id is %@", YXW_PRODUCT_ID);

    // 在这里注入QQ音乐授权实现到DMSDK，注意参数中填入您申请的QQ音乐AppID、密钥和配置对应的回调URL
    NSMutableDictionary<NSString *, id<TVSCPAuthAgent>> * agentMap = [NSMutableDictionary dictionary];
    agentMap[QQ_MUSIC_AUTH_TYPE_APP] = [[QQMusicAuthAgent alloc]initWithAppId:QQ_MUSIC_APP_ID andSecretKey:QQ_MUSIC_SECRET_KEY andCallbackUrl:QQ_MUSIC_CALLBACK_URL];
    agentMap[QQ_MUSIC_AUTH_TYPE_WEIXIN] = [[QQMusicWxmpAuthAgent alloc]initWithAppId:QQ_MUSIC_APP_ID andSecretKey:QQ_MUSIC_SECRET_KEY];
    agentMap[QQ_MUSIC_AUTH_TYPE_QQ] = [[QQMusicQqmpAuthAgent alloc]initWithAppId:QQ_MUSIC_APP_ID andSecretKey:QQ_MUSIC_SECRET_KEY andDevName:QQ_MUSIC_DEV_NAME];
    [[TVSCPAuthAgentManager shared]setAgentMap:agentMap ofCP:TVSCPQQMusic];
    return YES;
}

//处理 微信/QQ 等 URL 跳转
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {
    DDLogDebug(@"AppDelegate.openURL");
    if ([[SdkLoginProxy shared]handleOpenUrl:url]) {
        return YES;
    }
    if ([QQMusicOpenSDK handleOpenURL:url]) {
        DDLogDebug(@"AppDelegate.openURL Handled by QQMusicOpenSDK");
        return YES;
    }
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
    DDLogDebug(@"AppDelegate.applicationWillEnterForeground");
    id agent = [[TVSCPAuthAgentManager shared]getAgentOfCP:TVSCPQQMusic andAuthType:QQ_MUSIC_AUTH_TYPE_QQ];
    if (agent && [agent isKindOfClass:[QQMusicQqmpAuthAgent class]]) {
        QQMusicQqmpAuthAgent * qqmpAgent = agent;
        if ([qqmpAgent willEnterForeground]) {
            DDLogDebug(@"AppDelegate.applicationWillEnterForeground Handled by QQMusicQqmpAuthAgent");
            return;
        }
    }
}


- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> *restorableObjects))restorationHandler {
    return YES;
}

@end
