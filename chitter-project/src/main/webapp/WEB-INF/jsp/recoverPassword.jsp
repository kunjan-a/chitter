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
</head>
<body>
Add New Password and retype password checkboxes, only if the variable "valid" is set to 1, else say the recovery link
has expired.
On submitting the password values make an ajax call using POST to chitter.com/resetPasswordByRecovery with parameter
password.
If JSon response has Success = 1, then ask user to go to login page, else invalid session and ask to regenerate recovery
link by going to chitter.com/forgotPassword
</body>
</html>