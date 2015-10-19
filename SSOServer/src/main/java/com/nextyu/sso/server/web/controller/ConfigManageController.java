package com.nextyu.sso.server.web.controller;

import com.alibaba.fastjson.JSON;
import com.nextyu.sso.common.util.JSONUtil;
import com.nextyu.sso.server.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 重新加载配置文件Controller.
 * 提供一个更新配置页面，用于重新加载配置文件.
 *
 * @author nextyu
 * @version 1.0
 */
@Controller
@RequestMapping("/config")
public class ConfigManageController {

    @Autowired
    private Config config;

    @RequestMapping(method = RequestMethod.GET)
    public String config() {
        return "config";
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public void refresh(String code, HttpServletResponse response) throws Exception {
        Map<String, String> map = new HashMap<>();
        if ("test".equals(code)) {
            config.refreshConfig();
            map.put("resp", "success");
        } else {
            map.put("resp", "failure");
        }
        String jsonString = JSON.toJSONString(map);
        JSONUtil.writeOut(response, jsonString);

    }

}
