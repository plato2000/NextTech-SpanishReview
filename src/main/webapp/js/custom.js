/**
 * Created by plato2000 on 4/21/16.
 */
$(function() {
    $(".row").height("100%");
    drake = dragula({
        isContainer: function (el) {
            return el.classList.contains('droppable');
        },
        revertOnSpill: true,
        accepts: function (el, target, source, sibling) {
            //console.log((target.innerHTML.match(/<div/g) || []).length);
            //(target.innerHTML.match(/is/g) || []).length
            return !target.classList.contains('full') || target.id == "wordbank-container"; // elements can be dropped in any of the `containers` by default
        }
    });
    drake.on("drag", function(e1, source) {
        source.className = source.className.replace(/\bfull\b/,'');
        //if(source.classList.contains('well')) {
        //    target.style.padding = "";
        //}
    });
    drake.on("drop", function(e1, target, source, sibling) {
        console.log(target);
        target.className += " full";
        //if(target.classList.contains('well')) {
        //    target.style.padding = "2px 2px 2px 2px";
        //}
    });
    document.documentElement.innerHTML = document.documentElement.innerHTML.replace(/�/g, "");
});

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


function incompleteForm() {
    // TODO: real stuff for error
    return;
}

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


