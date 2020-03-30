//
//  NSObject+QQMusicWxmpAuthAgent.h
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/1/8.
//  Copyright © 2020 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSThirdPartyAuth.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DialogDelegate <NSObject>

- (void)showLoadingDialog;

- (void)hideLoadingDialog;

@end

@interface QQMusicQqmpAuthAgent : NSObject <TVSCPAuthAgent>

@property(nonatomic,strong) TVSDeviceInfo * deviceInfo;
@property(nonatomic,weak) id<DialogDelegate> dialogDelegate;

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey andDevName:(NSString *)devName;

- (BOOL)willEnterForeground;

@end

NS_ASSUME_NONNULL_END
