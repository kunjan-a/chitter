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
    <!--<li class="tweetItem well" id="tweetItem_<\%= tweetId %>" style="width:650px">
        <div class="timeStamp" style="align:right;color:#a9a9a9;"><\%= time %></div>
        <span style="float:left" width:60px><img src="http://chitter.com//static/images/user/1" width="60px" height="60px"/></span>
        <span>
            <div class="tweetedBy"><a href="#!/users/<\%= tweetUserId %>/"><\%= name %></a></div>
            <div class="tweetContent"><\%= tweetText %></div>
            <\% if(loggedIn && tweetUserId != loggedInUserId){ %>
                <\% if(retweeted){ %>
                    <button disabled="disabled">Retweeted</button>
                <\% }else{ %>
                    <button onclick="reTweet(<\%= tweetId %>)">Retweet</button>
                <\% } %>
            <\% } %>

        </span>

    </li>-->

    <li id="tweetItem_<\%= tweetId %>" class="well" style="width:650px">
        <h3>
            <img src="http://chitter.com//static/images/user/1" alt="<\%= name %>" width="45" style="float:left">
            <div style="margin-top: 0px;">
                <a href="/#!/users/<\%= tweetUserId %>" title="<\%= name %>"><\%= name %></a>

            </div>
        </h3>
        <p style="margin-left:50px;margin-top:0px;word-wrap:break-word;"><\%= tweetText %></p>
        <span style="float:left;">
            <\% if(loggedIn && tweetUserId != loggedInUserId){ %>
                <\% if(retweeted){ %>
                    Retweeted
    >         <\% }else{ %>
                    <a href="javascript:void(0);" onclick="reTweet(<\%= tweetId %>)">Retweet</a>
                <\% } %>
            <\% } %>
        </span>
        <span class="date" style="float:right;font-weight:light;color:#a9a9a9;"><\%= time %></span>
    </li>
</script>

<script type="text/template" id="profileBanner">
    <div class="banner-container well">
        <div class="profilepic"><img src="http://chitter.com/<\%= photoPath %>" alt="User Name" width="100px" height="100px"></div>
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

<script type="text/template" id="homePageBanner">
    <div class="banner-container well">
        <div style="float:left; width:241px;"><h1>Welcome!</h1></div>
        <div style="width: 700px;margin-left: 220px;">
        <form action="" method="post" class="form-inline" onsubmit="sendTweet(this);return false;">
            <textarea name="text" id="tweetBox" class="blankTweetBox input-xlarge" rows="3" placeholder="Post new Tweet..." style="resize: none;width: 650px;"/>
            <div style="margin-left:20px;margin-right:20px;margin-top:5px;">
                <span style="float:left;">
                <div class="progress active" id="progress" style="width:300px;align:left !important; text-color:black !important;">
                    <div class="bar" id="bar" style="width: 0%;align:left !important; text-color:black !important;"><span>&nbsp;&nbsp;<span id="textCount">0</span>/140 Characters used.</span></div>

                </div>
                </span>
                <input type="submit" id="submitButton" disabled="disabled" class="btn" style="float:right;" value="Tweet">
            </div>
        </form>
        </div>
    </div>
</script>