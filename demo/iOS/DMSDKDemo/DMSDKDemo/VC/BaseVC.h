//
//  BaseVC.h
//  DMSDKDemo
//
//  Created by Rinc Liu on 29/1/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"

NS_ASSUME_NONNULL_BEGIN

#define kSCREEN_WIDTH [UIScreen mainScreen].bounds.size.width
#define kSCREEN_HEIGHT [UIScreen mainScreen].bounds.size.height
#define kSTATUS_BAR_HEIGHT [[UIApplication sharedApplication] statusBarFrame].size.height
#define kNAVIGATION_BAR_HEIGHT self.navigationController.navigationBar.frame.size.height
#define iPhoneX ((kSCREEN_WIDTH == 375.f && kSCREEN_HEIGHT == 812.f) || (kSCREEN_WIDTH == 414.f && kSCREEN_HEIGHT == 896.f))
#define iPhoneX_BOTTOM_SPACE 34

#define NotEmpty(s) (s&&s.length>0)

@interface BaseVC : UIViewController

-(void)checkLogin:(void(^)(void))block;

-(void)showText:(NSString*)text view:(UITextView*)tv;

-(AppDelegate*)delegate;

-(NSDictionary*)dictFromJson:(NSString*)json;

-(void)checkToken:(void(^)(void))block;

-(NSString*)getProductID;

@end

NS_ASSUME_NONNULL_END
