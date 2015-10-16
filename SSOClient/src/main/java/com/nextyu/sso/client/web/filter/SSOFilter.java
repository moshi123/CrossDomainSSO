package com.nextyu.sso.client.web.filter;

import com.nextyu.sso.common.util.StringUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nextyu
 * @version 1.0
 */
public class SSOFilter implements Filter {


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
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 如果是不需要拦截的请求，直接通过
        if (requestIsExclude(req)) {
            chain.doFilter(request, response);
            return;
        }
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
        boolean matches = requestURI.matches(excludes);

        return matches;
    }

    @Override
    public void destroy() {

    }
}
