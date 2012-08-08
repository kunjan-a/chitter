var messageBox = "#MsgBox";

var tweetTemplateText = '<div class="tweetItem">' +
    '<div class="tweetedBy"><a href="/user/<%= tweetUserId %>"><%= tweetUserId %></a></div>'+
    '<div class="tweetContent"><%= tweetText %></div>'+
    '</div>';

var compiledTweetTemplate = _.template(tweetTemplateText);

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

var followerTemplate = _.template(followerTemplateText);

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
        $(msg).hide().appendTo($(messageBox)).slideDown();
}

