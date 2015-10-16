package com.nextyu.sso.server.domain;

/**
 * @author nextyu
 * @version 1.0
 */
public abstract class Credential {

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

}
