<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <link rel="stylesheet" type="text/css" href="/static/css/jquery-ui-1.8.custom.css">
    <style>
        .blankTweetBox {
            color: #808080;
        }

        .nonBlankTweetBox {
            color: black;
        }
    </style>
    <script type="text/javascript">
        function sendTweet(tweetForm) {
            $('#submitButton').attr('disabled','disabled');
            $.post('/action/tweet', $(tweetForm).serialize(), function (data) {
                console.log(data);
                if(data.Success == "1" && data.response.length > 0){
                    addNames(data);
                    $(generateTweetHTML(data.response)).hide().prependTo("#tweetList").show('slow');
                    $('#tweetBox').val('');
                    $('#tweetBox').trigger('blur');
                    showMessageAndClear("Tweet successfully posted");
                }else{
                    showMessageAndClear("Error Posting Tweet. "+data.msg);
                }

            });
        }
    </script>

    <script>
        function resetTweetBox(){
            $('#tweetBox').val('Post new Tweet...');
            $('#tweetBox').addClass("blankTweetBox");
            $('#submitButton').attr('disabled','disabled');
        }
    </script>
</head>
<body>
<%@include file="top.jsp" %>
<form action="" method="post" onsubmit="sendTweet(this);return false;">
    <textarea maxlength="140" name="text" id="tweetBox" class="blankTweetBox" rows="2" cols="20">Post new
        Tweet...</textarea><br/>
    <input type="submit" id="submitButton" disabled="disabled"><br/>
    <span id="textCount">0</span>/140 Characters used.
</form>
<div id="MsgBox">
</div>
<%@include file="tweetList.jsp" %>
</body>
<script type="text/javascript">
    $("#tweetBox").focus(function () {
                if ($(this).hasClass("blankTweetBox")) {
                    $(this).removeClass("blankTweetBox");
                    $(this).addClass("nonBlankTweetBox");
                    $(this).val('');
                    $(this).attr('rows', 3);
                    $(this).attr('cols', 80);
                    $('#submitButton').removeAttr('disabled');
                }
            }
    )

    $("#tweetBox").blur(function () {
                if ($(this).val() == '') {
                    $(this).removeClass("nonBlankTweetBox");
                    $(this).addClass("blankTweetBox");
                    $(this).val('Post new Tweet...');
                    $(this).attr('rows', 2);
                    $(this).attr('cols', 20);
                    $('#submitButton').attr('disabled', 'disabled');
                }
            }
    )

    $('#tweetBox').keyup(function(){
        $("#textCount").html($(this).val().length);
    });
</script>
<script>
    $(document).ready(function(){
        $.post("/rest/${sessionScope.userID}/feeds/", {}, function (data) {
            console.log(data);
            addNames(data);
            if(data.response.length > 0)
                $(generateTweetHTML(data.response)).appendTo("#tweetList");
        });
    });
</script>
</html>

