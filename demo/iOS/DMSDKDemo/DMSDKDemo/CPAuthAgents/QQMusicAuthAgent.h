//
//  QQMusicAuthAgent.h
//  DMSDKDemo
//
//  Created by perqinxie on 2019/8/11.
//  Copyright © 2019年 tencent. All rights reserved.
//

#import <TVSTSKM/TVSThirdPartyAuth.h>
#import <QQMusicOpenSDK.h>

@interface QQMusicAuthAgent : NSObject <TVSCPAuthAgent,QQMusicOpenSDKDelegate>

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey andCallbackUrl:(NSString *)callbackUrl;

@end
