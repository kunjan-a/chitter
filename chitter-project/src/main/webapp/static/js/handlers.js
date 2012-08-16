function clearAllContent(){
    $('#banner').html('');
    $('#leftPane').html('');
    clearLists();
}

function clearLists(){
    $('#tweetList').html('');
    $('#userList').html('');
}

function userHandler(hash){
    var userid = hash.replace(/users\//,'');
    userid = userid.replace(/\/.*$/,'');
    if(/following/.test(hash)){
        showFollowing(userid,userid != currentUserId);
    }else if(/followers/.test(hash)){
        showFollowers(userid,userid != currentUserId);
    }else{
        showUserTweets(userid,userid != currentUserId);
    }
    currentUserId = userid;
}

function showUserTweets(userid,flag){
    if(flag){
        clearAllContent();
    }else{
        clearLists();
    }
    $.post('/rest/'+userid+'/tweets/', {}, function (data) {
        console.log(data);
        data.user.follows = data.follows;
        if(flag){
            showProfileBanner(data.user);
            showLeftPane();
        }
        tweetStore = new TweetStore();
        tweetStore.add(data);
        console.log(tweetStore);
    });
}

function showFollowing(userid,flag){
    if(flag){
        clearAllContent();
    }else{
        clearLists();
    }
    $.post('/rest/'+userid+'/following/', {}, function (data) {
        console.log(data);
        if(flag){
            showProfileBanner(data.user);
            showLeftPane();
        }
        followingStore = new UserListStore();
        _.map(data.response,function(o){
            o["follows"] = jsonPath(data, "$.usersFollowed[?(@.celebrityId == "+o.id+")]") == false?false:true;
        });
        console.log(data);
        _.each(data.response,function(user){
            followingStore.add(user);
        });
        console.log(followingStore);
    });
}

function showFollowers(userid,flag){
    if(flag){
        clearAllContent();
    }else{
        clearLists();
    }
    $.post('/rest/'+userid+'/followers/', {}, function (data) {
        console.log(data);
        data.user.follows = data.follows;
        if(flag){
            showProfileBanner(data.user);
            showLeftPane();
        }
        followerStore = new UserListStore();
        _.map(data.response,function(o){
            o["follows"] = jsonPath(data, "$.usersFollowed[?(@.celebrityId == "+o.id+")]") == false?false:true;
        });
        console.log(data);
        _.each(data.response,function(user){
            followerStore.add(user);
        });
        console.log(followerStore);
    });
}


function editProfilePageHandler(){
    clearAllContent();
    alert("Edit Profile Handler Called");
}

function homePageHandler(){
    if(loggedIn){
        clearAllContent();
        $.post('/rest/'+loggedInUserId+'/feeds/', {}, function (data) {
            console.log(data);
            showHomePageBanner();
            showLeftPane();
            tweetStore = new TweetStore();
            tweetStore.add(data);
            console.log(tweetStore);
        });
    }
}

function errorPageHandler(){
    clearAllContent();
    $('#myModal').modal('show');
}

function hashChangeHandler(){
    var hash = window.location.hash;
    var tests = [
        {
            name : 'User Profile',
            test : function(str){return /^users\/[0-9]+(\/((following\/{0,1}|followers\/{0,1}){0,1})){0,1}$/.test(str)},
            call : userHandler
        },
        {
            name : 'Edit Profile',
            test : function(str){return /^edit\/profile\/{0,1}$/.test(str)},
            call: editProfilePageHandler
        },
        {
            name : 'Home Page',
            test : function(str){return str == ''},
            call : homePageHandler
        },
        {
            name : 'Error',
            test : function(str){return true},
            call : errorPageHandler
        }
    ];

    hash = hash.replace(/^#!\//,"");
    _.chain(tests)
        .filter(function(Test){return Test.test(hash)})
        .first()
        .value()
        .call(hash);
}