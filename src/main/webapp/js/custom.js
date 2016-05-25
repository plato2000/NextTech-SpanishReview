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
            //console.log((target.innerHTML.match(/<div/g) || []).length);
            //(target.innerHTML.match(/is/g) || []).length
            // If it's not full or it is the wordbank, it can be dragged there
            return !target.classList.contains('full') || target.id == "wordbank-container"; // elements can be dropped in any of the `containers` by default
        }
    });
    // On drag from place: make it not full
    drake.on("drag", function(e1, source) {
        source.className = source.className.replace(/\bfull\b/,'');
        //if(source.classList.contains('well')) {
        //    target.style.padding = "";
        //}
    });
    // On drop in place: make it full
    drake.on("drop", function(e1, target, source, sibling) {
        //console.log(target);
        target.className += " full";
        //if(target.classList.contains('well')) {
        //    target.style.padding = "2px 2px 2px 2px";
        //}
    });
    // Remove ? characters that get placed there by a bug in Google App Engine
    for(var i = 0; i < document.getElementsByClassName("cell").length; i++) {
        document.getElementsByClassName("cell")[i].innerHTML =  document.getElementsByClassName("cell")[i].innerHTML.replace(/�/g, "");
    }
    for(var i = 0; i < document.getElementsByClassName("well").length; i++) {
        document.getElementsByClassName("well")[i].innerHTML =  document.getElementsByClassName("well")[i].innerHTML.replace(/�/g, "");
    }

    //document.documentElement.innerHTML = document.documentElement.innerHTML.replace(/�/g, "");
});

// Handles submission of worksheet to backend
function submitWorksheet() {
    var csv = getGradableCSV("worksheet-table")
    if(csv == "") {
        incompleteForm();
        return;
    } else {
        console.log("csv: " + csv);
        // TODO: submit logic
    }
}

// Alerts when submission is tried with an incomplete form
function incompleteForm() {
    // TODO: real stuff for error
    return;
}

// Converts table to CSV
function getGradableCSV(id) {
    var csv = "";
    var table = document.getElementById(id);
    for (var i = 0, row; row = table.rows[i]; i++) {
        for (var j = 0, col; col = row.cells[j]; j++) {
            //iterate through columns
            //columns would be accessed using the "col" variable assigned in the for loop
            if(col.hasChildNodes()) {
                if(col.childNodes[0].hasChildNodes()) {
                    csv += col.childNodes[0][0].innerHTML + ",";
                } else {
                    return "";
                }
            } else {
                if(col.innerText != "") {
                    csv += col.innerText + ",";
                } else {
                    return "";
                }
            }
        }
        csv += "\n"
    }
    return csv;
}


