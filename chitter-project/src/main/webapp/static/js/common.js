var messageBox = "#MsgBox";

var tweetTemplateText = '<div class="tweetItem">' +
    '<div class="tweetedBy"><a href="/users/<%= id %>"><%= name %></a></div>'+
    '<div class="tweetContent"><%= tweet %></div>'+
    '</div>';

var compiledTweetTemplate = _.template(tweetTemplateText);

function generateTweetHTML(list){
    return _.chain(list)
        .map(compiledTweetTemplate)
        .reduce(function (memo, el) {return memo + el;}, '')
        .value();
}


function genericValidator() {
    var msg = this.msg;
    return _.chain(arguments)//variable arguments indicating ids of the elements to be tested
            .map(function (inputElementId) {return $('#' + inputElementId);})
            .filter(this.test)
            .map(function(inputElement){inputElement.addClass("errorInput");return inputElement;})
            .reduce(function (memo, inputElement) {return memo + '<div>' + inputElement.attr('pretty_name') + ' ' + msg + '</div>';}, '')
            .value();
}

function clearMessageBox() {
    $(messageBox).html('');
}

function clearErrorInputs() {
    $(":input").each(function () {
        if ($(this).hasClass("errorInput")) {
            $(this).removeClass("errorInput");
        }
    });
}

var requireFieldsTest = {
    test : function (inputElement) {
        return inputElement.val() == '';
    },
    "msg" : 'cannot be blank'
};

requiredFieldsFilled = _.bind(genericValidator,requireFieldsTest);

var validEmailTest = {
    test : function (inputElement) {
        return !/^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/.test(inputElement.val());
    },
    "msg" : 'is not a valid one'
};

var validateEmail = _.bind(genericValidator,validEmailTest);

function showMessage(msg) {
    if (msg != '')
        $(msg).hide().appendTo($(messageBox)).slideDown();
}

