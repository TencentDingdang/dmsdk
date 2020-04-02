//
//  UrlUtil.m
//  DMSDKDemo
//
//  Created by Perqin Xie on 2020/2/26.
//  Copyright © 2020 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIApplication.h>
#import "UrlUtil.h"

@implementation UrlUtil

+ (void)openUrl:(NSString *)urlStr {
    if (urlStr && urlStr.length > 0) {
        // DDLogDebug(@"OpenUrl:%@", urlStr);
        NSURL* url = [NSURL URLWithString:urlStr];
        UIApplication *app = [UIApplication sharedApplication];
        if ([app canOpenURL:url]) {
            if ([[[UIDevice currentDevice] systemVersion] compare:@"10.0" options:NSNumericSearch] != NSOrderedAscending) {
                if (@available(iOS 10.0, *)) {
                    [app openURL:url options:@{} completionHandler:^(BOOL success) {
                    }];
                }
            } else {
                [app openURL:url];
            }
            return;
        }
    }
}

@end
