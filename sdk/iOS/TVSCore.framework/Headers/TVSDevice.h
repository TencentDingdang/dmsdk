//
//  TVSDevice.h
//  DMSDK
//
//  Created by Rinc Liu on 17/11/2017.
//  Copyright © 2017 tencent. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TVSAuth.h"


/*
 * @brief 云端 API 接入方案的音箱绑定时需要传入的 pushIdExtra 常量
 */
extern NSString* const PUSH_ID_EXTRA_SDK_SPEAKER;

/*
 * @brief SDK 接入方案的音箱绑定时需要传入的 pushIdExtra 常量
 */
extern NSString* const PUSH_ID_EXTRA_TVS_SPEAKER;




/*
 * @brief 设备绑定类型
 */
typedef NS_ENUM(NSInteger,TVSDeviceBindType) {
    /*
     * @brief SDK 接入方案的 App
     */
    TVSDeviceBindTypeSDKApp,
    
    /*
     * @brief SDK 接入方案的音箱
     */
    TVSDeviceBindTypeSDKSpeaker,
    
    /*
     * @brief 云端 API 接入方案的 App
     */
    TVSDeviceBindTypeTVSApp,
    
    /*
     * @brief 云端 API 接入方案的音箱
     */
    TVSDeviceBindTypeTVSSpeaker
};



/*
 * @class TVSDeviceInfo
 * @brief TVS Push 设备
 */
@interface TVSDeviceInfo : NSObject

/*
 * @brief productId TVS 后台申请的 appid:accessToken
 */
@property(nonatomic,copy) NSString* productId;

/*
 * @brief dsn 设备序列号
 */
@property(nonatomic,copy) NSString* dsn;

/*
 * @brief bindType 设备绑定类型
 */
@property(nonatomic,assign) TVSDeviceBindType bindType;

/*
 * @brief pushId
 */
@property(nonatomic,copy) NSString* pushId;

/*
 * @brief pushIdExtra
 */
@property(nonatomic,copy) NSString* pushIdExtra;

/*
 * @brief guid
 */
@property(nonatomic,copy) NSString* guid;

/*
 * @brief deviceId 设备ID
 */
@property(nonatomic,copy) NSString* deviceId;

/*
 * @brief deviceName 设备名
 */
@property(nonatomic,copy) NSString* deviceName;

/*
 * @brief deviceType 设备类型
 */
@property(nonatomic,copy) NSString* deviceType;

/*
 * @brief deviceSerial 设备系列
 */
@property(nonatomic,copy) NSString* deviceSerial;

/*
 * @brief deviceOEM 设备厂商
 */
@property(nonatomic,copy) NSString* deviceOEM;

/*
 * @brief deviceOEMUrl 设备品牌图标
 */
@property(nonatomic,copy) NSString* deviceOEMUrl;

/*
 * @brief deviceOEM 设备厂商 token（用于 PushKit）
 */
@property(nonatomic,copy) NSString* deviceOEMToken;

/*
 * @brief deviceMark 设备备注
 */
@property(nonatomic,copy) NSString* deviceMark;

/*
 * @brief QUA
 */
@property(nonatomic,copy) NSString* QUA;

/*
 * @brief IMEI
 */
@property(nonatomic,copy) NSString* IMEI;

/*
 * @brief LC
 */
@property(nonatomic,copy) NSString* LC;

/*
 * @brief MAC
 */
@property(nonatomic,copy) NSString* MAC;

/*
 * @brief QIMEI
 */
@property(nonatomic,copy) NSString* QIMEI;

/*
 * @brief enrollTime 注册时间
 */
@property(nonatomic,assign) long long enrollTime;

/*
 * @brief bindTime 绑定时间
 */
@property(nonatomic,assign) long long bindTime;


/*
 * @brief extra 扩展信息
 */
@property(nonatomic,strong) NSDictionary* extra;

/*
 * @brief extra 业务扩展信息
 */
@property(nonatomic,strong) NSDictionary* businessExtra;

/*
 * @brief 配置信息
 */
@property(nonatomic,strong) NSDictionary* configInfo;

/*
 * @brief accountinfo 账号信息
 */
@property(nonatomic,strong) TVSAccountInfo* accountinfo;

@end



/*
 * @class TVSDeviceManager
 * @warning 如果是自己做账号授权，需要调用 [[TVSAuthManager shared]setAccountInfo:acctInfo] 手动注入账号信息!!
 * @brief TVS 设备管理接口
 */
@interface TVSDeviceManager : NSObject

/*
 * @brief 获得 TVS 设备管理类单例对象
 * @return TVS 设备管理类实例
 */
+(instancetype)shared;

/*
 * @brief 有屏音箱扫码预绑定
 * @warning 必须确保已登录！！
 * @param device 设备信息，其中 productId、 dsn、bindType 必传!! 绑定 Speaker 设备时还必须传 pushIdExtra 字段（取值为 PUSH_ID_EXTRA_SDK_SPEAKER 或 PUSH_ID_EXTRA_TVS_SPEAKER 常量）！！
 * @param cancel 是否取消预绑定
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)preBindScreenDevice:(TVSDeviceInfo*)device cancel:(BOOL)cancel handler:(void(^)(BOOL))handler;

/*
 * @brief 绑定设备
 * @warning 必须确保已登录！！
 * @param device 设备信息，其中 productId、dsn、bindType 必传!! 绑定 Speaker 设备时还必须传 pushIdExtra 字段（取值为 PUSH_ID_EXTRA_SDK_SPEAKER 或 PUSH_ID_EXTRA_TVS_SPEAKER 常量）！！
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)bindDevice:(TVSDeviceInfo*)device handler:(void(^)(BOOL))handler;

/*
 * @brief 解绑设备
 * @warning 必须确保已登录！！
 * @param device 设备信息，其中 productId、 dsn、bindType 必传!! 解绑 Speaker 设备时还必须传 pushIdExtra 字段（取值为 PUSH_ID_EXTRA_SDK_SPEAKER 或 PUSH_ID_EXTRA_TVS_SPEAKER 常量）！！
 * @param handler 回调，BOOL 值表示是否成功
 */
-(void)unbindDevice:(TVSDeviceInfo*)device handler:(void(^)(BOOL))handler;

/*
 * @brief 通过 guid 查询设备列表
 * @param guid
 * @param bindType 设备绑定类型
 * @param handler 回调
 */
-(void)queryDevicesByGuid:(NSString*)guid bindType:(TVSDeviceBindType)bindType handler:(void(^)(NSArray<TVSDeviceInfo*>*))handler;

/*
 * @brief 通过 productId/dsn 查询设备列表
 * @param productId
 * @param dsn
 * @param bindType 设备绑定类型
 * @param handler 回调
 */
-(void)queryDevicesByProductId:(NSString*)productId dsn:(NSString*)dsn bindType:(TVSDeviceBindType)bindType handler:(void(^)(NSArray<TVSDeviceInfo*>*))handler;

/*
 * @brief 通过账号查询设备列表
 * @warning 必须确保已登录！！
 * @param bindType 设备绑定类型
 * @param handler 回调
 */
-(void)queryDevicesByAccountWithBindType:(TVSDeviceBindType)bindType handler:(void(^)(NSArray<TVSDeviceInfo*>*))handler;

/*
 * @brief 根据设备信息反查绑定的账号信息
 * @param device 设备信息，其中 productId、 dsn、bindType 必传!! 查询 Speaker 设备时还必须传 pushIdExtra 字段（取值为 PUSH_ID_EXTRA_SDK_SPEAKER 或 PUSH_ID_EXTRA_TVS_SPEAKER 常量）！！
 * @param handler 回调，TVSAccountInfo 为账号信息
 */
-(void)queryAccountWithDevice:(TVSDeviceInfo*)device handler:(void(^)(TVSAccountInfo*))handler;

/**
 * @brief 更新设备信息
 * @param deviceInfo 设备信息
 * @param businessDict 业务数据
 * @param handler 回调
 */
-(void)updateDeviceInfo:(TVSDeviceInfo*)deviceInfo businessDict:(NSDictionary*)businessDict handler:(void(^)(BOOL))handler;

@end
