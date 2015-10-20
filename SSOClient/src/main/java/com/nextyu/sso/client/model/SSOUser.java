package com.nextyu.sso.client.model;

import java.io.Serializable;
import java.util.Set;

/**
 * 当前登录用户.
 *
 * @author nextyu
 * @version 1.0
 */
public interface SSOUser extends Serializable {

    /**
     * 能够区分用户的唯一标识.
     *
     * @return
     */
    String getId();

    /**
     * 按名称获取用户属性值.
     *
     * @param name
     * @return
     */
    Object getProperty(String name);

    /**
     * 获取所有可用属性名集合.
     *
     * @return
     */
    Set<String> propertyNames();

}
