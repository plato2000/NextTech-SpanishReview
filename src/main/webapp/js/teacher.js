// Your Client ID can be retrieved from your project in the Google
// Developer Console, https://console.developers.google.com
var CLIENT_ID = "453755821502-1k95kijujmdh4g16opd1qpaqn6miboro.apps.googleusercontent.com";

//var SCOPES = ["https://www.googleapis.com/auth/classroom.courses.readonly", "profile", "https://www.googleapis.com/auth/classroom.courses.readonly", "https://www.googleapis.com/auth/classroom.rosters.readonly", "https://www.googleapis.com/auth/classroom.profile.emails"];
var SCOPES = [ "profile"];

var currentStudents = [];

var inClass = [];

var currentCourse = {id: "", name: ""};

var teacher;

/**
 * Check if current user has authorized this application.
 */
function checkAuth() {
    gapi.auth.authorize(
        {
            "client_id": CLIENT_ID,
            "scope": SCOPES.join(" "),
            "immediate": true
        }, handleAuthResult);
}

/**
 * Handle response from authorization server.
 *
 * @param {Object} authResult Authorization result.
 */
function handleAuthResult(authResult) {
    var signin = document.getElementById("sign-in");
    console.log(authResult);
    if (authResult && !authResult.error) {
        // Hide auth UI, then load client library.
        signin.style.display = "none";
        loadClassroomApi();
    } else {
        // Show auth UI, allowing the user to initiate authorization by
        // clicking authorize button.
        signin.style.display = "inline";
    }
}

/**
 * Initiate auth flow in response to user clicking authorize button.
 *
 */
function handleAuthClick() {
    gapi.auth.authorize(
        {client_id: CLIENT_ID, scope: SCOPES.join(" "), immediate: false},
        handleAuthResult);
    return false;
}

/**
 * Load Classroom API client library.
 */
function loadClassroomApi() {
    gapi.client.load("classroom", "v1", listCourses);
}


function listCourses(id) {
    var request = gapi.client.classroom.courses.list({
    });

    request.execute(function(resp) {
        var courses = resp.courses;
        if (courses.length > 0) {
            for (i = 0; i < courses.length; i++) {
                var course = courses[i];
                if(i == 0) {
                    getTeacher(course.id);
                }
                appendDropdown(course.name, course.id)
            }
        }

    });
}

function getTeacher(id) {
    var request = gapi.client.request({
        path: "https://classroom.googleapis.com/v1/courses/" + id + "/teachers",
        params: {

        }
    });
    request.execute(function(resp) {
        var teachers = resp.teachers;
        teacher = teachers[0];
    });
}

function appendDropdown(message, id) {
    $("#dropdown").append("<li><a href=\"javascript:selectCourse(" + id + ", '" + message + "')\">" + message + "</a></li>");
}

function appendStudent(name, id, status) {
    console.log("name: " + name + " id: " + id + " status: " + status);
    $("#students").find("tbody")
        .append($("<tr>")
            .append($("<td>")
                .text(name)
            ).append($("<td>")
                .text(id == "" ? "Could not find in .MER file" : status)
                .attr("id", id == "" ? name : id)
                .attr("class", id == "" ? "no" : "id")
            )
        );
}

function signOut() {
    var auth = gapi.auth.getAuthInstance();
    auth.signOut().then(function () {
        console.log("User signed out.");
    });
}

function listStudents(id) {
    var request = gapi.client.classroom.courses.students.list({
        courseId: id
    });
    request.execute(function(resp) {
        var students = resp.students;
        inClass = students;
        for(i = 0; i < students.length; i++) {
            var student = students[i];
            console.log(student);
            appendStudent(student.profile.name.fullName, getIdFromName(student.profile.name.fullName), "Loading...");
        }
        refreshInfoFromServer();
    });
}

function selectCourse(id, name) {
    currentCourse.id = id;
    currentCourse.name = name;
    listStudents(id);
    $("#deadline-span").show();
    $("#requirements-span").show();
    $("#class-name").text(name);
    $("#button-bar").show();
}

function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object
    if(typeof files !== "undefined" && files[0].name.split(".")[files[0].name.split(".").length - 1].toLowerCase() == "mer") {
        var reader = new FileReader();
        reader.onload = function() {
            readCSV(this.result);
        };
        reader.readAsText(files[0]);
    } else {
        alert('Please select a .MER file.');
    }

}

function readCSV(csv) {
    console.log(csv);
    $.csv.toObjects(csv, {separator: ",", delimiter: "\""}, function(err, objects) {
        if(typeof(Storage) !== "undefined") {
            for(var i = 0; i < objects.length; i++) {
                localStorage.setItem(objects[i]["FIRST"] + " " + objects[i]["LAST"], objects[i]["ID"]);
            }
            localStorage.setItem("time", new Date().getTime());
        } else {
            for(var i = 0; i < objects.length; i++) {
                var obj = {}
                var key = objects[i]["FIRST"] + " " + objects[i]["LAST"];
                obj[key] = objects[i]["ID"];
                currentStudents.push(obj);
            }
        }
        $("#mer-file-label").text(".MER file selected just now");
        updateIds();
    });
}

function fileRead() {
    if(typeof(Storage) !== "undefined") {
        if(localStorage.getItem("time") != null && localStorage.getItem("time") != "") {
            return true;
        } else {
            return false;
        }
    } else {
        if(currentStudents.length > 0) {
            return true;
        } else {
            return false;
        }
    }
}

function getIdFromName(name) {
    if(fileRead()) {
        if(typeof(Storage) !== "undefined") {
            if(localStorage.getItem(name) != null) {
                return localStorage.getItem(name);
            } else {
                return "";
            }
        } else {
            for(var i = 0; i < currentStudents.length; i++) {
                if(currentStudents[i].name == name) {
                    return currentStudents[i].id;
                }
            }
            return "";
        }
    } else {
        return "";
    }
}

function updateIds() {
    var elements = document.getElementsByClassName("no");
    for(var i = 0; i < elements.length; i++) {
        elements[i].id = getIdFromName(elements[i].id);
        elements[i].setAttribute("class", "id");
    }
}

function refreshInfoFromServer() {
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids
    };
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    $.ajax({
        type: "POST",
        url: location.origin + "/teacher/getInfo",
        data: $.toJSON(dictToSerialize),
        success: function(data) {
            console.log(data);
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
            elements = document.getElementsByClassName("id");
            for(var i = 0; i < elements.length; i++) {
                elements[i].innerHTML = data.results[elements[i].id];
            }
        },
        error: function(data, error) {
            console.log(data);
            console.log(error);
        }
    });
}

function updateRequirements() {
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids,
        total: $("#total").val(),
        blank: $("#blank").val(),
        scoredOver: $("#scoredOver").val(),
        deadline: $("#deadline").datepicker("getDate").getTime()
    };
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    $.ajax({
        type: "POST",
        url: location.origin + "/teacher/setInfo",
        data: $.toJSON(dictToSerialize),
        success: function(data) {
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
            refreshInfoFromServer();
        }
    });
}

function resetUsers() {
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids,
        total: $("#total").val(),
        blank: $("#blank").val(),
        scoredOver: $("#scoredOver").val(),
        deadline: $("#deadline").datepicker("getDate").getTime()
    };
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    $.ajax({
        type: "POST",
        url: location.origin + "/teacher/resetUsers",
        data: $.toJSON(dictToSerialize),
        success: function(data) {
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
        }
    });
}

$(function() {
    if (window.File && window.FileReader && window.FileList) {
        // Great success! All the File APIs are supported.
    } else {
        alert('Please use Google Chrome or update your browser, as this site requires newer features not available in this version of your browser.');
    }
    document.getElementById('file-picker').addEventListener('change', handleFileSelect, false);
    if(typeof(Storage) !== "undefined") {
       if(localStorage.getItem("time") !== null && localStorage.getItem("time") != "") {
            $("#mer-file-label").text(".MER file loaded from " + moment(Number(localStorage.getItem("time"))).fromNow())
       }
    }
});

