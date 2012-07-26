<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <style>
        #signUpForm {
            display: none;
        }

        #loginForm {
            display: block;
        }

        .errorInput {
            background: red;
            color: white;
        }

    </style>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript">
        var messageBox = "#MsgBox";

        function clearMessageBox() {
            $(messageBox).html('');
        }

        function clearErrorInputs() {
            $(":input").each(function () {
                if ($(this).hasClass("errorInput")) {
                    $(this).removeClass("errorInput");
                }
            });
        }

        function toggleForms() {
            clearMessageBox();
            clearErrorInputs();
            $('#signUpForm').slideToggle('slow');
            $('#loginForm').slideToggle('slow');
        }

        function requiredFieldsFilled(required) {
            var flag = true;
            _.each(required, function (inputElementId) {
                var inputElement = $('#' + inputElementId);
                if (inputElement.val() == "") {
                    flag = false;
                    inputElement.addClass("errorInput");
                    $('<div>' + inputElement.attr('pretty_name') + ' cannot be blank</div>').hide().appendTo($(messageBox)).show('slow');
                }
            })
            return flag;
        }

        function validateEmail(emails) {
            var flag = true;
            _.each(emails, function (inputElementId) {
                var inputElement = $('#' + inputElementId);
                if (!/^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/.test(inputElement.val())) {
                    flag = false;
                    inputElement.addClass("errorInput");
                    $('<div>Enter a valid email address</div>').hide().appendTo($(messageBox)).show('slow');
                }
            })
            return flag;
        }

        function Matches(a, b) {
            var el1 = $("#" + a);
            var el2 = $("#" + b);
            if (el1.val() != el2.val()) {
                $('<div>' + el1.attr('pretty_name') + ' and ' + el2.attr('pretty_name') + ' should match</div>').hide().appendTo($(messageBox)).show('slow');
                el1.addClass("errorInput");
                el2.addClass("errorInput");
                return false;
            }
            return true;
        }

        function validateLogin() {
            var flag = true;
            if (!requiredFieldsFilled(["l_password"])) {
                flag = false;
            }
            if (!validateEmail(["l_email"])) {
                flag = false;
            }
            return flag;
        }

        function doLogin(loginForm) {
            clearMessageBox();
            if (validateLogin()) {
                $.post('/request/login', $(loginForm).serialize(), function (data) {
                    if (data.Success == 1) {
                        window.location = "/";
                    } else {
                        $('<div>Invalid Email/Password</div>').hide().appendTo($(messageBox)).show('slow');
                    }

                });
            }
        }

        function validateSignUp() {
            var flag = true;
            if (!requiredFieldsFilled(["s_name", "s_password", "s_password2"])) {
                flag = false;
            }

            if (!validateEmail(["s_email"])) {
                flag = false;
            }

            if (!Matches("s_password", "s_password2")) {
                flag = false
            }

            return flag;
        }

        function doRegister(signUpForm) {
            clearMessageBox();
            if (validateSignUp()) {
                $.post('/request/register', $(signUpForm).serialize(), function (data) {
                    console.log(data);
                });
            }
        }

    </script>

</head>
<body>
<div id="MsgBox">
</div>
<div id="signUpForm">
    Signup:<br/>

    <form action="" method="post" onsubmit="doRegister(this);return false;">
        name: <input type="text" name="name" id="s_name" pretty_name="Name"/><br/>
        email: <input type="text" id="s_email" name="email" pretty_name="Email Address"/><br/>
        password: <input type="password" name="password" id="s_password" pretty_name="Password"/><br/>
        password again: <input type="password" name="password2" id="s_password2" pretty_name="Password Again"/><br/>
        Profile Picture: <input type="file" name="upload_filename" id="s_upload_filename"
                                pretty_name="Profile Picture"/><br/>
        <input type="submit">
    </form>
    Already have an account please <a href="javascript:void(0);" onclick="toggleForms();">Login</a>.
</div>

<div id="loginForm">
    Login:<br/>

    <form action="" method="post" onsubmit="doLogin(this);return false;">
        email: <input type="text" name="email" id="l_email" pretty_name="Email Address"><br/>
        password: <input type="password" name="password" id="l_password" pretty_name="Password"><br/>
        <input type="submit">
    </form>
    Don't have an account please <a href="javascript:void(0);" onclick="toggleForms();">Sign Up</a>.
</div>
</body>
<script type="text/javascript">
    $(":input").focus(function () {
        if ($(this).hasClass("errorInput")) {
            $(this).removeClass("errorInput");
        }
    });
</script>
</html>



