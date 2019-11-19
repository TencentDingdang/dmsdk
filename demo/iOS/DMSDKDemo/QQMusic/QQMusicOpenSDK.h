//
//  QQMusicOpenSDK.h
//  QQMusicOpenSDK
//
//  Created by travisli(李鞠佑) on 2018/10/18.
//  Copyright © 2018年 腾讯音乐. All rights reserved.
//

#import <Foundation/Foundation.h>

//QQMusicOpenSDK版本号
extern NSString* const QQMusicOpenSDKVersion;

typedef NS_ENUM(NSInteger, QQMusicLogLevel) {
    QQMusicLogLevelDebug     = 0,
    QQMusicLogLevelInfo      = 1,
    QQMusicLogLevelWarning   = 2,
    QQMusicLogLevelError     = 3,
};

@protocol QQMusicOpenSDKDelegate <NSObject>

/**
 授权成功回调

 @param openID QQ音乐分配的OpenID
 @param openToken QQ音乐分配的OpenID
 */
- (void)onAuthSuccess:(NSString*)openID Token:(NSString*)openToken;


/**
 授权成功失败

 @param errorCode 错误码
 @param errorMsg 错误信息
 */
- (void)onAuthFailed:(NSInteger)errorCode ErrorMsg:(NSString*)errorMsg;


/**
 取消授权
 */
- (void)onAuthCancel;


/**
 SDK日志回调

 @param log 日志
 @param level 日志等级
 */
- (void)traceLog:(NSString *)log level:(QQMusicLogLevel)level;

@end


/**
 QQMusicOpenSDK
 */
@interface QQMusicOpenSDK : NSObject


/**
 注册AppID

 @param appId QQ音乐分配的appId
 @param packageName 第三方app bundleID
 @param secretKey QQ音乐分配的密钥
 @param callbackUrl QQ音乐授权后拉起App的scheme
 @param delegate SDK回调
 */
+ (BOOL)registerAppID:(NSString*)appId packageName:(NSString *)packageName SecretKey:(NSString*)secretKey callbackUrl:(NSString*)callbackUrl delegate:(id<QQMusicOpenSDKDelegate>)delegate;


/**
 检查QQ音乐是否已安装

 @return 安装返回YES，否则返回NO
 */
+ (BOOL)isQQMusicInstalled;


/**
 打开QQ音乐

 @return 成功返回YES，否则返回NO
 */
+ (BOOL)openQQMusicApp;



/**
 拉起QQ音乐授权

 @return 成功返回YES，否则返回NO
 */
+ (BOOL)startAuth;


/**
 AppStore安装链接
 
 @return 返回url
 */
+ (NSString*)getQQMusicInstallUrl;


/**
 处理客户端程序通过URL启动第三方应用时传递的数据
 
 需要在 application:openURL:sourceApplication:annotation:或者application:handleOpenURL中调用
 @param url 启动第三方应用的URL
 */
+ (BOOL)handleOpenURL:(NSURL *)url;



@end
