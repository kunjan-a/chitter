


function validateSignUp() {
    var functionOutputs = [requiredFieldsValidator.validate("s_name", "s_password", "s_password2"), emailValidator.validate(["s_email"]), Matches("s_password", "s_password2")];
    var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
        return memo + functionOutput;
    }, '');
    showMessage(errorMsg);
    return errorMsg == '';
}

function doRegister(signUpForm) {
    clearMessageBox();
    if (validateSignUp()) {
        $.post("/request/emailExists", { "email":$('#s_email').val() }, function (data) {
            console.log(data);
            if (data.Exists == 0) {
                $.post('/request/register', $(signUpForm).serialize(), function (data) {
                    showMessage('<div>' + data.msg + '</div>');
                });
            } else {
                showMessage('<div>An account already exists for this Email Address</div>');

            }

        });


    }
}