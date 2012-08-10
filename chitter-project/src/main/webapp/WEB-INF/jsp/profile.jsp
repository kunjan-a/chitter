<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <link rel="stylesheet" type="text/css" href="/static/css/jquery-ui-1.8.custom.css">
    <style>
        .unfollowbtn {
            background: red;
            color: white;
        }
    </style>
    <script type="text/javascript">
        var loggedin = ${not empty sessionScope.userName};
        var loggedin_user = "${sessionScope.userID}";
        var profile_user = "${user.getId()}";
        var follows = ${follows};
        var userexists = ${userexists};
    </script>
</head>
<body>
<%@include file="top.jsp" %>
<div id="MsgBox">
</div>

<c:choose>
    <c:when test="${userexists}">
        <%@include file="prepareUserInfo.jsp" %>
        <button id="followBtn" class="followBtn" style="display: none;">Follow</button>
        <%@include file="tweetList.jsp" %>
    </c:when>
    <c:otherwise>
        <script>alert("The user ${user.getId()} doesn't exist");</script>
    </c:otherwise>
</c:choose>
</body>
<c:if test="${not empty sessionScope.userName && userexists && sessionScope.userID != user.getId()}">
    <script type="text/javascript">
        function unfollowUser(){
            if(follows)
                $.post("/action/unfollow", {"user_id":profile_user}, function (data) {
                    console.log(data);
                    if(data.Success == "1"){
                        follows = false;
                        $('#followBtn').html('Follow');
                    }else{
                        alert(data.msg);
                    }
                });
        }

        function followUser(){
            if(!follows)
                $.post("/action/follow", {"user_id":profile_user}, function (data) {
                    console.log(data);
                    if(data.Success == "1"){
                        follows = true;
                        $('#followBtn').html('Following');
                    }else{
                        alert(data.msg);
                    }
                });
        }

        $(document).ready(function(){
            $("#followBtn").html(follows?"Following":"Follow").show();

            $("#followBtn").click(function(){
                if(follows){
                    unfollowUser();
                }else{
                    followUser();
                }
            });

            $("#followBtn").mouseover(function(){
                if(follows){
                    $("#followBtn").addClass('unfollowbtn');
                    $("#followBtn").html('UnFollow');
                }
            });

            $("#followBtn").mouseout(function(){
                if($("#followBtn").hasClass('unfollowbtn')){
                    $("#followBtn").removeClass('unfollowbtn');
                }
                if(follows){
                    $("#followBtn").html('Following');
                }
            });
        });
    </script>
</c:if>

<c:if test="${userexists}">
    <script>
        $(document).ready(function(){
            $.post("/rest/${user.getId()}/tweets/", {}, function (data) {
                console.log(data);
                addNames(data);
                if(data.response.length > 0)
                    $(generateTweetHTML(data.response)).appendTo("#tweetList");
            });
        });
    </script>
</c:if>
</html>

