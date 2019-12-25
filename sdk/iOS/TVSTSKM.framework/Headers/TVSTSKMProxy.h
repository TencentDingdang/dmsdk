//
//  TVSTSKMProxy.h
//  TVSTSKM
//
//  Created by Rinc Liu on 2/4/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSCore/TVSAuth.h>
#import <TVSCore/TVSDevice.h>

/*
 * @brief TVS 领域服务请求回调
 */
typedef void(^TVSTSKMCallback)(BOOL,NSInteger,NSDictionary*);

/*
 * @class TVSTSKMProxy
 * @brief TVS 领域服务访问代理类
 */
@interface TVSTSKMProxy : NSObject

/*
 * @brief 实例化（QQ/微信登录场景）
 * @warning 如果是自己做账号授权，需要调用 [[TVSAuthManager shared]setAccountInfo:acctInfo] 手动注入账号信息!!
 * @param deviceInfo 设备信息（其中 productId 必填，dsn 和 guid 二选一）
 * @return 实例
 */
-(instancetype)initWithDeviceInfo:(TVSDeviceInfo*)deviceInfo;

/*
 * @brief 领域服务访问
 * @warning 此接口后面会废弃掉，请直接调用 TVSAlarm(闹钟管理)、TVSThirdPartyAuth(第三方账号授权)、TVSDeviceControl(多端同步)、TVSChildMode(儿童模式) 相关 API !!
 */
-(void)uniAccessWithDomain:(NSString*)domain intent:(NSString*)intent blobInfo:(NSDictionary*)blobInfo handler:(TVSTSKMCallback)handler __attribute__((deprecated));

@end
