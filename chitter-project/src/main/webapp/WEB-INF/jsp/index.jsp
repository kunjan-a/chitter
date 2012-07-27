<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <script>
        function sendTweet(tweetForm) {
            $.post('/action/tweet', $(tweetForm).serialize(), function (data) {
                console.log(data);
            });
        }
    </script>
    <style>
        .blankTweetBox {
            color: #808080;
        }

        .nonBlankTweetBox {
            color: black;
        }
    </style>
</head>
<body>
<%@include file="top.jsp" %>
<form action="" method="post" onsubmit="sendTweet(this);return false;">
    <textarea maxlength="140" name="tweet" id="tweetBox" class="blankTweetBox" rows="2" cols="20">Post new
        Tweet...</textarea><br/>
    <input type="submit" id="submitButton" disabled="disabled">
</form>
<div id="MsgBox">
</div>
<div id="tweetFeeds">
</div>
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

</script>
</html>

