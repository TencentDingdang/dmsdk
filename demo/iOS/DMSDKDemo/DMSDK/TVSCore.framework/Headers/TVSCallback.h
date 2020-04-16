//
//  TVSCallback.h
//  TVSCore
//
//  Created by Perqin Xie on 2019/10/28.
//  Copyright © 2019 RINC. All rights reserved.
//

#ifndef TVSCallback_h
#define TVSCallback_h

#import <Foundation/Foundation.h>

// 通用回调类型，第一个参数为错误码，0表示成功，非0表示失败
typedef void(^TVSCallback) (NSInteger);

#endif /* TVSCallback_h */
