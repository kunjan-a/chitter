<%--
  Created by IntelliJ IDEA.
  User: kunjan
  Date: 6/8/12
  Time: 11:38 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <script type="text/javascript" src="/static/js/jsonpath.js"></script>
    <script type="text/javascript">
        var loggedin = ${not empty sessionScope.userName};
        var loggedin_user = "${sessionScope.userID}";
    </script>
    <style>
        .unfollowbtn {
            background: red;
            color: white;
        }
    </style>
</head>
<body>
<%@include file="top.jsp" %>
<%@include file="prepareUserInfo.jsp" %>
<ul id="followersList">

</ul>
</body>
<script>

    function unfollowUser(user){
        if($(user).attr('follows') == "true")
            $.post("/action/unfollow", {"user_id":$(user).attr('userid')}, function (data) {
                console.log(data);
                if(data.Success == "1"){
                    $(user).attr('follows','false');
                    $(user).html('Follow');
                }else{
                    alert(data.msg);
                }
            });
    }

    function followUser(user){
        if($(user).attr('follows') == "false")
            $.post("/action/follow", {"user_id":$(user).attr('userid')}, function (data) {
                console.log(data);
                if(data.Success == "1"){
                    $(user).attr('follows','true');
                    $(user).html('Following');
                }else{
                    alert(data.msg);
                }
            });
    }

    $("#followersList").delegate("button", "click", function(event){
        var userid = $(this).attr('userid');
        if(userid == loggedin_user){
            alert("Editing Profile");
        }else{
            if($(this).attr('follows') == "true"){
                unfollowUser(this);
            }else{
                followUser(this);
            }
        }
    });

    $("#followersList").delegate("button", "mouseover", function(event){
        if($(this).attr('follows') == "true"){
            $(this).addClass('unfollowbtn');
            $(this).html('UnFollow');
        }
    });

    $("#followersList").delegate("button", "mouseout", function(event){
        if($(this).hasClass('unfollowbtn')){
            $(this).removeClass('unfollowbtn');
        }
        if($(this).attr('follows') == "true"){
            $(this).html('Following');
        }
    });

    $(document).ready(function(){
        $.post("/rest/${user.getId()}/followers/", {}, function (data) {
            console.log(data);
            if(data.Success == "1"){
                _.map(data.response,function(o){
                    o["follows"] = jsonPath(data, "$.follows[?(@.celebrityId == "+o.id+")]") == false?false:true;
                });
                console.log(data);
                if(data.response.length > 0)
                 $(generateFollowerHTML(data.response)).appendTo("#followersList");
            }


        });
    });
</script>
</html>