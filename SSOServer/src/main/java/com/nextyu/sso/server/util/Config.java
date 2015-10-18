package com.nextyu.sso.server.util;

import com.nextyu.sso.server.domain.ClientSystem;
import com.nextyu.sso.server.service.AuthenticationHandler;

import java.util.ArrayList;
import java.util.List;

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
    private String loginViewName = "login";

    /**
     * 令牌有效期，单位为分钟，默认30分钟.
     */
    private int tokenTimeout = 30;

    /**
     * 验证身份处理器.
     */
    private AuthenticationHandler authenticationHandler;

    /**
     * 客户端系统列表.
     */
    private List<ClientSystem> clientSystems = new ArrayList<>();

    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public AuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    public List<ClientSystem> getClientSystems() {
        return clientSystems;
    }

    public void setClientSystems(List<ClientSystem> clientSystems) {
        this.clientSystems = clientSystems;
    }
}
