<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.userName}">
        <div class="btn-group pull-right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                <img src="http://localhost/static/images/user/x.jpg" widht="20px" height="20px"/> ${sessionScope.userName}
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="#!/users/${sessionScope.userID}/">Profile</a></li>
                <li class="divider"></li>
                <li><a href="/logout">Sign Out</a></li>
            </ul>
        </div>
        <div class="pull-right">
            <input type="text" placeholder="Search..." style="margin-top:5px;">
        </div>
    </c:when>
    <c:otherwise>
        <div class="pull-right">
            <form class="form-inline" style="margin-bottom:2px;">
                <input type="text" class="input-small" placeholder="Email">
                <input type="password" class="input-small" placeholder="Password">
                <button type="submit" class="btn">Sign in</button>
            </form>
        </div>
    </c:otherwise>
</c:choose>
