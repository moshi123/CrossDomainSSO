package com.nextyu.sso.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextyu.sso.server.domain.LoginUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息序列化工具.
 *
 * @author nextyu
 * @version 1.0
 */
public abstract class UserSerializer {

    /**
     * 用户数据类型.
     */
    protected class UserData {
        /**
         * 唯一标识.
         */
        private String id;

        /**
         * 其他属性.
         */
        private Map<String, Object> properties = new HashMap<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties.putAll(properties);
        }

        /**
         * 新增单个属性.
         *
         * @param key
         * @param value
         */
        public void setProperty(String key, Object value) {
            this.properties.put(key, value);
        }

    }

    /**
     * 序列化.
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    public String serial(LoginUser loginUser) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserData userData = new UserData();

        // 在继承的具体实现类中完成转换
        if (loginUser != null) {
            translate(loginUser, userData);
        }

        return mapper.writeValueAsString(userData);
    }

    /**
     * 数据转换.
     *
     * @param loginUser
     * @param userData
     */
    protected abstract void translate(LoginUser loginUser, UserData userData);
}
