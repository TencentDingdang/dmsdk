//
//  AlarmReminderVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 8/4/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "AlarmReminderVC.h"
#import <TVSTSKM/TVSAlarmReminder.h>

@interface AlarmReminderVC ()

@property(nonatomic,strong) TVSAlarmReminder* alarmReminder;

@end

@implementation AlarmReminderVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _alarmReminder = [[TVSAlarmReminder alloc]initWithTSKMProxy:[self delegate].tskmProxy];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 闹钟管理
- (IBAction)onClickBtnSendReq:(id)sender {
    [_tvBlob resignFirstResponder];
    __weak typeof(self) weakSelf = self;
    if (NotEmpty(_tvBlob.text)) {
        [self checkToken:^{// 所有 TSKM 相关接口都需要先验证 token ！！！
            [weakSelf.alarmReminder alarmOperation:TVSAlarmReminderOperationManage blob:[self dictFromJson:weakSelf.tvBlob.text] handler:^(BOOL success, NSInteger code, NSDictionary * result) {
                if (success) {
                    if (result) {
                        [weakSelf showText:[NSString stringWithFormat:@"闹钟管理成功:\n%@", result] view:weakSelf.tvResult];
                    } else {
                        [weakSelf showText:@"闹钟管理成功" view:weakSelf.tvResult];
                    }
                } else {
                    [weakSelf showText:[NSString stringWithFormat:@"闹钟管理失败，错误码: %ld", code] view:weakSelf.tvResult];
                }
            }];
        }];
    }
}

@end
