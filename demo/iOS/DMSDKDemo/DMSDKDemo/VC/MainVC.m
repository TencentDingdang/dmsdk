//
//  MainVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 6/3/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "MainVC.h"
#import "AuthVC.h"
#import <TVSCore/TVSEnvironment.h>
#import <TVSCore/TVSAuth.h>
#import <TVSCore/TVSThirdPartyAuth.h>

@interface MainVC ()<UIPickerViewDataSource, UIPickerViewDelegate>

@end

@implementation MainVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _pickerView.dataSource = self;
    _pickerView.delegate = self;
    
    // 读取 SDK 版本号
    self.title = [NSString stringWithFormat:@"DMSDK(v%@)", [TVSEnvironment shared].sdkVersion];
    // 读取后台环境配置
    [_pickerView selectRow:[TVSEnvironment shared].netConfig.serverEnv inComponent:0 animated:NO];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return 4;
}

- (NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    switch (row) {
        case TVSServerEnvFormal: {
            return @"正式环境";
            break;
        }
        case TVSServerEnvExplore: {
            return @"体验环境";
            break;
        }
        case TVSServerEnvTest: {
            return @"测试环境";
            break;
        }
        case TVSServerEnvDev: {
            return @"开发环境";
            break;
        }
    }
    return nil;
}

#pragma mark UIPickerViewDelegate

// 由于不同环境账号信息不互通，切换环境后需要重新登录
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    if ([TVSEnvironment shared].netConfig.serverEnv == row) return;
    
    if ([TVSAuthManager shared].isVendorTokenExist) {
        UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"提示" message:@"不同环境下账号信息不互通，切换后需要重新登录。确定要切换么？" preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
            // 注销
            [[TVSAuthManager shared]logout];
            // 保存后台环境配置
            [TVSEnvironment shared].netConfig.serverEnv = row;
            
            AuthVC* vc = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"AuthVC"];
            vc.fromAlert = YES;
            [self.navigationController pushViewController:vc animated:YES];
        }]];
        [alert addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
            [pickerView selectRow:[TVSEnvironment shared].netConfig.serverEnv inComponent:0 animated:YES];
        }]];
        [self presentViewController:alert animated:YES completion:nil];
    }
}

- (IBAction)onClickUploadLogButton:(id)sender {
    [[TVSEnvironment shared]performLogReportWithHandler:^(BOOL successful, NSString * _Nullable reportId) {
        if (!successful) {
            UIAlertController * alert = [UIAlertController alertControllerWithTitle:@"提示" message:[NSString stringWithFormat:@"上传日志失败"] preferredStyle:UIAlertControllerStyleAlert];
            [alert addAction:[UIAlertAction actionWithTitle:@"OK" style:UIAlertActionStyleDefault handler:nil]];
            [self presentViewController:alert animated:YES completion:nil];
            return;
        }
        UIAlertController * alert = [UIAlertController alertControllerWithTitle:@"提示" message:[NSString stringWithFormat:@"上传日志成功，点击按钮复制Report ID到剪切板"] preferredStyle:UIAlertControllerStyleAlert];
        [alert addAction:[UIAlertAction actionWithTitle:@"复制" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [UIPasteboard generalPasteboard].string = reportId;
        }]];
        [self presentViewController:alert animated:YES completion:nil];
        return;
    }];
}

@end
