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
    <div class="accordion" id="accordion2" style="width:500px;margin-left: auto;margin-right: auto;">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
                    <h3>Change Profile Picture</h3>
                </a>
            </div>
            <div id="collapseOne" class="accordion-body collapse" style="height: auto; ">
                <div class="accordion-inner">
                    <form class="form-inline well" action="/action/uploadProfilePic" method="post" enctype="multipart/form-data" onsubmit="return checkfile();">
                        <input type="file" name="picFile" id="upload_filename" pretty_name="Profile Picture" class="btn btn-success"/>
                        <input type="submit" value="Upload Photo" class="btn btn-primary"/>

                    </form>


                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
                    <h3>Change Profile Info</h3>
                </a>
            </div>
            <div id="collapseTwo" class="accordion-body in collapse" style="height: 0px; ">
                <div class="accordion-inner">
                    <form class="form-horizontal well" onsubmit="updateProfileInfo(this);return false;" id="updateProfileForm">
                        <fieldset>
                            <div class="control-group">
                                <label class="control-label" for="name">Full Name</label>
                                <div class="controls">
                                    <input type="text" name="name" id="name" pretty_name="Name" class="input-xlarge" placeholder="Full Name" value="${user.getName()}"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="bio">Short Bio</label>
                                <div class="controls">
                                    <input type="text" name="bio" id="bio" pretty_name="Bio" class="input-xlarge" placeholder="Short Bio" value="${user.getBio()}"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="location">Location</label>
                                <div class="controls">
                                    <input type="text" name="location" id="location" pretty_name="Location" class="input-xlarge" placeholder="Location" value="${user.getLocation()}"/>
                                </div>
                            </div>
                            <div class="control-group">
                                <label class="control-label" for="website">Website</label>
                                <div class="controls">
                                    <input type="text" name="website" id="website" pretty_name="Website" class="input-xlarge" placeholder="Website" value="${user.getWebsite()}"/>
                                </div>
                            </div>
                        </fieldset>
                        <input type="submit" class="btn btn-primary" value="Save Changes">
                    </form>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree">
                    <h3>Change Password</h3>
                </a>
            </div>
            <div id="collapseThree" class="accordion-body in collapse" style="height: 0px; ">
                <div class="accordion-inner">
                    <form method="post" class="well" onsubmit="changePassword(this);return false;" id="changePasswordForm">
                        <input type="password" name="currentPassword" id="password_old" pretty_name="Current Password" class="input-xlarge" placeholder="Current Password"/><br/>
                        <input type="password" name="password" id="password_new" pretty_name="New Password" class="input-xlarge" placeholder="New Password"/><br/>
                        <input type="password" name="password2" id="password_new_again" pretty_name="New Password Again" class="input-xlarge" placeholder="New Password Again"/><br/>
                        <input type="submit" class="btn btn-primary" value="Save Changes">
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="MsgBox" style="background-color: yellowgreen;position: fixed;top: 5px;z-index: 10000;width: 500px;margin-left: auto;margin-right: auto;display: none;left: 0px;right: 0px;font-weight: bold;padding: 3px;"></div>

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

<script type="text/javascript">
    function checkfile(){
        var fileName = $('#upload_filename').val();
        var ext = _(fileName).strRightBack('.').toUpperCase();
        //alert(_.include(["GIF","JPG","JPEG","PNG"],ext));
        if(_.include(["GIF","JPG","JPEG","PNG"],ext)){
            return true;
        }else{
            showMessage("Upload jpg, gif or png images only");
            return false;
        }
    }

</script>

<script>
    $('#myModal').modal({
        keyboard:false,
        show:false
    });

    $('.collapse').collapse();
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

    function validatePasswordForm() {
        var functionOutputs = [requiredFieldsValidator.validate("password_old","password_new","password_new_again"), Matches("password_new","password_new_again")];
        var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
            return memo + functionOutput;
        }, '');
        showMessage(errorMsg);
        return errorMsg == '';
    }

    function changePassword(passWordForm){
        if (validatePasswordForm()) {
            $.post('/action/changePassword', $(passWordForm).serialize(), function (data) {
                showMessage(data.msg);
            });
        }
    }

    function validateUpdateProfileForm() {
        var functionOutputs = [requiredFieldsValidator.validate("name")];
        var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
            return memo + functionOutput;
        }, '');
        showMessage(errorMsg);
        return errorMsg == '';
    }

    function updateProfileInfo(updateProfileForm){
        if (validateUpdateProfileForm()) {
            $.post('/action/updateProfile', $(updateProfileForm).serialize(), function (data) {
                if (data.Success == 1) {
                    showMessage(data.msg);
                } else {
                    showMessage(data.msg);
                }
            });
        }
    }


</script>

</body>
</html>