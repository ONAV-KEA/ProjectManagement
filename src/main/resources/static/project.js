$(document).ready(function () {
    // Collapse all subtasks on page load
    $('.subtasks').hide();

    $('.toggle-subtasks').click(function () {
        const subtasks = $(this).closest('tr').find('.subtasks');
        subtasks.slideToggle('slow');
        $(this).find('i').toggleClass('fa-arrow-down fa-arrow-up');
    });
});

var statusElements = document.getElementsByClassName('status');

// Mapping object to transform status values
var statusMapping = {
    'todo': 'To Do',
    'in_progress': 'In Progress',
    'completed': 'Completed'
};

// Loop through all elements with the 'status' class
for (var i = 0; i < statusElements.length; i++) {
    var element = statusElements[i];
    var originalStatus = element.textContent.trim();

    // Check if the original status value exists in the mapping object
    if (originalStatus in statusMapping) {
        // Use the transformed status value from the mapping object
        var transformedStatus = statusMapping[originalStatus];
        element.innerText = transformedStatus;
    }
}
