//
//  TVSChildMode.h
//  TVSTSKM
//
//  Created by Rinc Liu on 4/4/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSTSKMProxy.h>

/**
 * @class TVSChildMode
 * @brief TVS 儿童模式
 */
@interface TVSChildMode : NSObject

/**
 * @brief 初始化方法
 * @param tskmProxy 技能服务访问代理
 * @return TVSChildMode 实例
 */
-(instancetype)initWithTSKMProxy:(nonnull TVSTSKMProxy*)tskmProxy;

/**
 * @brief 保存儿童模式配置
 * @param jsonBlob 请求数据
 * @param handler 回调
 */
-(void)setConfigWithJsonBlob:(NSDictionary*)jsonBlob handler:(nonnull TVSTSKMCallback)handler;

/**
 * @brief 查询儿童模式配置
 * @param jsonBlob 请求数据
 * @param handler 回调
 */
-(void)getConfigWithJsonBlob:(NSDictionary*)jsonBlob handler:(nonnull TVSTSKMCallback)handler;

@end
