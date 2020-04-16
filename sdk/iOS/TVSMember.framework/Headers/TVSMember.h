//
//  TVSMember.h
//  DMSDK
//
//  Created by Rinc Liu on 17/11/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * @brief TVS 会员类型
 */
typedef NS_ENUM(NSInteger,TVSMemberType) {
    /**
     * @brief QQ 音乐会员
     */
    TVSMemberTypeQQMusic
};

/**
 * @brief TVS 会员时长单位
 */
typedef NS_ENUM(NSInteger,TVSMemberUnit) {
    /**
     * @brief 年
     */
    TVSMemberUnitYear,
    /**
     * @brief 月
     */
    TVSMemberUnitMonth
};


/**
 * @class TVSMember
 * @brief TVS 会员相关接口
 */
__attribute__ ((deprecated("设备会员方案即将下线，请迁移到QQ音乐绿钻会员方案")))
@interface TVSMember: NSObject

/**
 * @brief 设置设备相关信息（用于领取会员）
 * @param productId TVS 后台申请的产品 id
 * @param dsn 设备号
 * @return 实例
 * @deprecated 设备会员方案即将下线，请迁移到QQ音乐绿钻会员方案
 */
-(instancetype)initWithDeviceProductId:(NSString*)productId dsn:(NSString*)dsn;

/**
 * @brief 查询设备是否领取过会员
 * @warning 必须确保已登录
 * @param type 会员类型
 * @param handler 回调，BOOL 值表示是否可以领取会员，NSInteger 和 TVSMemberUnit 分别表示可以领取的会员时长数量和单位
 * @deprecated 设备会员方案即将下线，请迁移到QQ音乐绿钻会员方案
 */
-(void)queryDeviceStatusWithType:(TVSMemberType)type handler:(nonnull void(^)(BOOL,NSInteger,TVSMemberUnit))handler;

/**
 * @brief 查询会员状态
 * @warning 必须确保已登录
 * @param type 会员类型
 * @param handler 回调，BOOL 值表示是否是否会员，两个 NSDate 分别表示会员开始时间、会员过期时间
 * @deprecated 设备会员方案即将下线，请迁移到QQ音乐绿钻会员方案
 */
-(void)queryMemberStatusWithType:(TVSMemberType)type handler:(nonnull void(^)(BOOL,NSDate*,NSDate*))handler;

@end
