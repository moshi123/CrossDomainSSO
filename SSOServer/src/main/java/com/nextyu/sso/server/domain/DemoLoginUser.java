package com.nextyu.sso.server.domain;

/**
 * @author nextyu
 * @version 1.0
 */
public class DemoLoginUser extends LoginUser {
    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return loginName;
    }
}
