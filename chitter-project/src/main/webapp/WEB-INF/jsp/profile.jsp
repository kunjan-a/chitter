<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <script type="text/javascript" src="/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="/static/js/underscore.min.js"></script>
    <script type="text/javascript" src="/static/js/common.js"></script>
    <link rel="stylesheet" type="text/css" href="/static/css/jquery-ui-1.8.custom.css">
    <script type="text/javascript">
        var testData = {
            count : 1,
            tweetList : [
                <c:choose>
                    <c:when test="${user1}">
                        {
                            id : '1',
                            name : 'A B',
                            tweet : 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
                        }
                    </c:when>
                    <c:otherwise>
                        {
                            id : '2',
                            name : 'C D',
                            tweet : '1011110000000000011111111111111111111111111'
                        }
                    </c:otherwise>
                </c:choose>


            ]
        };
    </script>
</head>
<body>
<%@include file="top.jsp" %>
<div id="MsgBox">
</div>
<c:choose>
    <c:when test="${userexists}">
        <%@include file="tweetList.jsp" %>
    </c:when>
    <c:otherwise>
        <script>alert("The user ${name} doesn't exist");</script>
    </c:otherwise>
</c:choose>
</body>
</html>

