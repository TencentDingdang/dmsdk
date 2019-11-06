//
//  TVSAlarmReminder.h
//  TVSTSKM
//
//  Created by Rinc Liu on 4/4/2019.
//  Copyright © 2019 RINC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <TVSTSKM/TVSTSKMProxy.h>



/*
 * @brief 闹钟提醒操作
 */
typedef NS_ENUM(NSInteger, TVSAlarmReminderOperation) {
    /*
     * @brief 管理
     */
    TVSAlarmReminderOperationManage,
    
    /*
     * @brief 同步数据
     */
    TVSAlarmReminderOperationSyncData,
    
    /*
     * @brief 清除数据
     */
    TVSAlarmReminderOperationClearData
};



/*
 * @class TVSAlarmReminder
 * @brief TVS 闹钟提醒管理
 */
@interface TVSAlarmReminder : NSObject

/*
 * @brief 初始化方法
 * @param tskmProxy 技能服务访问代理
 * @return TVSAlarmReminder 实例
 */
-(instancetype)initWithTSKMProxy:(TVSTSKMProxy*)tskmProxy;

/*
 * @brief 管理闹钟
 * @param op 操作类型
 * @param blob 请求数据
 * @return handler 回调
 */
-(void)alarmOperation:(TVSAlarmReminderOperation)op blob:(NSDictionary*)blob handler:(TVSTSKMCallback)handler;

/*
 * @brief 管理提醒
* @param op 操作类型
 * @param blob 请求数据
 * @return handler 回调
 */
-(void)reminderOperation:(TVSAlarmReminderOperation)op blob:(NSDictionary*)blob handler:(TVSTSKMCallback)handler;

@end
