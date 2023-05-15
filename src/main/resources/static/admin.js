function validateForm() {
    var username = document.getElementById("inputUsername").value;
    var password = document.getElementById("inputPassword").value;
    var firstName = document.getElementById("inputFirstName").value;
    var lastName = document.getElementById("inputLastName").value;
    var role = document.getElementById("inputRole").value;

    var usernameRegex = /^[a-zA-Z0-9]+$/;
    if (username === "") {
        displayError("inputUsername", "Username is required.");
        return false;
    } else if(!usernameRegex.test(username)) {
        displayError("inputUsername", "Username can only contain letters and numbers.");
    } else {
        clearError("inputUsername");
    }

    if (password === "") {
        displayError("inputPassword", "Password is required.");
        return false;
    } else {
        clearError("inputPassword");
    }

    if (firstName === "") {
        displayError("inputFirstName", "First name is required.");
        return false;
    } else {
        clearError("inputFirstName");
    }

    if (lastName === "") {
        displayError("inputLastName", "Last name is required.");
        return false;
    } else {
        clearError("inputLastName");
    }

    if (role === "") {
        displayError("inputRole", "Role is required.");
        return false;
    } else {
        clearError("inputRole");
    }

    return true;
}