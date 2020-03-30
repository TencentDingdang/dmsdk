//
//  SdkLoginProxy.h
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/2/28.
//  Copyright © 2020 tencent. All rights reserved.
//

#ifndef SdkLoginProxy_h
#define SdkLoginProxy_h

@interface SdkLoginProxy : NSObject<OpenSdkLoginDelegate>

+ (instancetype)shared;

- (void)registerApp;

- (BOOL)handleOpenUrl:(NSURL *)url;

- (BOOL)handleContinueUserActivity:(NSUserActivity *)userActivity;

@end

#endif /* SdkLoginProxy_h */
