<%--
  Created by IntelliJ IDEA.
  User: kunjan
  Date: 10/8/12
  Time: 1:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script>
        function genericValidator(){
            this.msg = "message to display";
            this.test = function(inputElement){return false;};//return true if validation check fails
            this.validate =  function() {
                var msg = this.msg;
                return _.chain(arguments)//variable arguments indicating ids of the elements to be tested
                        .map(function (inputElementId) {return $('#' + inputElementId);})
                        .filter(this.test)
                        .map(function(inputElement){inputElement.addClass("errorInput");return inputElement;})
                        .reduce(function (memo, inputElement) {return memo + '<div>' + inputElement.attr('pretty_name') + ' ' + msg + '</div>';}, '')
                        .value();
            };
        }

        requiredFieldsValidator = new genericValidator();
        requiredFieldsValidator.msg = "cannot be blank";
        requiredFieldsValidator.test = function (inputElement) {
            return inputElement.val() == '';
        };

        emailValidator = new genericValidator();
        emailValidator.msg = "is not a valid one";
        emailValidator.test = function (inputElement) {
            return !/^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/.test(inputElement.val());
        };

        function Matches(a, b) {
            var el1 = $("#" + a);
            var el2 = $("#" + b);
            if (el1.val() != el2.val()) {
                el1.addClass("errorInput");
                el2.addClass("errorInput");
                return '<div>' + el1.attr('pretty_name') + ' and ' + el2.attr('pretty_name') + ' should match</div>';
            }
            return '';
        }

        function validateSignUp() {
            var functionOutputs = [requiredFieldsValidator.validate("password", "password2"), Matches("password", "password2")];
            var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
                return memo + functionOutput;
            }, '');
           // showMessage(errorMsg);
            return errorMsg == '';
        }

        function doLogin(signUpForm) {
            //clearMessageBox();
            if (validateSignUp()) {
                $.post("/resetPasswordByRecovery", $(signUpForm).serialize(), function (data) {
                    console.log(data);
                    /*if (data.Success == "1") {
                        showMessage('Password Successfully reset. Please <a href="/login">login with your new password.</a>');

                    } else {
                        showMessage(data.msg);

                    }*/

                });


            }
        }
    </script>
</head>
<body>
<div id="MsgBox">

</div>
<%--Add New Password and retype password checkboxes, only if the variable "valid" is set to 1, else say the recovery link
has expired.
On submitting the password values make an ajax call using POST to chitter.com/resetPasswordByRecovery with parameter
password.
If JSon response has Success = 1, then ask user to go to login page, else invalid session and ask to regenerate recovery
link by going to chitter.com/forgotPassword--%>
<c:choose>
    <c:when test="${valid}">
        <form action="" method="post" onsubmit="doLogin(this);return false;">
            password: <input type="password" name="password" id="password" pretty_name="Password"/><br/>
            password again: <input type="password" name="password2" id="password2" pretty_name="Password Again"/><br/>
            <input type="submit">
            <a href="javascript:void(0);" onclick="doLogin(this);">dddd</a>
        </form>
    </c:when>
    <c:otherwise>
        The recovery link has expired.
    </c:otherwise>
</c:choose>
</body>
</html>