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
        var testData = {
            count : 1,
            tweetList : [
                <c:choose>
                    <c:when test="${user1}">
                        {
                            user_id : '1',
                            name : 'A B',
                            text : 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
                        }
                    </c:when>
                    <c:otherwise>
                        {
                            user_id : '2',
                            name : 'C D',
                            text : '1011110000000000011111111111111111111111111'
                        }
                    </c:otherwise>
                </c:choose>


            ]
        };

        var loggedin_user = "${sessionScope.userID}";
        var profile_user = "${user.getId()}";
        var follows = ${Follows};
    </script>
</head>
<body>
<%@include file="top.jsp" %>
<div id="MsgBox">
</div>
<c:choose>
    <c:when test="${userexists}">
        ${user.getName()}'s Page
        <c:if test="${not empty sessionScope.userName}">
            <c:if test="${sessionScope.userID != user.getId()}">
                <button id="followBtn" style="width:100;height:100">
                    <c:choose>
                        <c:when test="${Follows}">
                            Following
                        </c:when>
                        <c:otherwise>
                            Follow
                        </c:otherwise>
                    </c:choose>
                </button>
            </c:if>
        </c:if>
        <%@include file="tweetList.jsp" %>
    </c:when>
    <c:otherwise>
        <script>alert("The user ${user.getId()} doesn't exist");</script>
    </c:otherwise>
</c:choose>
</body>
<c:if test="${sessionScope.userID != user.getId()}">
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
            $.post("/action/fetchTweets/${user.getId()}", {}, function (data) {
                console.log(data);
                $(generateTweetHTML(data.response)).appendTo("#tweetList");
            });

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

<script>
    $(document).ready(function(){
        $.post("/action/fetchTweets/${user.getId()}", {}, function (data) {
            console.log(data);
            if(data.response.length > 0)
            $(generateTweetHTML(data.response)).appendTo("#tweetList");
        });
    });
</script>


</html>

