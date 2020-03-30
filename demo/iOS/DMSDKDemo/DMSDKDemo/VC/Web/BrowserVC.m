//
//  BrowserVC.m
//  DMSDKDemo
//
//  Created by Rinc Liu on 8/3/2019.
//  Copyright © 2019 tencent. All rights reserved.
//

#import <TVSCore/TVSThirdPartyAuth.h>
#import "BrowserVC.h"
#import "QQMusicQqmpAuthAgent.h"

@interface BrowserVC()<TVSWebUniversalDelegate, TVSWebBusinessDelegate, DialogDelegate/*, TVSAuthDelegate*/>

@property(nonatomic,strong) UIView* vNav;
@property(nonatomic,strong) UIButton *btnBack, *btnReload, *btnStop, *btnForward;
@property(nonatomic,strong) UIProgressView* progressView;
@property(nonatomic,strong) TVSWebView* webview;
@property(nonatomic,strong) UIAlertController * loadingDialogController;

@end

@implementation BrowserVC

-(void)viewDidLoad {
    [super viewDidLoad];
    
    _vNav = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 120, 40)];
    _btnBack = [self newButtonX:0 title:@"←" sel:@selector(onClickBtnBack:)];
    _btnReload = [self newButtonX:30 title:@"↺" sel:@selector(onClickBtnReload:)];
    _btnStop = [self newButtonX:60 title:@"✗" sel:@selector(onClickBtnStop:)];
    _btnForward = [self newButtonX:90 title:@"→" sel:@selector(onClickBtnForward:)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc]initWithCustomView:_vNav];
    
    CGFloat offsetY = kSTATUS_BAR_HEIGHT + kNAVIGATION_BAR_HEIGHT;
    
    _progressView = [[UIProgressView alloc]initWithFrame:CGRectMake(0, offsetY, kSCREEN_WIDTH, 1)];
    _progressView.hidden = YES;
    [self.view addSubview:_progressView];
    
    // 添加 TVSWebView
    _webview = [[TVSWebView alloc] initWithFrame:CGRectMake(0, offsetY, kSCREEN_WIDTH, kSCREEN_HEIGHT - offsetY - (iPhoneX ? iPhoneX_BOTTOM_SPACE : 0))];
    _webview.webUniversalDelegate = self;
    _webview.webBusinessDelegate = self;
    //_webview.authDelegate = self; //如果自己实现账号登录，必须实现此协议!!
    // QQ 音乐会员相关页面需要设备信息
    _webview.device = [TVSDeviceInfo new];
    _webview.device.bindType = TVSDeviceBindTypeTVSSpeaker;
    _webview.device.pushIdExtra = PUSH_ID_EXTRA_TVS_SPEAKER;
    _webview.device.deviceType = @"ScreenSpeaker";
    _webview.device.deviceOEM = @"TencentDingdang";
    _webview.device.productId = _pid;
    _webview.device.dsn = _dsn;
    _webview.device.guid = _deviceGuid;
    [self.view insertSubview:_webview belowSubview:_progressView];
    // 自定义 TVSWebView 内部的 UIScrollView
    _webview.scrollView.bounces = YES;
    _webview.scrollView.showsVerticalScrollIndicator = YES;
    
    // Set device information to CP Auth Agent
    id agent = [[TVSCPAuthAgentManager shared]getAgentOfCP:TVSCPQQMusic andAuthType:QQ_MUSIC_AUTH_TYPE_QQ];
    if (agent && [agent isKindOfClass:[QQMusicQqmpAuthAgent class]]) {
        QQMusicQqmpAuthAgent * qqmpAgent = agent;
        TVSDeviceInfo * di = [TVSDeviceInfo new];
        di.productId = _pid;
        di.dsn = _dsn;
        qqmpAgent.deviceInfo = di;
        qqmpAgent.dialogDelegate = self;
    }
    
    // 加载网页或 URL
    if (_url) {
        [_webview loadUrl:_url];
    } else {
        [_webview loadPage:_pageType];
    }
}

-(UIButton*)newButtonX:(CGFloat)x title:title sel:(SEL)sel {
    UIButton* btn = [[UIButton alloc]initWithFrame:CGRectMake(x, 0, 30, 40)];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setTitleColor:btn.tintColor forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor lightGrayColor] forState:UIControlStateDisabled];
    [btn addTarget:self action:sel forControlEvents:UIControlEventTouchUpInside];
    btn.enabled = NO;
    [_vNav addSubview:btn];
    return btn;
}

-(void)refreshButtons {
    _btnReload.enabled = YES;
    _btnBack.enabled = [_webview canGoBack];
    _btnBack.enabled = YES;//[_webview canGoBack];
    _btnForward.enabled = [_webview canGoForward];
}

// 回退网页
- (void)onClickBtnBack:(id)sender {
    [_webview goBack];
}

// 刷新网页
- (void)onClickBtnReload:(id)sender {
    [_webview reload];
}

// 停止加载网页
- (void)onClickBtnStop:(id)sender {
    [_webview stopLoading];
}

// 前进网页
- (void)onClickBtnForward:(id)sender {
    [_webview goForward];
}

#pragma mark TVSWebUIDelegate 网页 UI 回调

// 网页加载开始
-(void)TVSWebLoadStart {
    _progressView.hidden = NO;
    _progressView.progress = 0;
    _btnStop.enabled = YES;
    [self refreshButtons];
}

// 网页加载进度更新
-(void)TVSWebLoadProgress:(double)progress {
    _progressView.progress = progress;
    [self refreshButtons];
}

// 网页加载完成
-(void)TVSWebLoadFinish {
    _progressView.hidden = YES;
    _progressView.progress = 0;
    _btnStop.enabled = NO;
    [self refreshButtons];
}

// 网页加载错误
-(void)TVSWebLoadError:(NSError *)error {
    _progressView.hidden = YES;
    _progressView.progress = 0;
    _btnStop.enabled = NO;
    [self refreshButtons];
    NSLog(@"BrowserVC load error:%@", error.localizedDescription);
}

// 网页拉取到标题
-(void)TVSWebGotTitle:(NSString *)title {
    self.title = title;
}

// 是否允许加载指定scheme链接（默认只允许打开 http、https、wexin、wtloginm、itms、itms-apps、dingdang 等常规 scheme）
-(BOOL)TVSWebShouldOpenScheme:(NSString *)scheme {
    return NO;
}

// 是否允许加载指定网页链接（默认都允许打开）
-(BOOL)TVSWebShouldLoadUrl:(NSString *)url {
    return YES;
}

#pragma mark TVSWebBusinessDelegate 网页业务回调

// 网页透传数据
-(void)TVSWebProxyData:(NSDictionary *)data {
    NSLog(@"BrowserVC proxyData:%@", data);
}

// 网页请求关闭
-(void)TVSWebRequestExit {
    [self.navigationController popViewControllerAnimated:YES];
}

// 收到 JS 消息
-(void)TVSWebReceivedJSMessage:(NSString*)msg data:(id)data {
    NSLog(@"BrowserVC recv JS msg:%@ data:%@", msg, data);
}

// 注入额外数据
-(NSDictionary*)TVSWebRequestExtraData {
    return @{@"k": @"v"};
}

#pragma mark DialogDelegate

- (void)showLoadingDialog {
    _loadingDialogController = [UIAlertController alertControllerWithTitle:nil message:@"查询授权结果" preferredStyle:UIAlertControllerStyleAlert];
    UIActivityIndicatorView * indicator = [[UIActivityIndicatorView alloc]initWithFrame:CGRectMake(10, 5, 50, 50)];
    indicator.hidesWhenStopped = YES;
    indicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
    [indicator startAnimating];
    [_loadingDialogController.view addSubview:indicator];
    [self presentViewController:_loadingDialogController animated:YES completion:nil];
}

- (void)hideLoadingDialog {
    if (_loadingDialogController) {
        [_loadingDialogController dismissViewControllerAnimated:YES completion:nil];
    }
}

#pragma mark TVSAuthDelegate
/*
-(TVSAccountInfo*)TVSAuthGetAccountInfo {
    TVSAccountInfo* tai = [TVSAccountInfo new];
    tai.accountType = TVSAccountTypeQQOpenId;
    tai.appId = @"1106196086";
    tai.accessToken = @"7DF675AC82A4F9502C2409B3B4946A46";
    tai.openId = @"F73223EAB66333A999755D9AAE06C2FC";
    tai.tvsId = @"11c2670f00050e3f";
    return tai;
}

-(TVSUserInfo*)TVSAuthGetUserInfo {
    TVSUserInfo* tui = [TVSUserInfo new];
    tui.headImgUrl = @"http:\/\/thirdwx.qlogo.cn\/mmopen\/vi_32\/DYAIOgq83erxUUfG98qavsOLP79zHdbiau61hkAqF0Bp8lJafiaANibvTibsRwPqaLRpTqNicF0ADATOX8nXPfbvricA\/132";
    tui.nickName = @"Rinc";
    return tui;
}
*/

@end
