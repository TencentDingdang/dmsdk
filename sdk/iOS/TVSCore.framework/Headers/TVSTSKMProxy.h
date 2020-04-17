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

/**
 * @brief TVS 领域服务请求回调，第一个参数表示是否成功，第二个参数表示错误码，第三个参数表示服务端返回的jsonBlobInfo字段。
 */
typedef void(^TVSTSKMCallback)(BOOL,NSInteger,NSDictionary* _Nullable);

/**
 * @class TVSTSKMProxy
 * @brief TVS 领域服务访问代理类
 */
@interface TVSTSKMProxy : NSObject

/// 设备信息，表示当前TVSTSKMProxy实例操作的目标设备
@property(nonatomic,strong) TVSDeviceInfo* deviceInfo;

/**
 * @brief 实例化（QQ/微信登录场景）
 * @warning 如果是自己做账号授权，需要调用 [TVSAuthManager accountInfo] 手动注入账号信息!!
 * @param deviceInfo 设备信息，其中productId和dsn必填
 * @return 实例
 */
-(instancetype)initWithDeviceInfo:(TVSDeviceInfo*)deviceInfo;

/// 发起UniAccess请求
/// @param domain 发起请求的domain字段
/// @param intent 发起请求的intent字段
/// @param blobInfo 发起请求的jsonBlobInfo字段
/// @param handler 请求结果回调
-(void)uniAccessWithDomain:(NSString*)domain intent:(NSString*)intent blobInfo:(NSDictionary*)blobInfo handler:(nonnull TVSTSKMCallback)handler;

@end
