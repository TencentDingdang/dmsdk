//
//  NSObject+QQMusicWxmpAuthAgent.h
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/1/8.
//  Copyright © 2020 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSThirdPartyAuth.h>
#import "WxApi.h"

NS_ASSUME_NONNULL_BEGIN

@interface QQMusicWxmpAuthAgent : NSObject <TVSCPAuthAgent>

- (instancetype)initWithAppId:(NSString *)appId andSecretKey:(NSString *)secretKey;

- (BOOL)onResp:(BaseResp *)resp;

@end

NS_ASSUME_NONNULL_END
