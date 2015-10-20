package com.nextyu.sso.client.util;

import com.nextyu.sso.client.service.UserDeserializer;
import com.nextyu.sso.client.service.impl.JsonUserDeserializer;

/**
 * 用户反序列化器工厂.
 *
 * @author nextyu
 * @version 1.0
 */
public class UserDeserializerFactory {
    private UserDeserializerFactory() {
    }

    public static UserDeserializer create() {
        // 此处直接通过new方法实现
        // 若要实现更灵活配置方式，可通过配置文件或注解方式实现
        return new JsonUserDeserializer();
    }
}
