package com.nextyu.sso.server.domain;

/**
 * @author nextyu
 * @version 1.0
 */
public abstract class Credential {

    /**
     * 认证失败提示信息.
     */
    private String error;

    /**
     * 认证失败时，设置失败提示信息.
     *
     * @return
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * 获取一个参数值.
     *
     * @param name
     * @return
     */
    public abstract String getParameter(String name);

    /**
     * 获取多个参数值.
     *
     * @param name
     * @return
     */
    public abstract String[] getParameterValues(String name);

    /**
     * 由PreLoginHandler通过setSessionValue()方法写入特定session属性值.
     *
     * @return
     */
    public abstract Object getSettedSessionValue();

}
