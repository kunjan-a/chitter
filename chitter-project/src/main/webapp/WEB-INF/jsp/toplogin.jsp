<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.userName}">
        <div class="btn-group pull-right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                <img src="http://chitter.com/static/images/user/${sessionScope.userID}" width="20px" height="20px"/> ${sessionScope.userName}
                <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
                <li><a href="/#!/users/${sessionScope.userID}/">Profile</a></li>
                <li class="divider"></li>
                <li><a href="javascript:void(0)" onclick="doLogout()">Sign Out</a></li>
            </ul>
        </div>
        <div class="pull-right">
            <input type="text" placeholder="Search..." style="margin-top:5px;" id="autocomplete">
        </div>
    </c:when>
    <c:otherwise>
        <div class="pull-right">
            <form class="form-inline" style="margin-bottom:2px;"  action="" method="post" onsubmit="doLogin(this);return false;">
                <input type="text" name="email" id="l_email" pretty_name="Email Address" class="input-medium" placeholder="Email"/>
                <input type="password" name="password" id="l_password" pretty_name="Password" class="input-medium" placeholder="Password"/>
                <input type="submit" value="Sign In" class="btn"/>
            </form>
            <span><a href="javascript:void(0);" onclick="forgotPassword();">Forgot Password</a> | <a href="javascript:void(0)" onclick="registerAccount()">Register</a></span>
        </div>
    </c:otherwise>
</c:choose>
