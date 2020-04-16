//
//  AuthVC.h
//  TvsLoginDemo
//
//  Created by Rinc Liu on 2019/1/28.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "BaseVC.h"

@interface AuthVC : BaseVC

@property (strong, nonatomic) IBOutlet UIButton *btnVendorLogin;
@property (strong, nonatomic) IBOutlet UIButton *btnVendorSig;
@property (strong, nonatomic) IBOutlet UITextView *tvResult;
@property (strong, nonatomic) IBOutlet UIButton *btnLogout;
@property (strong, nonatomic) IBOutlet UIButton *btnAccountInfo;
@property (strong, nonatomic) IBOutlet UIButton *btnUserInfo;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loadingView;
@property (strong, nonatomic) IBOutlet UIButton *btnRefreshToken;

@property(nonatomic,assign) BOOL fromAlert;

@end

