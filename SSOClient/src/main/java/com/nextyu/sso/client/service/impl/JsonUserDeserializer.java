package com.nextyu.sso.client.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextyu.sso.client.model.SSOUser;
import com.nextyu.sso.client.model.SSOUserImpl;
import com.nextyu.sso.client.service.UserDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 反序列化JSON格式数据.
 *
 * @author nextyu
 * @version 1.0
 */
public class JsonUserDeserializer implements UserDeserializer {
    @Override
    public SSOUser deserialize(String userData) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(userData);
        String id = rootNode.get("id").textValue();
        if (id == null) {
            return null;
        } else {
            SSOUserImpl ssoUser = new SSOUserImpl(id);
            JsonNode properties = rootNode.get("properties");
            Map<String, Object> propertyMap = mapper.readValue(properties.toString(), HashMap.class);
            ssoUser.setProperties(propertyMap);
            return ssoUser;
        }
    }
}
