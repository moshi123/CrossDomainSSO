package com.nextyu.sso.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SSOUser的实现，服务端生成此对象实例并序列化后传输给客户端.
 *
 * @author nextyu
 * @version 1.0
 */
public class SSOUserImpl implements SSOUser {

    private static final Map<String, Object> PROPERTY_MAP = new HashMap<>();

    private String id;

    public SSOUserImpl(String id) {
        this.id = id;
    }

    /**
     * 写入属性.
     *
     * @param properties
     */
    public void setProperties(Map<String, Object> properties) {
        PROPERTY_MAP.putAll(properties);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getProperty(String name) {
        return PROPERTY_MAP.get(name);
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_MAP.keySet();
    }
}
