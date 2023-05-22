function validateForm() {
    var projectName = document.getElementById("inputName").value; // Getting name from input field
    var startDate = document.getElementById("inputStartDate").value; // Getting start date from input field
    var endDate = document.getElementById("inputEndDate").value; // Getting end date from input field

    // Checking that the name of the project is not empty
    if (projectName.trim() === "") {
        displayError("inputName", "Name is required");
        return false; // Prevent form submission
    } else {
        clearError("inputName");
    }

    // Checking that the start date is not a newer date than the end date
    if (startDate > endDate) {
        displayError("inputStartDate", "Start date cannot be newer than end date");
        return false; // Prevent form submission
    } else {
        clearError("inputStartDate");
    }

    // Checking that the end date is not null
    if(endDate === "") {
        displayError("inputEndDate", "End date is required");
        return false; // Prevent form submission
    }

    return true; // Allow form submission
}