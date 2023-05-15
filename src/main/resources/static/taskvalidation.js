function validateForm() {
    var taskName = document.getElementById("inputName").value; // Getting name from input field
    var startDate = document.getElementById("inputStartDate").value; // Getting start date from input field
    var endDate = document.getElementById("inputEndDate").value; // Getting end date from input field

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

    return true; // Allow form submission
}
