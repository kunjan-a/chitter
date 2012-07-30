<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="tweetList">

</div>

<script>
    $(document).ready(function(){
        $("#tweetList").append(generateTweetHTML(testData.tweetList));
    });
</script>
