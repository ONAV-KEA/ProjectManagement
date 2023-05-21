function validateForm() {
    var taskName = document.getElementById("inputName").value; // Getting name from input field
    var startDate = document.getElementById("inputStartDate").value; // Getting start date from input field
    var endDate = document.getElementById("inputEndDate").value; // Getting end date from input field
    var projectEndDate = document.getElementById("projectEndDate").value; // Getting project end date from input field

    // Checking that the name of the project is not empty
    if (taskName.trim() === "") {
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

    // Checking that the end date is not a newer date than the project end date
    if (endDate > projectEndDate) {
        displayError("inputEndDate", "End date cannot be newer than project end date, project end date is " + projectEndDate);
        return false; // Prevent form submission
    }

    return true; // Allow form submission
}
