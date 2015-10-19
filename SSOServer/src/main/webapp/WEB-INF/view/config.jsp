<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="tagLibs.jsp" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="${path}/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript">
        function refreshConfig() {
            $.ajax({
                type: "POST",
                url:"${path}/config/refresh",
                data:{
                    code:$("#code").val()
                },
                success: function (data) {
                    //var data = $.parseJSON(data);
                    if (data.resp == "success") {
                        alert("更新成功");
                    } else {
                        alert("更新失败");
                    }
                },
                error: function () {
                    alert("服务器忙，请稍后再试!");
                }
            });
        }
    </script>
</head>
<body>
<input type="text" id="code" >
<button onclick="refreshConfig();">更新配置</button>
</body>
</html>
