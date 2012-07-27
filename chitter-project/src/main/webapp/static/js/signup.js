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


function validateSignUp() {
    var functionOutputs = [requiredFieldsFilled(["s_name", "s_password", "s_password2"]), validateEmail(["s_email"]), Matches("s_password", "s_password2")];
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