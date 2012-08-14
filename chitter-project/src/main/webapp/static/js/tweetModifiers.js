function getUsers(data){
    var ret = new Object();
    _.each(data.users,function(o){
        ret[o["id"]] = o["name"];
    });
    ret[data.user["id"]] = data.user["name"];
    return ret;
}

function addNames(data){
    var users = getUsers(data);
    _.each(data.response,function(tweet){
        tweet["name"] = users[tweet["tweetUserId"]];
    });
}

function addTimes(data){

    var tweets = data.response;

    _.each(tweets,function(tweet){
        var a = new Date(tweet['feedTime']);
        var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
        var year = a.getFullYear();
        var month = months[a.getMonth()];
        var date = a.getDate();
        var hour = a.getHours();
        var min = a.getMinutes();
        var sec = a.getSeconds();
        var time = date+','+month+' '+year+' '+hour+':'+min+':'+sec ;

        tweet['time'] = time;
    });
}

function getRetweets(data){
    var ret = new Object();
    _.each(data.retweeted,function(id){
        ret[id] = true;
    });
    return ret;
}

function addRetweetedFlag(data){
    var tweets = data.response;
    var reTweets = getRetweets(data);
    console.log(reTweets);
    _.each(tweets,function(tweet){
        if(tweet["tweetUserId"] == loggedInUserId){
            tweet["retweeted"] = false;
        }else{
            //alert(reTweets[tweet["tweetId"]]);
            //tweet["retweeted"] = true;
            if(reTweets[tweet["tweetId"]]){
                tweet["retweeted"] = true;
            }else{
                tweet["retweeted"] = false;
            }
        }
    });

}

function processTweets(data){
    addNames(data);
    addTimes(data);
    addRetweetedFlag(data);
}