function validateLogin() {
    var functionOutputs = [requiredFieldsValidator.validate("l_password"), emailValidator.validate("l_email")];
    var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
        return memo + functionOutput;
    }, '');
    showMessage(errorMsg);
    return errorMsg == '';
}

function doLogin(loginForm) {
    clearMessageBox();
    if (validateLogin()) {
        $.post('/request/login', $(loginForm).serialize(), function (data) {
            if (data.Success == 1) {
                window.location = window.location.pathname;
            } else {
                $('<div>Invalid Email/Password</div>').hide().appendTo($(messageBox)).show('slow');
            }

        });
    }
}