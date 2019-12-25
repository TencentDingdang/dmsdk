//
//  ChildModeVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 8/4/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "ChildModeVC.h"
#import <TVSTSKM/TVSChildMode.h>

@interface ChildModeVC ()

@property(nonatomic,strong) TVSChildMode* childMode;

@end

@implementation ChildModeVC

- (void)viewDidLoad {
    [super viewDidLoad];
    _childMode = [[TVSChildMode alloc]initWithTSKMProxy:[self delegate].tskmProxy];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

// 保存儿童模式配置
- (IBAction)onClickBtnSetConfig:(id)sender {
    [_tvBlob resignFirstResponder];
    __weak typeof(self) weakSelf = self;
    if (NotEmpty(_tvBlob.text)) {
        [self checkToken:^{// 所有 TSKM 相关接口都需要先验证 token ！！！
            [weakSelf.childMode setConfigWithJsonBlob:[weakSelf dictFromJson:weakSelf.tvBlob.text] handler:^(BOOL success, NSInteger code, NSDictionary * result) {
                if (success) {
                    if (result) {
                        [weakSelf showText:[NSString stringWithFormat:@"保存配置成功:\n%@", result] view:weakSelf.tvResult];
                    } else {
                        [weakSelf showText:@"保存配置成功" view:weakSelf.tvResult];
                    }
                } else {
                    [weakSelf showText:[NSString stringWithFormat:@"保存配置失败，错误码: %ld", code] view:weakSelf.tvResult];
                }
            }];
        }];
    }
}

// 查询儿童模式配置
- (IBAction)onClickBtnGetConfig:(id)sender {
    [_tvBlob resignFirstResponder];
    __weak typeof(self) weakSelf = self;
    if (NotEmpty(_tvBlob.text)) {
        [self checkToken:^{// 所有 TSKM 相关接口都需要先验证 token ！！！
            [weakSelf.childMode getConfigWithJsonBlob:[weakSelf dictFromJson:weakSelf.tvBlob.text] handler:^(BOOL success, NSInteger code, NSDictionary * result) {
                if (success) {
                    if (result) {
                        [weakSelf showText:[NSString stringWithFormat:@"查询配置成功:\n%@", result] view:weakSelf.tvResult];
                    } else {
                        [weakSelf showText:@"查询配置成功" view:weakSelf.tvResult];
                    }
                } else {
                    [weakSelf showText:[NSString stringWithFormat:@"查询配置失败，错误码: %ld", code] view:weakSelf.tvResult];
                }
            }];
        }];
    }
}

@end
