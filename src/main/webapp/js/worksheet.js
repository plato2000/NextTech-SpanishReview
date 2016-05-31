/**
 * Created by plato2000 on 4/21/16.
 */
$(function() {
    // Make worksheet/wordbank full size
    $(".row").height("100%");
    // Initialize drag and drop library
    drake = dragula({
        isContainer: function (el) {
            return el.classList.contains('droppable');
        },
        // If it is dragged to a place where it can't be dragged to, revert
        revertOnSpill: true,
        accepts: function (el, target, source, sibling) {
            // If it's not full or it is the wordbank, it can be dragged there
            return !target.classList.contains('full') || target.id == "wordbank-container"; // elements can be dropped in any of the `containers` by default
        }
    });
    // On drop in place: make it full
    drake.on("drop", function(e1, target, source, sibling) {
        source.className = source.className.replace(/\bfull\b/,'');
        target.className += " full";
    });
    // Remove ? characters that get placed there by a bug in Google App Engine (should no longer be necessary, there just in case)
    // These are now removed clientside
    for(var i = 0; i < document.getElementsByClassName("cell").length; i++) {
        document.getElementsByClassName("cell")[i].innerHTML =  document.getElementsByClassName("cell")[i].innerHTML.replace(/�/g, "");
    }
    for(var i = 0; i < document.getElementsByClassName("well").length; i++) {
        document.getElementsByClassName("well")[i].innerHTML =  document.getElementsByClassName("well")[i].innerHTML.replace(/�/g, "");
    }

});

// Handles submission of worksheet to backend
function submitWorksheet() {
    // Converts table to 2D array to send to backend
    var array = get2DArray("worksheet-table");
    // Displays modal if incomplete, else sends an HTTP POST request to /grader
    if(!incompleteForm()) {
        // JS Object containing information to send
        var dictToSerialize = {
            email: userName,
            ws: array
        };
        // If browser does not support location.origin, set it
        if (!location.origin)
            location.origin = location.protocol + "//" + location.host;
        // Convert object to JSON string
        console.log($.toJSON(dictToSerialize));
        // Send POST request to /grader
        $.ajax({
            type: "POST",
            url: location.origin + "/grader",
            data: $.toJSON(dictToSerialize),
            success: function(data) {
                // If it worked, redirect to / with score in URL to be read
                if(data["success"] == "true") {
                    window.location.replace(location.origin + "/?score=" + data["score"]);
                } else {
                    // Otherwise, log the error and show submission failure
                    console.log(data);
                    $("#submission-failure").modal("show");
                }
            }
        });
    }
}

// Alerts when submission is tried with an incomplete form
function incompleteForm() {
    // If the wordbank doesn't have anything left, it is a complete form
    if($("#wordbank-container").find("div.well").length > 0) {
        $("#incomplete-worksheet").modal("show");
        return true;
    }
    return false;
}

// Converts table to array
function get2DArray(id) {
    // Create array to push to
    var array = [];
    var table = document.getElementById(id);
    for (var i = 0, row; row = table.rows[i]; i++) {
        array.push([]);
        for (var j = 0, cell; cell = row.cells[j]; j++) {
            //iterate through columns
            //columns would be accessed using the "cell" variable assigned in the for loop
            //console.log($(cell).find("div"));
            // If there is a div in the cell, it is a blank
            if($(cell).find("div").length > 0) {
                //console.log($(cell).find("div"));
                // If there is more than one div in the cell, there is more than one blank
                if($(cell).find("div").length > 1) {
                    // Add the ID of the first blank
                    array[i].push($(cell).find("div")[0].id);
                    for (var k = 1; k < $(cell).find("div").length; k++) {
                        // Add the rest of the IDs separated by commas
                        array[i][j] += "," + $(cell).find("div")[k].id;
                    }
                } else {
                    // Otherwise, add the ID for the one blank
                    array[i].push($(cell).find("div")[0].id);
                }
            } else {
                // If it was a cell without a blank:
                if(cell.id != null) {
                    // Add the ID
                    array[i].push(cell.id);
                } else {
                    // If something else happened (maybe unfilled blank somehow bypassed) put an empty string
                    array[i].push("");
                }
            }
        }
    }

    return array;
}

$(function() {
    $("#info-popover").popover();
});


