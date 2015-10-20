package com.nextyu.sso.client.web.servlet;

import com.nextyu.sso.client.util.TokenManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * 接收服务端发送的通知.
 *
 * @author nextyu
 * @version 1.0
 */
@WebServlet("/notice/*")
public class ServerNoticeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // notice后路径为notice类型，如/notice/timeout，则当前通知为timeout类型
        String uri = req.getRequestURI();
        String cmd = uri.substring(uri.lastIndexOf("/") + 1);

        String vt = req.getParameter("vt");

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("utf-8");

        PrintWriter writer = resp.getWriter();

        switch (cmd) {
            case "timeout": {
                int tokenTimeout = Integer.parseInt(req.getParameter("tokenTimeout"));
                Date timeout = TokenManager.timeout(vt, tokenTimeout);
                writer.write(timeout == null ? "" : timeout.getTime() + "");
                break;
            }
            case "logout": {
                TokenManager.invalidate(vt);
                writer.write("true");
                break;
            }
            case "shutdown": {
                TokenManager.destroy();
                writer.write("true");
                break;
            }
        }

    }
}
