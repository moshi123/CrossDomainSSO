package com.nextyu.sso.server.util;

import com.nextyu.sso.server.service.AuthenticationHandler;

/**
 * 应用配置信息.
 *
 * @author nextyu
 * @version 1.0
 */
public class Config {

    /**
     * 登录页面视图名称.
     */
    private String loginViewName;

    /**
     * 验证身份处理器.
     */
    private AuthenticationHandler authenticationHandler;

    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }
}
