// Function to display error message for a specific field
function displayError(fieldId, errorMessage) {
    var field = document.getElementById(fieldId); // Getting the id base on what field is being checked
    var errorSpanId = fieldId + "-error"; // Creating a unique id for the error message
    var errorSpan = document.getElementById(errorSpanId); // Getting the error message span

    // Create and display the error message
    if (!errorSpan) { // If the error message span does not exist, create it
        errorSpan = document.createElement("span");
        errorSpan.id = errorSpanId;
        errorSpan.classList.add("error-message");
        field.parentNode.appendChild(errorSpan);
    }
    errorSpan.innerText = errorMessage;
}

// Function to clear error message for a specific field
function clearError(fieldId) {
    var errorSpanId = fieldId + "-error"; // Creating a unique id for the error message
    var errorSpan = document.getElementById(errorSpanId); // Getting the error message span
    if (errorSpan) {
        errorSpan.parentNode.removeChild(errorSpan); // Remove the error message span
    }
}