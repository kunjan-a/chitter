<script type="text/template" id="tweetItemTemplate">
    <div class="tweetItem" id="tweetItem_<\%= tweetId %>">
        <div class="tweetedBy"><a href="/user/<\%= tweetUserId %>"><\%= name %></a></div>
        <div class="tweetContent"><\%= tweetText %></div>
        <div class="timeStamp"><\%= time %></div>
        <\% if(loggedin && tweetUserId != loggedin_user){ %>
            <\% if(retweeted){ %>
                <button disabled="disabled">Retweeted</button>
            <\% }else{ %>
                <button onclick="reTweet(<\%= tweetId %>)">Retweet</button>
            <\% } %>
        <\% } %>
    </div>
</script>

<script type="text/template" id="tweetItemTemplateNew">
    <li class="tweetItem well" id="tweetItem_<\%= tweetId %>" style="width:650px">
        <div class="tweetedBy"><a href="#!/users/<\%= tweetUserId %>/"><\%= name %></a></div>
        <div class="tweetContent"><\%= tweetText %></div>
        <div class="timeStamp"><\%= time %></div>
        <\% if(loggedIn && tweetUserId != loggedInUserId){ %>
        <\% if(retweeted){ %>
        <button disabled="disabled">Retweeted</button>
        <\% }else{ %>
        <button onclick="reTweet(<\%= tweetId %>)">Retweet</button>
        <\% } %>
        <\% } %>
    </li>
</script>

<script type="text/template" id="profileBanner">
    <div class="banner-container well">
        <div class="profilepic"><img src="http://localhost/static/images/user/x.jpg" alt="User Name" width="100px" height="100px"></div>
        <div class="bannertext">
            <h1><\%= name %></h1>
            <h2>additional details</h2>
        </div>
        <div class="profileRightDiv">
            <button id="followBtn" class="btn" userid="<\%= id %>" follows="<\%= follows %>"><\% if(id == loggedInUserId){ %> Edit Profile <\% }else if(follows){ %>Following <\% }else{ %>Follow <\% } %></button>
            <div><a href="#!/users/<\%= id %>/">Tweets: <span id="tweetCount"><\%= tweetCount %></span></a></div>
            <div><a href="#!/users/<\%= id %>/following/">Following: <span id="followingCount"><\%= followingCount %></span></a></div>
            <div><a href="#!/users/<\%= id %>/followers/">Followers:<span id="followerCount"><\%= followerCount %></span></a></div>
        </div>
    </div>
</script>

<script type="text/template" id="leftPaneContent">
    <div class="well">
        <div>Tweets</div>
        <div>Following</div>
        <div>Followers</div>
    </div>
</script>

<script type="text/template" id="userItemTemplate">
    <li class="followerItem">
        <div class="followerName"><a href="/user/<\%= id %>"><\%= name %></a>
            <button id="followBtn" class="followBtn" userid="<\%= id %>" follows="<\%= follows %>"><\% if(id == loggedin_user){ %> Edit Profile <\% }else if(follows){ %>Following <\% }else{ %>Follow <\% } %></button></div>
    </li>
</script>

<script type="text/template" id="userItemTemplateNew">
    <li class="userItem well" style="width:650px">
        <div class="userName" style="float:left;"><a href="#!/users/<\%= id %>/"><\%= name %></a></div>
        <div class="followBtnHolder" style="float:right">
            <button class="btn" userid="<\%= id %>" follows="<\%= follows %>"><\% if(id == loggedInUserId){ %> Edit Profile <\% }else if(follows){ %>Following <\% }else{ %>Follow <\% } %></button>
        </div>
    </li>
</script>