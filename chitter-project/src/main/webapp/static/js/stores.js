function TweetStore(){
    this.tweets = [];

    this.add = function(data){
        processTweets(data);
        var tweets = data.response;
        console.log(data);
        for(var i in tweets){
            var clonedTweet = _.clone(tweets[i]);
            this.tweets.push(clonedTweet);
            if(arguments.length > 1 && arguments[1]){
                $(generateTweetHTML([clonedTweet])).hide().prependTo("#tweetList").show('slow');
            }else{
                $(generateTweetHTML([clonedTweet])).hide().appendTo("#tweetList").show('slow');
            }

        }
    };

    this.addNew = function(data){
        this.add(data,true);
    };
}

function UserListStore(){
    this.userList = [];
    this.add = function(user){
        var clonedUser = _.clone(user);
        this.userList.push(clonedUser);
        $(generateUserItemHTML([clonedUser])).hide().appendTo("#userList").show('slow');
    };
}