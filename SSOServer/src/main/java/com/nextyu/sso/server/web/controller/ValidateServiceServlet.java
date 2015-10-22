package com.nextyu.sso.server.web.controller;

import com.nextyu.sso.common.util.StringUtil;
import com.nextyu.sso.server.domain.LoginUser;
import com.nextyu.sso.server.service.UserSerializer;
import com.nextyu.sso.server.util.Config;
import com.nextyu.sso.server.util.SpringContextUtil;
import com.nextyu.sso.server.util.TokenManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 提供系统内网间VT验证服务.
 *
 * @author nextyu
 * @version 1.0
 */
@WebServlet("/validate_service")
public class ValidateServiceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("utf-8");

        // 客户端传来的vt
        String vt = req.getParameter("vt");
        LoginUser user = null;

        // 验证vt有效性
        if (!StringUtil.isEmpty(vt)) {
            user = TokenManager.validate(vt);
        }

        Config config = SpringContextUtil.getBean(Config.class);
        UserSerializer userSerializer = config.getUserSerializer();

        try {
            resp.getWriter().write(userSerializer.serial(user));
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}

