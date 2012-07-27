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
    <script type="text/javascript" src="/static/js/common.js"></script>
    <script type="text/javascript" src="/static/js/login.js"></script>
    <script type="text/javascript" src="/static/js/signup.js"></script>
    <script type="text/javascript">
        function toggleForms() {
            clearMessageBox();
            clearErrorInputs();
            $('#signUpForm').slideToggle('slow');
            $('#loginForm').slideToggle('slow');
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



