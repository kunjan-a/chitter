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
    <script type="text/javascript">
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
        function validateForm() {
            var errorMsg = emailValidator.validate("email");
            //showMessage(errorMsg);
            return errorMsg == '';
        }

        function sendRecoverRequest(loginForm) {
            //clearMessageBox();
            if (validateForm()) {
                $.post('/recoverAccount', $(loginForm).serialize(), function (data) {
                    ///showMessage(data.msg);
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
<form action="" method="post" onsubmit="sendRecoverRequest(this);return false;" autocomplete="on" id="loginForm">
    email: <input type="text" name="email" id="email" pretty_name="Email Address"><br/>
    <input type="submit">
</form>
</body>
</html>