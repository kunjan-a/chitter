<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.userName}">
    <div id="myCarousel" class="carousel slide">
        <!-- Carousel items -->
        <div class="carousel-inner">
            <div class="active item">
                <img alt="" src="http://localhost/static/images/carousel/registration.jpg">
                <div class="carousel-caption">
                    <h4>Easy Registration</h4>
                    <p>No need to fill long list of details. Your email-id, password and preferred Cheet name are all that is required.</p>
                </div>
            </div>
            <div class="item">
                <img alt="" src="http://localhost/static/images/carousel/profile.jpg">
                <div class="carousel-caption">
                    <h4>Create Profile ( Optional )</h4>
                    <p>Create your avatar by adding a cool pic, and a minimal bio.</p>
                </div>
            </div>
            <div class="item">
                <img alt="" src="http://localhost/static/images/carousel/cheet.jpg">
                <div class="carousel-caption">
                    <h4>Make Cheets </h4>
                    <p>Make cheets to let everyone know about your happening life.</p>
                </div>
            </div>
            <div class="item">
                <img alt="" src="http://localhost/static/images/carousel/search_follow.jpg">
                <div class="carousel-caption">
                    <h4>Search Cheeters </h4>
                    <p>Easily find your friends, crushes and loved ones. Follow them to receive their cheets directly on your home page.</p>
                </div>
            </div>
            <div class="item">
                <img alt="" src="http://localhost/static/images/carousel/followers.jpg">
                <div class="carousel-caption">
                    <h4>Know your fans </h4>
                    <p>Don't forget to check out your huge fan following.</p>
                </div>
            </div>
            <div class="item">
                <img alt="" src="http://localhost/static/images/carousel/favourite.jpg">
                <div class="carousel-caption">
                    <h4>Check your fans </h4>
                    <p>If you like a cheet or a cheeter then do remember to add it to your elite list of favourites.</p>
                </div>
            </div>
        </div>
        <!-- Carousel nav -->
        <a class="carousel-control left" href="#myCarousel" data-slide="prev">&lsaquo;</a>
        <a class="carousel-control right" href="#myCarousel" data-slide="next">&rsaquo;</a>
    </div>
    <script type="text/javascript">
        $('.carousel').carousel({
        interval: 1000
        })
    </script>
</c:if>
