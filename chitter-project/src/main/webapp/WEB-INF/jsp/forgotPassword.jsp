<%--
  Created by IntelliJ IDEA.
  User: kunjan
  Date: 10/8/12
  Time: 1:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <script type="text/javascript">
        function validateLogin() {
            var errorMsg = emailValidator.validate("email");
            showMessage(errorMsg);
            return errorMsg == '';
        }

        function doLogin(loginForm) {
            clearMessageBox();
            if (validateLogin()) {
                $.post('/recoverAccount', $(loginForm).serialize(), function (data) {
                    showMessage(data.msg);
                    if(data.Success == "1"){
                        $('#loginForm').hide();
                    }

                });
            }
        }
    </script>
</head>
<body>
<%--Add a small textbox in which user can enter email id and send it to chitter.com/recoverAccount--%>
<div id="MsgBox">

</div>
<form action="" method="post" onsubmit="doLogin(this);return false;" autocomplete="on" id="loginForm">
    email: <input type="text" name="email" id="email" pretty_name="Email Address"><br/>
    <input type="submit">
</form>
</body>
</html>