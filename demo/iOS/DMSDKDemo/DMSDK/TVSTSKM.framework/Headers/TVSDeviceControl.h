//
//  TVSDeviceControl.h
//  TVSTSKM
//
//  Created by Rinc Liu on 1/4/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSTSKM/TVSTSKMProxy.h>

/*
 * @class TVSDeviceControl
 * @brief TVS 多端控制
 */
@interface TVSDeviceControl : NSObject

/*
 * @brief 初始化方法
 * @param tskmProxy 技能服务访问代理
 * @return TVSDeviceControl 实例
 */
-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy;

/*
 * @brief 设备控制
 * @param nameSpace
 * @param name
 * @param payload
 * @param handler 操作回调
 */
-(NSString*)controlDeviceWithNamespace:(NSString *)nameSpace name:(NSString *)name payload:(NSDictionary *)palyload handler:(TVSTSKMCallback)handler ;

@end
