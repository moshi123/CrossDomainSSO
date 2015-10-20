package com.nextyu.sso.client.web.filter;

import com.nextyu.sso.client.model.SSOUser;
import com.nextyu.sso.client.util.TokenManager;
import com.nextyu.sso.client.util.UserHolder;
import com.nextyu.sso.common.util.CookieUtil;
import com.nextyu.sso.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nextyu
 * @version 1.0
 */
public class SSOFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(SSOFilter.class);

    private String excludes; // 不需要拦截的URI模式，以正则表达式表示
    private String serverBaseUrl; // 服务端公网访问地址
    private String serverInnerAddress; // 服务端系统间通信用内网地址

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludes = filterConfig.getInitParameter("excludes");
        serverBaseUrl = filterConfig.getInitParameter("serverBaseUrl");
        serverInnerAddress = filterConfig.getInitParameter("serverInnerAddress");

        if (StringUtil.isEmpty(serverBaseUrl) || StringUtil.isEmpty(serverInnerAddress)) {
            throw new ServletException("SSOFilter配置错误，必须设置serverBaseUrl和serverInnerAddress参数!");
        }

        TokenManager.serverInnerAddress = serverInnerAddress;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 如果是不需要拦截的请求，直接通过
        if (requestIsExclude(req)) {
            chain.doFilter(request, response);
        }

        logger.debug("进入SSOFilter,当前请求url: {}", req.getRequestURL());

        // 进行登录状态验证
        String vt = CookieUtil.getCookie(req, "VT");
        if (!StringUtil.isEmpty(vt)) {
            SSOUser user = null;

            try {
                user = TokenManager.validate(vt);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (user != null) {
                // 将user存放，供业务系统使用
                holdUser(user, req);
                chain.doFilter(request, response);
            } else {
                // 删除无效的VT cookie
                CookieUtil.removeCookie(resp, "VT", null, null);
                // 引导浏览器重定向到服务端执行登录校验
                loginCheck(req, resp);
            }
        } else {
            // 从请求参数中解析vt
            String vtParam = parseVTparam(req);
            if (vtParam == null) {
                // url中没有vtParam，引导浏览器重定向到服务端执行登录校验
                loginCheck(req, resp);
            } else if (vtParam.length() == 0) {
                // 有vtParam，但内容为空，表示到服务端loginCheck后，得到的结果是未登录
                ((HttpServletResponse) response).sendError(403);
            } else {
                // 让浏览器向本链接发起一次重定向，此过程去除vtParam，将vt写入cookie
                redirectToSelf(vtParam, req, resp);
            }
        }


    }

    /**
     * 从参数中获取服务端传来的vt后，执行一个到本链接的重定向，将vt写入cookie
     * 重定向后再发来的请求就存在有效vt参数了
     *
     * @param vtParam
     * @param req
     * @param resp
     */
    private void redirectToSelf(String vtParam, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO 此处拼接redirect的url
        String location = "";

        Cookie cookie = new Cookie("VT", vtParam);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
        resp.sendRedirect(location);
    }

    /**
     * 从请求参数中解析vt.
     *
     * @param req
     * @return
     */
    private String parseVTparam(HttpServletRequest req) {
        return null;
    }

    /**
     * 将user存入threadLocal和request，供业务系统使用.
     *
     * @param user
     * @param req
     */
    private void holdUser(SSOUser user, HttpServletRequest req) {
        UserHolder.USER_THREAD_LOCAL.set(user);
        req.setAttribute("__current_sso_user", user);
    }

    /**
     * 引导浏览器重定向到服务端执行登录校验.
     *
     * @param req
     * @param resp
     */
    private void loginCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String location = "server_login_url?query_str";
        resp.sendRedirect(location);
    }

    /**
     * 判断是否拦截请求.
     *
     * @param request
     * @return
     */
    private boolean requestIsExclude(ServletRequest request) {
        if (StringUtil.isEmpty(excludes)) {
            return false;
        }

        // 获取去除context path后的请求路径
        String contextPath = request.getServletContext().getContextPath();
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        requestURI = requestURI.substring(contextPath.length());

        // 正则模式匹配的uri被排除，不需要拦截
        boolean isExcluded = requestURI.matches(excludes);

        if (isExcluded) {
            logger.debug("request path: {} is excluded!", requestURI);
        }

        return isExcluded;
    }

    @Override
    public void destroy() {

    }
}
