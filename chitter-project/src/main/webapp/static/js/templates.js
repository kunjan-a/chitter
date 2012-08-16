var compiledTweetTemplate = _.template($('#tweetItemTemplateNew').html());

function generateTweetHTML(list){
    return _.chain(list)
        .map(compiledTweetTemplate)
        .reduce(function (memo, el) {return memo + el;}, '')
        .value();
}

var compiledProfileBanner = _.template($('#profileBanner').html());

function showProfileBanner(user){
    $(compiledProfileBanner(user)).hide().appendTo('#banner').slideDown('slow');
    $('#followBtn').click(followButtonClick);
    $('#followBtn').mouseover(followButtonMouseOver);
    $('#followBtn').mouseout(followButtonMouseOut);
}

var compiledHomeBanner = _.template($('#homePageBanner').html());

function showHomePageBanner(){
    $(compiledHomeBanner({})).hide().appendTo('#banner').slideDown('slow');
}

var compiledLeftPane = _.template($('#leftPaneContent').html());

function showLeftPane(){
    $(compiledLeftPane({})).hide().appendTo('#leftPane').show('slow');
}

var CompiledUserItemTemplate = _.template($('#userItemTemplateNew').html());

function generateUserItemHTML(list){
    return _.chain(list)
        .map(CompiledUserItemTemplate)
        .reduce(function (memo, el) {return memo + el;}, '')
        .value();
}