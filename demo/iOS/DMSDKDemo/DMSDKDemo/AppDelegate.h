//
//  AppDelegate.h
//  TvsLoginDemo
//
//  Created by Rinc Liu on 2019/1/28.
//  Copyright © 2019 tencent. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <TVSTSKM/TVSTSKMProxy.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong,nonatomic) TVSTSKMProxy* tskmProxy;

@end

