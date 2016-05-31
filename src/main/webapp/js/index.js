/**
 * Created by plato2000 on 5/27/16.
 */

// Redirect user to blank sheet page
function blankSheet() {
    window.location.href = location.origin + "/worksheet?type=blank";
}

// Redirect user to normal sheet page
function normalSheet() {
    window.location.href = location.origin + "/worksheet?type=regular";
}

$(function() {
    if($("#deadline-rel") != null) {
        $("#deadline-rel").text(" (" + moment(millisDeadline).fromNow() + ")");
    }
});
