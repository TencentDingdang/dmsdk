//
//  TVSEnvironment.h
//  DMSDK
//
//  Created by Rinc Liu on 18/11/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>


/*
 * @brief TVS 后台环境
 */
typedef NS_ENUM(NSInteger,TVSServerEnv) {
    /*
     * @brief 正式环境（默认）
     */
    TVSServerEnvFormal,
    
    /*
     * @brief 体验环境
     */
    TVSServerEnvExplore,
    
    /*
     * @brief 测试环境
     */
    TVSServerEnvTest,
    
    /*
     * @brief 开发环境
     */
    TVSServerEnvDev
};

/*
 * @class TVSNetworkConfig
 * @brief TVS 网络配置
 */
@interface TVSNetworkConfig : NSObject

/*
 * @brief 后台环境
 */
@property(nonatomic,assign) TVSServerEnv serverEnv;

/*
 * @brief 请求超时时间
 */
@property(nonatomic,assign) NSTimeInterval reqTimeout;

/*
 * @brief 是否使用 IPList 方案
 */
@property(nonatomic,assign) BOOL useIPList;

@end

/*
 * @class TVSEnvironment
 * @warning 必须在调用 DMSDK 其他接口前设置！！
 * @brief TVS 环境设置接口
 */
@interface TVSEnvironment : NSObject

/*
 * @brief 网络配置
 */
@property(nonatomic,strong,readonly,nonnull) TVSNetworkConfig* netConfig;

/*
 * @brief 是否启用异常上报
 */
@property(nonatomic,assign) BOOL enableDiagnosis;

/*
 * @brief 获得 TVS 环境类单例对象
 * @return TVS 环境类实例
 */
+(nonnull instancetype)shared;

/*
 * @brief 开启日志
 */
-(void)enableLog;

/*
 * @brief 触发日志上报
 */
- (void)performLogReportWithHandler:(nonnull void (^)(BOOL, NSString * _Nullable))handler;

/*
 * @brief 获得 SDK 版本
 * @return SDK 版本
 */
-(nonnull NSString*)sdkVersion;

@end
