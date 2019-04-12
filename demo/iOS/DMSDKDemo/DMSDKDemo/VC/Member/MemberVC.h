//
//  MemberVC.h
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "BaseVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface MemberVC : BaseVC
@property (strong, nonatomic) IBOutlet UITextField *tfDSN;
@property (strong, nonatomic) IBOutlet UITextField *tfPID;
@property (strong, nonatomic) IBOutlet UITextView *tvResult;

@end

NS_ASSUME_NONNULL_END