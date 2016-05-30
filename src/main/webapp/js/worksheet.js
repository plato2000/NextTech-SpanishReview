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
    //// On drag from place: make it not full
    //drake.on("drag", function(e1, source) {
    //    source.className = source.className.replace(/\bfull\b/,'');
    //    //if(source.classList.contains('well')) {
    //    //    target.style.padding = "";
    //    //}
    //});
    // On drop in place: make it full
    drake.on("drop", function(e1, target, source, sibling) {
        //console.log(target);
        source.className = source.className.replace(/\bfull\b/,'');
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
    var array = get2DArray("worksheet-table");
    if(incompleteForm()) {

    } else {
        //console.log("array: " + array);
        var dictToSerialize = {
            email: userName,
            ws: array
        };
        if (!location.origin)
            location.origin = location.protocol + "//" + location.host;
        console.log($.toJSON(dictToSerialize));
        $.ajax({
            type: "POST",
            url: location.origin + "/grader",
            data: $.toJSON(dictToSerialize),
            success: function(data) {
                if(data["success"] == "true") {
                    window.location.href = location.origin + "/?score=" + data["score"];
                } else {
                    console.log(data);
                    $("#submission-failure").modal("show");
                }
            }
        });
    }
}

// Alerts when submission is tried with an incomplete form
function incompleteForm() {
    if($("#wordbank-container").find("div.well").length > 0) {
        $("#incomplete-worksheet").modal("show");
        return true;
    }
    return false;
}

// Converts table to array
function get2DArray(id) {
    var array = [];
    var table = document.getElementById(id);
    for (var i = 0, row; row = table.rows[i]; i++) {
        array.push([]);
        for (var j = 0, cell; cell = row.cells[j]; j++) {
            //iterate through columns
            //columns would be accessed using the "cell" variable assigned in the for loop
            //console.log($(cell).find("div"));
            if($(cell).find("div").length > 0) {
                //console.log($(cell).find("div"));
                if($(cell).find("div").length > 1) {
                    array[i].push($(cell).find("div")[0].id);
                    for (var k = 1; k < $(cell).find("div").length; k++) {
                        array[i][j] += "," + $(cell).find("div")[k].id;
                    }
                } else {
                    array[i].push($(cell).find("div")[0].id);
                }
            } else {
                if(cell.id != null) {
                    array[i].push(cell.id);
                } else {
                    array[i].push("");
                }
            }
        }
    }

    return array;
}


