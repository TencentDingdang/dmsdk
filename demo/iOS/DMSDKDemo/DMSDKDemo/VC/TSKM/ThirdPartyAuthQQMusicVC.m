//
//  ThirdPartyAuthVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "ThirdPartyAuthQQMusicVC.h"
#import <TVSCore/TVSThirdPartyAuth.h>
#import "BrowserVC.h"

@interface ThirdPartyAuthQQMusicVC ()

@property (nonatomic,strong) TVSThirdPartyAuth* auth;

@end

@implementation ThirdPartyAuthQQMusicVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _auth = [[TVSThirdPartyAuth alloc]initWithTSKMProxy:[self delegate].tskmProxy deviceInfo:_deviceInfo];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 拉起QQ音乐进行第三方账号授权
- (IBAction)onClickBtnAuth:(id)sender {
    // 跳转第三方账号授权 H5
    BrowserVC* bv = [BrowserVC new];
    NSURLComponents * components = [NSURLComponents componentsWithString:[TVSWebView getPresetUrlByPath:@"/v2m/cooperation/skillAuthManagerForCP"]];
    components.query = _tfQuery.text;
    bv.url = components.string;
    bv.pid = _deviceInfo.productId;
    bv.dsn = _deviceInfo.dsn;
    [self.navigationController pushViewController:bv animated:YES];
}

- (IBAction)onClickGetBoundCPAccountButton:(id)sender {
    [_auth getBoundCPAccountWithHandler:^(NSInteger errorCode, NSString * _Nullable accountType, TVSCPCredential * _Nullable credential) {
        if (errorCode != 0) {
            [self showText:[NSString stringWithFormat:@"查询失败：%ld", (long)errorCode] view:self->_tvResult];
            return;
        }
        if (!accountType || !credential) {
            [self showText:@"未找到授权账号" view:self->_tvResult];
            return;
        }
        [self showText:[NSString stringWithFormat:@"找到授权账号，类型：%@，AppID：%@，OpenID：%@", accountType, credential.appId, credential.openId] view:self->_tvResult];
    }];
}

@end
