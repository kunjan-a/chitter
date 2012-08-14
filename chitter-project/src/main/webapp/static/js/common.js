var messageBox = "#MsgBox";

function reTweet(tweetId){
    $.post("/action/retweet", { "id":tweetId }, function (data) {
        console.log(data);
    });
}

var compiledTweetTemplate = _.template($('#tweetItemTemplate').html());

function generateTweetHTML(list){
    return _.chain(list)
        .map(compiledTweetTemplate)
        .reduce(function (memo, el) {return memo + el;}, '')
        .value();
}

var followerTemplateText = '<li class="followerItem">' +
    '<div class="followerName"><a href="/user/<%= id %>"><%= name %></a>'+
    '<button id="followBtn" class="followBtn" userid="<%= id %>" follows="<%= follows %>"><% if(id == loggedin_user){ %> Edit Profile <% }else if(follows){ %>Following <% }else{ %>Follow <% } %></button></div>'+
'</li>';

var followerTemplate = _.template($('#userItemTemplate').html());

function generateFollowerHTML(list){
    return _.chain(list)
        .map(followerTemplate)
        .reduce(function (memo, el) {return memo + el;}, '')
        .value();
}

function clearMessageBox() {
    $(messageBox).html('');
}





function genericValidator(){
    this.msg = "message to display";
    this.test = function(inputElement){return false;};//return true if validation check fails
    this.validate =  function() {
        var msg = this.msg;
        return _.chain(arguments)//variable arguments indicating ids of the elements to be tested
            .map(function (inputElementId) {return $('#' + inputElementId);})
            .filter(this.test)
            .map(function(inputElement){inputElement.addClass("errorInput");return inputElement;})
            .reduce(function (memo, inputElement) {return memo + '<div>' + inputElement.attr('pretty_name') + ' ' + msg + '</div>';}, '')
            .value();
    };
}

requiredFieldsValidator = new genericValidator();
requiredFieldsValidator.msg = "cannot be blank";
requiredFieldsValidator.test = function (inputElement) {
    return inputElement.val() == '';
};

emailValidator = new genericValidator();
emailValidator.msg = "is not a valid one";
emailValidator.test = function (inputElement) {
    return !/^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/.test(inputElement.val());
};

function clearErrorInputs() {
    $(":input").each(function () {
        if ($(this).hasClass("errorInput")) {
            $(this).removeClass("errorInput");
        }
    });
}

function showMessage(msg) {
    if (msg != '')
        $('<div>'+msg+'</div>').hide().appendTo($(messageBox)).slideDown();
}

function showMessageAndClear(msg) {
    showMessage(msg);
    window.setTimeout(function(){$(messageBox).html('')},5000);
}


function Matches(a, b) {
    var el1 = $("#" + a);
    var el2 = $("#" + b);
    if (el1.val() != el2.val()) {
        el1.addClass("errorInput");
        el2.addClass("errorInput");
        return '<div>' + el1.attr('pretty_name') + ' and ' + el2.attr('pretty_name') + ' should match</div>';
    }
    return '';
}

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
        if(tweet["tweetUserId"] == loggedin_user){
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