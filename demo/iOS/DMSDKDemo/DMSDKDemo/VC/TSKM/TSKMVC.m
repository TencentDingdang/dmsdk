//
//  TSKMVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 8/4/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "TSKMVC.h"
#import "ThirdPartyAuthQQMusicVC.h"
#import "DeviceControlVC.h"
#import "BrowserVC.h"

@interface TSKMVC ()

@property (nonatomic,strong) TVSDeviceInfo* deviceInfo;

@end

@implementation TSKMVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _deviceInfo = [TVSDeviceInfo new];
}

// 初始化 TVSTSKMProxy
- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender {
    [_tfPID resignFirstResponder];
    [_tfDSN resignFirstResponder];
    __weak typeof(self) weakSelf = self;
    [self checkLogin:^{
        if (NotEmpty(weakSelf.tfPID.text) && NotEmpty(weakSelf.tfDSN.text)) {
            weakSelf.deviceInfo.productId = weakSelf.tfPID.text;
            weakSelf.deviceInfo.dsn = weakSelf.tfDSN.text;
            // 为了便于后面绑定设备，需要传 bindType 和 pushIdExtra 字段
            weakSelf.deviceInfo.bindType = TVSDeviceBindTypeTVSSpeaker/*TVSDeviceBindTypeSDKSpeaker*/;
            weakSelf.deviceInfo.pushIdExtra = PUSH_ID_EXTRA_TVS_SPEAKER/*PUSH_ID_EXTRA_SDK_SPEAKER*/;
            [weakSelf delegate].tskmProxy = [[TVSTSKMProxy alloc] initWithDeviceInfo:weakSelf.deviceInfo];
        }
    }];
    return ([TVSAuthManager shared].isQQTokenExist || [TVSAuthManager shared].isWXTokenExist) && NotEmpty(weakSelf.tfPID.text) && NotEmpty(weakSelf.tfDSN.text);
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // QQ音乐授权需要设备信息
    if ([segue.destinationViewController isKindOfClass:[ThirdPartyAuthQQMusicVC class]]) {
        ThirdPartyAuthQQMusicVC* vc = (ThirdPartyAuthQQMusicVC*)segue.destinationViewController;
        vc.deviceInfo = _deviceInfo;
    }
}

@end
