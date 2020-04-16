//
//  BaseVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "BaseVC.h"
#import "AuthVC.h"
#import <TVSCore/TVSAuth.h>

@interface BaseVC ()

@end

@implementation BaseVC

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

-(void)checkLogin:(void(^)(void))block {
    BOOL isLogin = [TVSAuthManager shared].isVendorTokenExist;
    if (isLogin) {
        block();
    } else {
        [self gotoLoginAlert:@"此功能需要账号信息"];
    }
}

-(void)gotoLoginAlert:(NSString*)alert {
    UIAlertController* ac = [UIAlertController alertControllerWithTitle:@"提示" message:alert preferredStyle:UIAlertControllerStyleAlert];
    [ac addAction:[UIAlertAction actionWithTitle:@"好的" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
        AuthVC* vc = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"AuthVC"];
        vc.fromAlert = YES;
        [self.navigationController pushViewController:vc animated:YES];
    }]];
    [self presentViewController:ac animated:YES completion:nil];
}

-(void)showText:(NSString *)text view:(UITextView *)tv {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (NotEmpty(tv.text)) {
            tv.text = [tv.text stringByAppendingString:[NSString stringWithFormat:@"%@%@", @"\n\n", text]];
        } else {
            tv.text = text;
        }
        if (NotEmpty(tv.text)) {
            NSRange bottom = NSMakeRange(tv.text.length - 1, 1);
            [tv scrollRangeToVisible:bottom];
        }
    });
}

-(AppDelegate*)delegate {
    return (AppDelegate*)[UIApplication sharedApplication].delegate;
}

-(NSDictionary*)dictFromJson:(NSString*)json {
    NSDictionary* dict = nil;
    if (json != nil && [json isKindOfClass:[NSString class]] && json.length > 0) {
        NSError *err;
        NSData *data = [json dataUsingEncoding:NSUTF8StringEncoding];
        if (data) {
            dict = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&err];
        }
    }
    return dict;
}

-(void)checkToken:(void (^)(void))block {
    if ([[TVSAuthManager shared] isVendorTokenExist]) {
        block();
    }else {
        [self gotoLoginAlert:@"此功能需要账号信息"];
    }
}

-(NSString*)getProductID {
    return YXW_PRODUCT_ID;
}

@end
