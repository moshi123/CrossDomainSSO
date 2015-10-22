<%@ page import="com.nextyu.sso.client.util.UserHolder" %>
<%@ page import="com.nextyu.sso.client.model.SSOUser" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
    SSOUser user = UserHolder.getUser();
    out.println("<br/>");
    out.println("<br/>");
    out.println("ID: " + user.getId());
    out.println("<br/>");
    for (String propertyName : user.propertyNames()) {
        out.println(propertyName + ": " + user.getProperty(propertyName));
        out.println("<br/>");
    }
%>
</body>
</html>
