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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * @author nextyu
 * @version 1.0
 */
public class SSOFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(SSOFilter.class);

    private String excludes; // 不需要拦截的URI模式，以正则表达式表示
    private String serverBaseUrl; // 服务端公网访问地址
    private String serverInnerAddress; // 服务端系统间通信用内网地址
    private boolean notLoginOnFail; // 当授权失败时是否让浏览器跳转到服务端登录页


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludes = filterConfig.getInitParameter("excludes");
        serverBaseUrl = filterConfig.getInitParameter("serverBaseUrl");
        serverInnerAddress = filterConfig.getInitParameter("serverInnerAddress");
        notLoginOnFail = Boolean.parseBoolean(filterConfig.getInitParameter("notLoginOnFail"));

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
                resp.sendError(403);
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
        final String PARAM_NAME = "__vt_param__=";
        // 拼接redirect的url,去除vt参数部分
        StringBuffer location = req.getRequestURL();
        String queryString = req.getQueryString();
        int index = queryString.indexOf(PARAM_NAME);

        if (index > 0) {// 还有其它参数
            queryString = "?" + queryString.substring(0, index - 1);
        } else {// 没有其它参数
            queryString = "";
        }
        location.append(queryString);

        Cookie cookie = new Cookie("VT", vtParam);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
        resp.sendRedirect(location.toString());
    }

    /**
     * 从请求参数中解析vt.
     *
     * @param req
     * @return
     */
    private String parseVTparam(HttpServletRequest req) {
        final String PARAM_NAME = "__vt_param__=";
        String queryString = req.getQueryString();
        if (StringUtil.isEmpty(queryString)) {
            return null;
        }

        int index = queryString.indexOf(PARAM_NAME);
        if (index > -1) {
            return queryString.substring(index + PARAM_NAME.length());
        }
        return null;
    }

    /**
     * 将user存入threadLocal和request，供业务系统使用.
     *
     * @param user
     * @param req
     */
    private void holdUser(SSOUser user, HttpServletRequest req) {
        UserHolder.set(user, req);
    }

    /**
     * 引导浏览器重定向到服务端执行登录校验.
     *
     * @param req
     * @param resp
     */
    private void loginCheck(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // ajax类型请求涉及跨域问题
        // CORS方案解决跨域操作时，无法携带Cookie，所以无法完成验证，此处不适合
        // jsonp方案可以处理Cookie问题，但jsonp方式对后端代码有影响，能实现但复杂不理想，大家可以课后练习实现
        // 所以ajax请求前建议先让业务系统获取到vt，这样发起ajax请求时就不会执行跳转验证操作，避免跨域操作产生
        if ("XMLHttpRequest".equals(req.getHeader("x-requested-with"))) {
            // 400 状态表示请求格式错误，服务器没有理解请求，此处返回400状态表示未登录时服务器拒绝此ajax请求
            resp.sendError(400);
        } else {
            // redirect只能是get请求，所以如果当前是post请求，会将post过来的请求参数变成url querystring，即get形式参数
            // 这种情况，此处实现就会有一个局限性 —— 请求参数长度的限制，因为浏览器对get请求的长度都会有所限制。
            // 如果post过来的内容过大，就会造成请求参数丢失
            // 解决这个问题，只能是让用户系统去避免这种情况发生.
            // 可以在发送这类请求前任意时间点发起一次任意get类型请求，这个get请求通过loginCheck
            // 的引导从服务端获取到vt，当再发起post请求时，vt已存在并有效，就不会进入到这个过程，从而避免了问题出现

            // 将所有请求参数重新拼接成queryString
            String queryString = makeQueryString(req);
            // 回调URL
            String redirectURL = req.getRequestURL() + queryString;
            String location = serverBaseUrl + "/login?redirectURL=" + URLEncoder.encode(redirectURL, "utf-8");
            if (notLoginOnFail) {
                location += "&notLogin=true";
            }
            resp.sendRedirect(location);
        }
    }

    /**
     * 将所有请求参数重新拼接成queryString.
     *
     * @param req
     * @return
     * @throws UnsupportedEncodingException
     */
    private String makeQueryString(HttpServletRequest req) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = req.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                sb.append("&").append(paramName).append("=").append(URLEncoder.encode(paramValue, "utf-8"));
            }

            if (sb.length() > 0) {
                sb.replace(0, 1, "?");
            }
        }
        return sb.toString();
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
