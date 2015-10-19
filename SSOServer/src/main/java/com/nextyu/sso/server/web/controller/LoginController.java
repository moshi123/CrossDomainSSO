package com.nextyu.sso.server.web.controller;

import com.alibaba.fastjson.JSON;
import com.nextyu.sso.common.util.CookieUtil;
import com.nextyu.sso.common.util.JSONUtil;
import com.nextyu.sso.common.util.StringUtil;
import com.nextyu.sso.server.domain.ClientSystem;
import com.nextyu.sso.server.domain.Credential;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.PreLoginHandler;
import com.nextyu.sso.server.util.Config;
import com.nextyu.sso.server.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
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
                // TODO 自动登录流程
                return null;
            }
        } else {// VT存在
            // 验证VT
            LoginUser loginUser = TokenManager.validate(vt);
            if (loginUser == null) {// VT无效
                return config.getLoginViewName();
            } else {// VT有效
                return validateSuccess(redirectURL, vt, loginUser, model);
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
    private String validateSuccess(String redirectURL, String vt, LoginUser loginUser, Model model) {

        if (StringUtil.isEmpty(redirectURL)) {
            // 没有redirectURL
            // 获取用户可用的业务系统列表
            model.addAttribute("sysList", config.getClientSystems(loginUser));
            model.addAttribute("VT", vt);
            model.addAttribute("loginUser", loginUser);
            return config.getLoginViewName();
        } else {
            // 有redirectURL
            return "redirect:" + StringUtil.appendURLParam(redirectURL, "VT", vt);
        }

    }

    /**
     * 登录验证.
     *
     * @param redirectURL
     * @param rememberMe
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String redirectURL, Boolean rememberMe, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final Map<String, String[]> parameterMap = request.getParameterMap();

        final Object sessionVal = session.getAttribute(PreLoginHandler.SESSION_ATTR_NAME);

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

            @Override
            public Object getSettedSessionValue() {
                return sessionVal;
            }
        };

        LoginUser loginUser = config.getAuthenticationHandler().authenticate(credential);

        if (loginUser == null) {
            model.addAttribute("errorMsg", credential.getError());
            return config.getLoginViewName();
        } else {
            String vt = authSuccess(loginUser, rememberMe, response);
            return validateSuccess(redirectURL, vt, loginUser, model);
        }

    }

    /**
     * 登录预处理器.
     *
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping(value = "/preLogin", method = RequestMethod.GET)
    public void preLogin(HttpServletResponse response, HttpSession session) throws Exception {
        PreLoginHandler preLoginHandler = config.getPreLoginHandler();
        if (preLoginHandler == null) {
            throw new Exception("没有配置preLoginHandler,无法执行预处理");
        }
        Map<?, ?> map = preLoginHandler.handle(session);
        String jsonString = JSON.toJSONString(map);
        JSONUtil.writeOut(response, jsonString);
    }


    /**
     * 退出登录.
     *
     * @param redirectURL
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(String redirectURL, HttpServletRequest request, HttpServletResponse response) {
        String vt = CookieUtil.getCookie(request, "VT");

        // 移除token
        TokenManager.invalid(vt);

        // 移除server端vt cookie
        Cookie cookie = new Cookie("VT", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 通知各客户端系统logout
        List<ClientSystem> clientSystems = config.getClientSystems();
        for (ClientSystem clientSystem : clientSystems) {
            clientSystem.noticeLogout(vt);
        }

        if (StringUtil.isEmpty(redirectURL)) {
            return "logout";
        } else {
            return "redirect:" + redirectURL;
        }

    }

    /**
     * 授权成功后的操作.
     *
     * @param response
     * @param rememberMe
     * @return
     */
    private String authSuccess(LoginUser loginUser, Boolean rememberMe, HttpServletResponse response) {
        // 生成VT
        String vt = StringUtil.uniqueKey();
        // 生成LT？
        // TODO 自动登录LT
        // 存入Map
        TokenManager.addToken(vt, loginUser);
        // 写Cookie
        Cookie cookie = new Cookie("VT", vt);
        response.addCookie(cookie);
        return vt;
    }


}
