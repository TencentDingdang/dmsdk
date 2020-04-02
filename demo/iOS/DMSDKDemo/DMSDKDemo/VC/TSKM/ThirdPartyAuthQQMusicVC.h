//
//  ThirdPartyAuthVC.h
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import "BaseVC.h"

NS_ASSUME_NONNULL_BEGIN

@interface ThirdPartyAuthQQMusicVC : BaseVC

@property (strong, nonatomic) IBOutlet UITextView *tvResult;
@property(strong, nonatomic) IBOutlet UITextField * tfQuery;

@property(nonatomic,strong) TVSDeviceInfo* deviceInfo;

@end

NS_ASSUME_NONNULL_END
