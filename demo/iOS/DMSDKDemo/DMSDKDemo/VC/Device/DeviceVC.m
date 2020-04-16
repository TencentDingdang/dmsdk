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
    _tfPid.text = YXW_PRODUCT_ID;
    _tfDSN.text = YXW_DEFAULT_DSN;
}



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

-(TVSDeviceIdentity*)getDevice {
    TVSDeviceIdentity* device = nil;
    if (NotEmpty(_tfDSN.text)) {
        device = [TVSDeviceIdentity tvsDeviceIdentityWithProductId:YXW_PRODUCT_ID andDsn:_tfDSN.text];
    }
    return device;
}

//设备授权
- (IBAction)onClickBtnBind:(id)sender {
    [self hideKeyboard];
    TVSDeviceIdentity* device = [self getDevice];
    if (!device) return;
    NSString* authReqInfo = _tfAuthReqInfo.text;
    if (!NotEmpty(authReqInfo)) {
        return;
    }
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceAuthManager shared]authorizeDevice:device withAuthReqInfo:authReqInfo andHandler:^(NSInteger errorcode, NSString * clientid, NSString * authRespInfo) {
            if (errorcode == 0) {
                [self showText:[NSString stringWithFormat:@"设备授权成功，client id %@, authRespID %@", clientid, authRespInfo] view:weakSelf.tvResult];
            }else {
                [self showText:[NSString stringWithFormat:@"设备授权失败，错误码 %ld", errorcode] view:weakSelf.tvResult];
            }
            
        }];
    }];
}

//解绑授权
- (IBAction)onClickBtnUnbind:(id)sender {
    [self hideKeyboard];
    TVSDeviceIdentity* device = [self getDevice];
    if (!device) return;
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceAuthManager shared]revokeDeviceAuthorizationFor:device withHandler:^(NSInteger errorcode) {
            if (errorcode == 0) {
                [self showText:[NSString stringWithFormat:@"设备解除授权成功"] view:weakSelf.tvResult];
            } else {
                [self showText:[NSString stringWithFormat:@"设备解除授权失败，错误码 %ld", errorcode] view:weakSelf.tvResult];
            }
        }];
    }];
}

//查询账号关联的的设备列表
- (IBAction)onClickBtnQueryDevices:(id)sender {
    [self hideKeyboard];
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        [[TVSDeviceAuthManager shared]getAuthorizedDevicesWithHandler:^(NSInteger code, NSArray<TVSDeviceIdentity *> * devices) {
            if (devices && devices.count > 0) {
                for (TVSDeviceIdentity* d in devices) {
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
    TVSDeviceIdentity* device = [self getDevice];
    if (!device) return;
    __weak typeof(self) weakSelf = self;
    [[TVSDeviceAuthManager shared]getBoundAccountForAuthorizedDevice:device
                                                         withHandler:^(NSInteger code, TVSAccountInfo * account) {
        if (code != 0) {
            [self showText:[NSString stringWithFormat:@"查询账号失败，错误码 %ld", code] view:weakSelf.tvResult];
            return;
        }
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
