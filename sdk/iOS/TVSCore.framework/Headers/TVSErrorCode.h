//
//  TVSErrorCode.h
//  TVSCore
//
//  Created by Perqin Xie on 2020/3/5.
//  Copyright © 2020 RINC. All rights reserved.
//

#ifndef TVSErrorCode_h
#define TVSErrorCode_h

/// 已经登录错误。抛出该错误的接口要求处于未登录状态。
#define TVS_ERROR_ALREADY_LOGGED_IN -255001

/// 未登录错误。抛出该错误的接口要求处于登录状态后再调用。
#define TVS_ERROR_LOGIN_REQUIRED -255002

/// 传输错误。请求云小微后台时遇到了网络错误、数据包解析错误等。
#define TVS_ERROR_TRANSPORT -255003

/// 参数不可用。传入的参数不符合要求，如需要JSON格式字符串的参数收到了非JSON格式的字符串。
#define TVS_ERROR_BAD_ARGUMENTS -255004

/// 不支持的登录类型。该接口的调用需要使用厂商账号登录，不支持微信、QQ登录。
#define TVS_ERROR_LEGACY_ACCOUNT_NOT_SUPPORTED -255005

/// 参数非法。请检查传入的参数是否正确。
#define TVS_ERROR_INVALID_ARGUMENTS -255006

#endif /* TVSErrorCode_h */
