//
//  SdkLoginProxy.h
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/2/28.
//  Copyright © 2020 tencent. All rights reserved.
//

#ifndef SdkLoginProxy_h
#define SdkLoginProxy_h

@interface SdkLoginProxy : NSObject

+ (instancetype)shared;

- (void)registerApp;

- (BOOL)handleOpenUrl:(NSURL *)url;

@end

#endif /* SdkLoginProxy_h */
