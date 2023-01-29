function checkPasswordMatch(fieldConfirmPassword) {
    if (fieldConfirmPassword.value != $("#password").val()) {
        fieldConfirmPassword.setCustomValidity("Hesla se neshodují.");
    } else {
        fieldConfirmPassword.setCustomValidity("");
    }
}