//
//  QQMusicUtils.h
//  QQMusicOpenSDK
//
//  Created by travisli(李鞠佑) on 2018/10/18.
//  Copyright © 2018年 腾讯音乐. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

#define AVOID_NIL_STRING(x) ((x) ?: @"")

@interface QQMusicUtils : NSObject

+ (NSDictionary*)paserURLParam:(NSURL *)url;

+ (NSString *)queryComponent:(NSURL*)url Named:(NSString *)name;

+ (id)objectWithJsonData:(NSData *)data error:(__autoreleasing NSError **)error targetClass:(Class)targetClass;

+ (NSString *)strWithJsonObject:(id)object;

@end

NS_ASSUME_NONNULL_END
