//
//  TVSDeviceControl.h
//  TVSTSKM
//
//  Created by Rinc Liu on 1/4/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSTSKMProxy.h>

/**
 * @class TVSDeviceControl
 * @brief TVS 多端控制
 */
@interface TVSDeviceControl : NSObject

/**
 * @brief 初始化方法
 * @param tskmProxy 技能服务访问代理
 * @return TVSDeviceControl 实例
 */
-(instancetype)initWithTSKMProxy:(nonnull TVSTSKMProxy*)tskmProxy;

/**
 * @brief 设备控制
 * @param nameSpace 控制指令的命名空间namespace字段
 * @param name 控制指令的名称
 * @param payload 控制指令的数据包体payload
 * @param handler 操作回调
 */
-(NSString*)controlDeviceWithNamespace:(NSString *)nameSpace name:(NSString *)name payload:(NSDictionary *)payload handler:(nonnull TVSTSKMCallback)handler ;

@end
