//
//  DeviceVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "DeviceVC.h"
#import <TVSCore/TVSDevice.h>

@interface DeviceVC ()

@end

@implementation DeviceVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

-(TVSDeviceInfo*)getDevice {
    TVSDeviceInfo* device = nil;
    if (NotEmpty(_tfPid.text) && NotEmpty(_tfDSN.text)) {
        device = [TVSDeviceInfo new];
        device.bindType = TVSDeviceBindTypeTVSSpeaker/*TVSDeviceBindTypeSDKSpeaker*/; //注意设置设备类型!!
        //device.pushIdExtra = PUSH_ID_EXTRA_TVS_SPEAKER/*PUSH_ID_EXTRA_SDK_SPEAKER*/; // 注意绑定设备需要传 pushIdExtra 字段!!
        device.productId = _tfPid.text;
        device.dsn = _tfDSN.text;
    }
    return device;
}

//绑定设备
- (IBAction)onClickBtnBind:(id)sender {
    [self hideKeyboard];
    TVSDeviceInfo* device = [self getDevice];
    if (!device) return;
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceManager shared]bindDevice:device handler:^(BOOL success) {
            [self showText:[NSString stringWithFormat:@"绑定 %@", success ? @"成功" : @"失败"] view:weakSelf.tvResult];
        }];
    }];
}

//解绑设备
- (IBAction)onClickBtnUnbind:(id)sender {
    [self hideKeyboard];
    TVSDeviceInfo* device = [self getDevice];
    if (!device) return;
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceManager shared]unbindDevice:device handler:^(BOOL success) {
            [self showText:[NSString stringWithFormat:@"解绑 %@", success ? @"成功" : @"失败"] view:weakSelf.tvResult];
        }];
    }];
}

//查询绑定的设备列表
- (IBAction)onClickBtnQueryDevices:(id)sender {
    [self hideKeyboard];
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceManager shared]queryDevicesByAccountWithBindType:[self getDevice].bindType handler:^(NSArray<TVSDeviceInfo *> * devices) {
            if (devices && devices.count > 0) {
                for (TVSDeviceInfo* d in devices) {
                    [self showText:[NSString stringWithFormat:@"查到设备\nProductId: %@\nDSN: %@", d.productId, d.dsn] view:weakSelf.tvResult];
                }
            } else {
                [self showText:@"未查到设备" view:weakSelf.tvResult];
            }
        }];
    }];
}

//根据设备信息查询绑定的账户信息
- (IBAction)onClickBtnQueryAccount:(id)sender {
    [self hideKeyboard];
    TVSDeviceInfo* device = [self getDevice];
    if (!device) return;
    __weak typeof(self) weakSelf = self;
    [[TVSDeviceManager shared]queryAccountWithDevice:device handler:^(TVSAccountInfo * account) {
        if (account && account.openId) {
            NSLog(@"openId: %@", account.openId);
            [self showText:[NSString stringWithFormat:@"查到账号\nOpenId: %@", account.openId] view:weakSelf.tvResult];
        } else {
            [self showText:@"未查到账号" view:weakSelf.tvResult];
        }
    }];
}


-(void)hideKeyboard {
    [_tfPid resignFirstResponder];
    [_tfDSN resignFirstResponder];
}

@end
