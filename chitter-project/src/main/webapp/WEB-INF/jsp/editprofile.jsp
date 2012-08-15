<!DOCTYPE html>
<!-- saved from url=(0066)http://twitter.github.com/bootstrap/examples/starter-template.html -->
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>Chitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="/static/css/bootstrap.css" rel="stylesheet">
    <style>
        html, body {
            height: 100%; /* needed for container min-height */
        }

        body {
            padding: 0px;
            margin: 0px;
            background: url("/static/images/body-bg.jpg") repeat-y center rgb(235, 235, 235);
            font-family: "Trebuchet MS", arial, sans-serif, verdana;
            padding-top: 40px; /* 60px to make the container go all the way to the bottom of the topbar */
        }

        .page-container {
            width: 990px;
            margin: 0px auto;
            top: 100px;
            height: auto !important; /* real browsers */
            height: 100%; /* IE6: treaded as min-height*/
            min-height: 90%; /* real browsers */
            position: relative; /* needed for footer positioning*/
        }

        div#banner {
            width: 990px;
            height: 124px;
            overflow: hidden;
            position: fixed;
        }

        #banner {
            font-family: arial, sans-serif, verdana;
            color: #333;
        }

        #banner div.profilepic {
            width: 120px;
            float: left;
            margin-top: 4px;
            margin-left: 25px;
        }

        #banner div.bannertext {
            float: left;
            width: 600px;
            height: 100%;
            margin-left: 15px;
            margin-top: 15px;
            text-align: left;
        }

        #banner div.profileRightDiv {
            float: right;
            margin-top: 10px;
            margin-right: 10px;
        }

        #banner div.bannertext h1 {
            font-size: 22px
        }

        #banner div.bannertext h2 {
            text-align: left;
            font-size: 16px
        }

        #banner a {
            text-decoration: none;
            color: #333;
        }

        #banner img {
            border: 0px;
        }

        div.banner-container {
            margin-left: 10px;
            margin-right: 30px;
            margin-bottom: 10px;
            height: 200px;
        }
    </style>
    <link href="/static/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script type="text/javascript">
        var loggedIn = ${not empty sessionScope.userName};
        var loggedInUserId = "${sessionScope.userID}";
        var currentUserId = '';
        var tweetStore;
        var followerStore;
        var followingStore;
        var timeouts = new Array();
    </script>
</head>

<body>

<div class="navbar navbar-fixed-top" style="z-index: 1">
    <div class="navbar-inner">
        <div class="container-fluid">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
            <a class="brand" href="#">Chitter</a>
            <%@include file="toplogin.jsp" %>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#about">About</a></li>
                    <li><a href="#contact">Contact</a></li>
                </ul>
            </div>
            <!--/.nav-collapse -->
        </div>
    </div>
</div>

<!--<div class="container" style="background-color:#e0ffff;height:100%;min-height: 100%;">
    Content goes here
    <div class="footer" style="position:absolute;bottom:0">Empty Div</div>
</div>-->
<div class="page-container">
    <div id="banner" style="top:40px;">

    </div>
    <div style="width: 250px;position: fixed;top:163px;bottom: auto;height: 100%;margin-top: 23px; margin-left: 10px;"
         id="leftPane"></div>


    <div class="content" style="margin-left: 270px; padding-top: 46px;">
        <li id="tweetList" style="list-style-type:none;"></li>
        <li id="userList" style="list-style-type:none;"></li>

    </div>
</div>

<div class="modal hide fade" id="myModal" style="display: none;">
    <div class="modal-header">
        <h3>Page Not Found</h3>
    </div>
    <div class="modal-body">
        <p>You have entered an invalid url. Please go to the <a href="javascript:void();"
                                                                onclick="$('#myModal').modal('hide'); window.location.hash='#!/';">Home</a>
            page or enter any valid url.</p>
    </div>
    <div class="modal-footer">
    </div>
</div>
<div id="MsgBox" style="background-color: yellowgreen;position: fixed;top: 5px;z-index: 10000;width: 500px;margin-left: auto;margin-right: auto;display: none;left: 0px;right: 0px;font-weight: bold;padding: 3px;"></div>
<c:if test="${empty sessionScope.userName}">
    <div class="modal hide fade" id="forgotPasswordModal">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">x</button>
            <h3>Forgot your password?</h3>
        </div>
        <div class="modal-body">
            Provide us with the email address you used to register your account and we will email you the steps for password recovery.<br/>
            <input type="text" name="email" id="forgotPassword_email" pretty_name="Email Address" class="input-xlarge" placeholder="Email"><br/>
        </div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary" onclick="sendRecoveryRequest();" id="forgotPassword_btn">Send</button>
        </div>
    </div>

    <div class="modal hide fade" id="signUpModal">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">×</button>
            <h3>Modal header</h3>
        </div>
        <div class="modal-body">
            <p>One fine body…</p>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">Close</a>
            <a href="#" class="btn btn-primary">Save changes</a>
        </div>
    </div>
</c:if>


<!-- Le javascript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/static/js/jquery.min.js"></script>
<script src="/static/js/underscore.min.js"></script>
<script src="/static/js/underscore.string.min.js"></script>
<script type="text/javascript"> _.mixin(_.str.exports()) </script>
<script type="text/javascript" src="/static/js/jsonpath.js"></script>
<script src="/static/js/bootstrap/bootstrap-transition.js"></script>
<script src="/static/js/bootstrap/bootstrap-alert.js"></script>
<script src="/static/js/bootstrap/bootstrap-modal.js"></script>
<script src="/static/js/bootstrap/bootstrap-dropdown.js"></script>
<script src="/static/js/bootstrap/bootstrap-scrollspy.js"></script>
<script src="/static/js/bootstrap/bootstrap-tab.js"></script>
<script src="/static/js/bootstrap/bootstrap-tooltip.js"></script>
<script src="/static/js/bootstrap/bootstrap-popover.js"></script>
<script src="/static/js/bootstrap/bootstrap-button.js"></script>
<script src="/static/js/bootstrap/bootstrap-collapse.js"></script>
<script src="/static/js/bootstrap/bootstrap-carousel.js"></script>
<script src="/static/js/bootstrap/bootstrap-typeahead.js"></script>
<script type="text/javascript" src="/static/js/tweetModifiers.js"></script>
<%@include file="clientSideTemplates.jsp" %>
<script type="text/javascript" src="/static/js/templates.js"></script>
<script type="text/javascript" src="/static/js/stores.js"></script>
<script type="text/javascript" src="/static/js/handlers.js"></script>
<script type="text/javascript" src="/static/js/login.js"></script>

<c:if test="${empty sessionScope.userName}">
    <script type="text/javascript">
        $('#forgotPasswordModal').modal({
            keyboard:false,
            show:false
        });
        function forgotPassword(){
            $('#forgotPasswordModal').modal('show');
        }

        function validateForgotPasswordForm() {
            var errorMsg = emailValidator.validate("forgotPassword_email");
            showMessage(errorMsg);
            return errorMsg == '';
        }

        function sendRecoveryRequest(loginForm) {
            if (validateForgotPasswordForm()) {
                $.post('/recoverAccount', {'email':$('#forgotPassword_email').val()}, function (data) {
                    showMessage(data.msg);
                });
            }
        }
    </script>
</c:if>
<script>
    $('#myModal').modal({
        keyboard:false,
        show:false
    });
    $(window).bind('hashchange', hashChangeHandler);
    $(document).ready(function () {
        $(window).trigger('hashchange');
    });
</script>

<script type="text/javascript">

    function showMessage(msg) {
        $('<div>'+msg+'</div>').appendTo($('#MsgBox'));
        $('#MsgBox').slideDown('slow');
        clearTimeouts();
        timeouts[timeouts.length]= window.setTimeout(function(){$('#MsgBox').slideUp(),$('#MsgBox').html('');},5000);
    }

    function clearTimeouts(){
        if (timeouts) for (var i in timeouts) if (timeouts[i]) window.clearTimeout(timeouts[i]);
        timeouts = [];
    }


</script>

</body>
</html>