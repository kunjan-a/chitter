function validateLogin() {
    //alert("1");
    //alert(requiredFieldsFilled(["l_password"]));
    //alert(validateEmail(["l_email"]));
    var functionOutputs = [requiredFieldsFilled(["l_password"]), validateEmail(["l_email"])];
    //alert("2");
    var errorMsg = _.reduce(functionOutputs, function (memo, functionOutput) {
        return memo + functionOutput;
    }, '');
    //alert("3");
    showMessage(errorMsg);
    //alert("4");
    return errorMsg == '';
}

function doLogin(loginForm) {
    // alert("hi");
    clearMessageBox();
    if (validateLogin()) {
        //alert("1");
        $.post('/request/login', $(loginForm).serialize(), function (data) {
            //alert("2");
            if (data.Success == 1) {
                window.location = "/";
            } else {
                $('<div>Invalid Email/Password</div>').hide().appendTo($(messageBox)).show('slow');
            }

        });
    }
}