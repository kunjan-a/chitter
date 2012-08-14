function TweetStore(){
    this.tweets = [];
    this.add = function(tweet){
        var clonedTweet = _.clone(tweet);
        this.tweets.push(clonedTweet);
        $(generateTweetHTML([clonedTweet])).hide().appendTo("#tweetList").show('slow');
    };
    this.render =function(){
        $(generateTweetHTML(this.tweets)).hide().appendTo("#tweetList").show('slow');
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