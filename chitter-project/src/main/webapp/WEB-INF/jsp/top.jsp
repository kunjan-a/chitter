<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${not empty sessionScope.userName}">
        <div id="top">Welcome ${sessionScope.userName}
            <input id="autocomplete" />
            <a href="/logout">Logout</a>
        </div>
    </c:when>
    <c:otherwise>
        <script type="text/javascript" src="/static/js/login.js"></script>
        <form action="" method="post" onsubmit="doLogin(this);return false;">
            email: <input type="text" name="email" id="l_email" pretty_name="Email Address">
            password: <input type="password" name="password" id="l_password" pretty_name="Password">
            <input type="submit">
        </form>
    </c:otherwise>
</c:choose>
<script type="text/javascript" src="/static/js/jquery-ui-1.8.custom.min.js"></script>
<script>
    $(document).ready(function() {
        $("input#autocomplete").autocomplete({
            source: "/search/users",
            focus: function(event, ui) {event.preventDefault();event.stopPropagation();},
            select: function(event,ui){
                window.location = '/users/'+ui.item.value;
            }

        });
    });
</script>


