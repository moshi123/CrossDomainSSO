<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="tagLibs.jsp" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="${path}/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="${path}/js/md5.js"></script>
    <script type="text/javascript" src="${path}/js/cookieUtil.js"></script>
    <script type="text/javascript">
        var UNAME_COOKIE_NAME = "lastLoginUserName";
        $(function () {
            var eleName = $("input[name=name]");
            eleName.val(Cookie.get(UNAME_COOKIE_NAME));

            // 登录按钮被点击时记住当前name
            $("form").submit(function () {
                Cookie.set(UNAME_COOKIE_NAME, $.trim(eleName.val()), null, 7 * 24 * 60, null, null);

                // 将密码字段使用 MD5(MD5(密码) + 验证码）编码后发给服务端
                var elePasswd = $("input[name=passwd]");
                var passwd = elePasswd.val();
                elePasswd.val($.md5($.md5(passwd) + $("input[name=captcha]").val()));
            });

            // 加载验证码
            drawCaptcha();

        });

        /**
         * 画验证码.
         */
        function drawCaptcha() {
            $.ajax("${path}/preLogin").done(function (data) {
                console.log(data);
                $("#captchaImg").attr("src", data.imgData);
            }).fail(function () {
                alert("验证码加载失败");
            });
        }
    </script>
</head>
<body>

<c:if test="${empty loginUser}">
    <c:if test="${not empty errorMsg}">
        <p style="color:red;font-weight:bold;">${errorMsg}</p>
    </c:if>
    <form action="${path}/login" method="post">
        <input type="hidden" name="redirectURL" value="${param.redirectURL}">
        <p>账号：<input type="text" name="name" autocomplete="off"/></p>

        <p>密码：<input type="password" name="password" autocomplete="off"/></p>

        <p>验证码：<input style="width:80px;" type="text" name="captcha" autocomplete="off"/><img src=""
                                                                                              onclick="drawCaptcha();"
                                                                                              id="captchaImg"
                                                                                              style="cursor:pointer;">
        </p>

        <p><input type="submit" value="登录"/></p>
    </form>
</c:if>


<c:if test="${not empty loginUser}">
    <p>欢迎：${loginUser}
        <button style="margin-left:20px;" onclick="location.href='${path}/logout'">退出</button>
    </p>
    <ul>
        <c:forEach items="${sysList }" var="sys">
            <li><a href="${sys.homeURL }" target="_blank">${sys.name}</a></li>
        </c:forEach>
    </ul>
</c:if>

<!-- 为每个业务系统设置cookie -->
<c:forEach items="${sysList}" var="sys">
    <script type="text/javascript" src="${sys.baseURL}/cookie_set?vt=${VT}"></script>
</c:forEach>

</body>
</html>
