//
//  AuthVC.m
//  TvsLoginDemo
//
//  Created by Rinc Liu on 2019/1/28.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "AuthVC.h"
#import <CommonCrypto/CommonDigest.h>
#import <TVSCore/TVSEnvironment.h>
#import <TVSCore/TVSAuth.h>

@implementation AuthVC

- (void)viewDidLoad {
    [super viewDidLoad];
    [_loadingView startAnimating];
    _loadingView.hidden = YES;
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self refreshBtnStatus];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void)disableButtons {
    _loadingView.hidden = NO;
    _btnAccountInfo.enabled = NO;
    _btnUserInfo.enabled = NO;
    _btnVendorLogin.enabled = NO;
    _btnLogout.enabled = NO;
}

//查询登录状态
-(void)refreshBtnStatus {
    _loadingView.hidden = YES;
    _btnAccountInfo.enabled = YES;
    _btnUserInfo.enabled = YES;
    if ([[TVSAuthManager shared]isVendorTokenExist]) {
        _btnVendorLogin.enabled = NO;
        _btnVendorSig.enabled = NO;
        _btnRefreshToken.enabled = YES;
        _btnLogout.enabled = YES;
    } else {//未登录
        _btnVendorLogin.enabled = YES;
        _btnVendorSig.enabled = YES;
        _btnLogout.enabled = NO;
        _btnRefreshToken.enabled = NO;
        _btnAccountInfo.enabled = NO;
        _btnUserInfo.enabled = NO;
    }
}

- (NSString *)getMd5HexOfString:(NSString *)str {
    const char * cStr = [str UTF8String];
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cStr, (int)strlen(cStr), result);
    return [NSString stringWithFormat:
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11], result[12], result[13], result[14], result[15]];
}

// 获取sig
- (IBAction)onGetSigButtonClick:(id)sender {
    // Ask for AccountID
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:@"厂商账号登录" message:@"输入AccountID" preferredStyle:UIAlertControllerStyleAlert];
    [alertController addTextFieldWithConfigurationHandler:^(UITextField * textField) {
        textField.placeholder = @"AccountID";
        textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    }];
    [alertController addAction:[UIAlertAction actionWithTitle:@"获取Sig" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        [self vendorLoginGetSigWithAccountId:alertController.textFields[0].text];
    }]];
    [self presentViewController:alertController animated:YES completion:nil];
}

- (void)vendorLoginGetSigWithAccountId:(NSString *)username {
    void(^callback)(BOOL, NSString *) = ^(BOOL successful, NSString * sigOrErrMsg) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self showText:[NSString stringWithFormat:@"获取Sig%@：%@", successful ? @"成功" : @"失败", sigOrErrMsg] view:self->_tvResult];
        });
    };
    NSString * url;
    switch ([TVSEnvironment shared].netConfig.serverEnv) {
        case TVSServerEnvFormal:
            url = @"https://firmacct.html5.qq.com/oauth2/get_sig";
            break;
        case TVSServerEnvExplore:
            url = @"https://firmacctgray.html5.qq.com/oauth2/get_sig";
            break;
        case TVSServerEnvTest:
        case TVSServerEnvDev:
            url = @"http://firmacct.sparta.html5.qq.com/oauth2/get_sig";
            break;
        default:
            break;
    }
    if (url == nil) {
        return;
    }
    // Construct request body json
    NSMutableDictionary * requestDict = [NSMutableDictionary dictionary];
    [requestDict setValue:YXW_APP_KEY forKey:@"appkey"];
    [requestDict setValue:[self getMd5HexOfString:[NSString stringWithFormat:@"%@:%@:%@", YXW_APP_KEY, YXW_APP_SECRET, username]] forKey:@"encryptsecret"];
    [requestDict setValue:username forKey:@"firmacctid"];
    NSError * error;
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:requestDict options:0 error:&error];
    if (!jsonData) {
        callback(NO, [NSString stringWithFormat:@"构造JSON失败：%@", error]);
        return;
    }
    NSString * jsonStr = [[NSString alloc]initWithData:jsonData encoding:NSUTF8StringEncoding];
    DDLogInfo(@"Json:%@", jsonStr);
    // Send request to get sig
    NSMutableURLRequest * request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:jsonData];
    NSURLSession * session = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    void(^httpCallback)(NSData *, NSURLResponse *, NSError *) = ^(NSData * data, NSURLResponse * response, NSError * error) {
        // Parse result
        if (error != nil) {
            callback(NO, [NSString stringWithFormat:@"请求失败：%@", error]);
            return;
        }
        if (!response || ![response isKindOfClass:[NSHTTPURLResponse class]]) {
            callback(NO, @"请求失败：response == nil");
            return;
        }
        NSInteger code = ((NSHTTPURLResponse *)response).statusCode;
        if (code != 200) {
            callback(NO, [NSString stringWithFormat:@"请求失败：错误码 %ld", (long)code]);
            return;
        }
        NSDictionary * resultDict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
        NSNumber * errCodeNumber = resultDict[@"ErrCode"];
        NSInteger errCode = errCodeNumber.integerValue;
        if (errCode != 0) {
            NSString * errMsg = resultDict[@"ErrMsg"];
            callback(NO, [NSString stringWithFormat:@"ErrCode:%ld ErrMsg:%@", (long)errCode, errMsg]);
            return;
        }
        NSString * sig = resultDict[@"Sig"];
        callback(YES, sig);
    };
    NSURLSessionDataTask * task = [session dataTaskWithRequest:request completionHandler:^(NSData * data, NSURLResponse * response, NSError * error) {
        httpCallback(data, response, error);
        [session finishTasksAndInvalidate];
    }];
    [task resume];
}

- (IBAction)onLoginWithVendorButtonClick:(id)sender {
    // Ask for Sig
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:@"厂商账号登录" message:@"输入Sig" preferredStyle:UIAlertControllerStyleAlert];
    [alertController addTextFieldWithConfigurationHandler:^(UITextField * textField) {
        textField.placeholder = @"Sig";
        textField.clearButtonMode = UITextFieldViewModeWhileEditing;
    }];
    [alertController addAction:[UIAlertAction actionWithTitle:@"登录" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        [[TVSAuthManager shared]vendorLoginWithSig:alertController.textFields[0].text andHandler:^(NSInteger errorCode) {
            [self showText:[NSString stringWithFormat:@"登录厂商账号%@：%ld", errorCode == 0 ? @"成功" : @"失败", (long)errorCode] view:self->_tvResult];
            [self refreshBtnStatus];
        }];
    }]];
    [self presentViewController:alertController animated:YES completion:nil];
}

-(IBAction)onRefreshTokenButtonClick:(id)sender {
    [[TVSAuthManager shared]refreshTokenWithHandler:^(NSInteger errorCode) {
        [self showText:[NSString stringWithFormat:@"刷新Token%@：%ld", errorCode == 0 ? @"成功" : @"失败", errorCode] view:self->_tvResult];
        [self refreshBtnStatus];
    }];
}

//读取账号信息
- (IBAction)onClickBtnAccountInfo:(id)sender {
    TVSAccountInfo* ai = [TVSAuthManager shared].accountInfo;
    [self showText:[NSString stringWithFormat:@"openId: %@",  ai.openId] view:_tvResult];
    [self showText:[NSString stringWithFormat:@"accessToken: %@",  ai.accessToken] view:_tvResult];
    [self showText:[NSString stringWithFormat:@"refreshToken: %@",  ai.refreshToken] view:_tvResult];
}

//读取用户信息
- (IBAction)onClickBtnUserInfo:(id)sender {
    
}

//注销登录
- (IBAction)onClickBtnLogout:(id)sender {
    [[TVSAuthManager shared]logout];
    [self refreshBtnStatus];
    _tvResult.text = nil;
}

@end
