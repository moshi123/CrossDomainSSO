package com.nextyu.sso.client.service;

import com.nextyu.sso.client.model.SSOUser;

import java.io.IOException;

/**
 * 将服务端传来的user数据反序列化.
 *
 * @author nextyu
 * @version 1.0
 */
public interface UserDeserializer {

    /**
     * 反序列化.
     *
     * @param userData
     * @return
     */
    SSOUser deserialize(String userData) throws Exception;
}
