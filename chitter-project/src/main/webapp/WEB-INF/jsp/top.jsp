<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.userName}">
        <h1>Welcome ${sessionScope.userName}</h1> <a href="/user/logout">Logout</a>
    </c:when>
    <c:otherwise>
        <h1>Please <a href="/user/login">Login</a></h1>
    </c:otherwise>
</c:choose>