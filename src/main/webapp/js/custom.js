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
});


