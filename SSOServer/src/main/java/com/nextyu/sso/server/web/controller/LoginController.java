package com.nextyu.sso.server.web.controller;

import com.nextyu.sso.common.util.CookieUtil;
import com.nextyu.sso.common.util.StringUtil;
import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.util.Config;
import com.nextyu.sso.server.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 登录Controller.
 *
 * @author nextyu
 * @version 1.0
 */
@Controller
public class LoginController {

    @Autowired
    private Config config;

    /**
     * 跳转到登录界面.
     *
     * @param redirectURL 重定向地址
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(String redirectURL, HttpServletRequest request, Model model) {
        // VT:Validate Token(令牌)
        // LT:Login Ticket(自动登录标识)
        String vt = CookieUtil.getCookie(request, "VT");
        if (vt == null) {// VT不存在
            String lt = CookieUtil.getCookie(request, "LT");
            if (lt == null) {// LT不存在
                return config.getLoginViewName();
            } else {// LT存在
                // TODO:自动登录流程
                return null;
            }
        } else {// VT存在
            // 验证VT
            LoginUser loginUser = TokenManager.validate(vt);
            if (loginUser == null) {// VT无效
                return config.getLoginViewName();
            } else {// VT有效
                return validateSuccess(redirectURL, vt, model);
            }
        }
    }

    /**
     * VT验证成功或登录成功后的操作.
     *
     * @param redirectURL
     * @param vt
     * @param model
     * @return
     */
    private String validateSuccess(String redirectURL, String vt, Model model) {
        if (StringUtil.isEmpty(redirectURL)) {
            model.addAttribute("VT", vt);
            return config.getLoginViewName();
        } else {
            return "redirect:" + StringUtil.appendURLParam(redirectURL, "VT", vt);
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String redirectURL, Boolean rememberMe, HttpServletRequest request, HttpServletResponse response, Model model) {

        final Map<String, String[]> parameterMap = request.getParameterMap();

        Credential credential = new Credential() {
            @Override
            public String getParameter(String name) {
                String[] values = parameterMap.get(name);
                return values != null && values.length > 0 ? values[0] : null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return parameterMap.get(name);
            }
        };

        LoginUser loginUser = config.getAuthenticationHandler().authenticate(credential);

        if (loginUser == null) {
            return config.getLoginViewName();
        } else {
            String vt = authSuccess(response, rememberMe);
            return validateSuccess(redirectURL, vt, model);
        }

    }

    /**
     * 授权成功后的操作.
     *
     * @param response
     * @param rememberMe
     * @return
     */
    private String authSuccess(HttpServletResponse response, Boolean rememberMe) {
        // 生成VT
        // 生成LT？
        // 存入Map
        // Cookie
        return null;
    }


}
