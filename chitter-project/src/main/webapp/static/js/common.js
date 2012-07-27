var messageBox = "#MsgBox";

function genericValidator(list, test, msg) {
    return _.chain(list)
        .map(function (inputElementId) {
            return $('#' + inputElementId);
        })
        .filter(test)
        .reduce(function (memo, inputElement) {
            inputElement.addClass("errorInput");
            return memo + '<div>' + inputElement.attr('pretty_name') + ' ' + msg + '</div>';
        }, '')
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

function requiredFieldsFilled(required) {
    //alert("1");
    var test = function (inputElement) {
        return inputElement.val() == '';
    };
    var msg = 'cannot be blank';
    return genericValidator(required, test, msg);
}

function validateEmail(emails) {
    var msg = 'is not a valid one';
    var test = function (inputElement) {
        return !/^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/.test(inputElement.val());
    };
    return genericValidator(emails, test, msg);
}

function showMessage(msg) {
    if (msg != '')
        $(msg).hide().appendTo($(messageBox)).slideDown();
}

