function genericValidator(){
    this.msg = "message to display";
    this.test = function(inputElement){return false;};//return true if validation check fails
    this.validate =  function() {
        var msg = this.msg;
        return _.chain(arguments)//variable arguments indicating ids of the elements to be tested
            .map(function (inputElementId) {return $('#' + inputElementId);})
            .filter(this.test)
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


function validateLogin() {
    var functionOutputs = [requiredFieldsValidator.validate("l_password"), emailValidator.validate("l_email")];
    var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
        return memo + functionOutput;
    }, '');
    showMessage(errorMsg);
    return errorMsg == '';
}

function doLogin(loginForm) {
    if (validateLogin()) {
        $.post('/request/login', $(loginForm).serialize(), function (data) {
            if (data.Success == 1) {
                window.location.reload();
            } else {
                showMessage('Invalid Email/Password');
            }

        });
    }
}

function doLogout(){
    $.get('/logout', {}, function (data) {
        window.location.reload();
    });
}