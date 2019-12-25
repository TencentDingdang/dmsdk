//
//  DeviceControlVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 8/4/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "DeviceControlVC.h"
#import <TVSTSKM/TVSDeviceControl.h>

@interface DeviceControlVC ()

@property(nonatomic,strong) TVSDeviceControl* deviceControl;

@end

@implementation DeviceControlVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _deviceControl = [[TVSDeviceControl alloc] initWithTSKMProxy:[self delegate].tskmProxy];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 绑定设备
- (IBAction)onClickBtnBind:(id)sender {
    [self hideKeyBoard];
    __weak typeof(self) weakSelf = self;
    [self checkToken:^{// 所有 TSKM 相关接口都需要先验证 token ！！！
        [[TVSDeviceManager shared]bindDevice:weakSelf.deviceInfo handler:^(BOOL success) {
            if (success) {
                 [weakSelf showText:@"绑定成功" view:weakSelf.tvResult];
            } else {
                [weakSelf showText:@"绑定失败" view:weakSelf.tvResult];
            }
        }];
    }];
}

// 设备控制
- (IBAction)onClickBtnControl:(id)sender {
    [self hideKeyBoard];
    __weak typeof(self) weakSelf = self;
    if (NotEmpty(_tvPayload.text) && NotEmpty(_tfNamespace.text) && NotEmpty(_tfName.text)) {
        [self checkToken:^{// 所有 TSKM 相关接口都需要先验证 token ！！！
            [weakSelf.deviceControl controlDeviceWithNamespace:weakSelf.tfNamespace.text name:weakSelf.tfName.text payload:[weakSelf dictFromJson:weakSelf.tvPayload.text] handler:^(BOOL success, NSInteger code, NSDictionary * result) {
                if (success) {
                    if (result) {
                        [weakSelf showText:[NSString stringWithFormat:@"控制成功:\n%@", result] view:weakSelf.tvResult];
                    } else {
                        [weakSelf showText:@"控制成功" view:weakSelf.tvResult];
                    }
                } else {
                    [weakSelf showText:[NSString stringWithFormat:@"控制失败，错误码: %ld", code] view:weakSelf.tvResult];
                }
            }];
        }];
    }
}

-(void)hideKeyBoard {
    [_tfNamespace resignFirstResponder];
    [_tfName resignFirstResponder];
    [_tvPayload resignFirstResponder];
}

@end
