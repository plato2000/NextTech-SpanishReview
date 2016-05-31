// Google Developer Client ID, from the Developer console
var CLIENT_ID = "453755821502-1k95kijujmdh4g16opd1qpaqn6miboro.apps.googleusercontent.com";

// The list of OAuth 2.0 scopes to request
var SCOPES = ["https://www.googleapis.com/auth/classroom.courses.readonly", "profile", "https://www.googleapis.com/auth/classroom.courses.readonly", "https://www.googleapis.com/auth/classroom.rosters.readonly", "https://www.googleapis.com/auth/classroom.profile.emails"];
//var SCOPES = [ "profile"];

// The list of students in the currently loaded .MER file, in case of no LocalStorage API. Otherwise, this is empty.
var currentStudents = [];

// The list of students in the currently loaded class
var inClass = [];

// The current course that is loaded (only ID and name are stored)
var currentCourse = {id: "", name: ""};

// The name of the teacher. It is a space to start in case it is not loaded in time for a refresh of student data, to
// prevent student from not seeing information about deadlines, etc.
var teacher = " ";

// Check if current user has authorized this application.
function checkAuth() {
    gapi.auth.authorize(
        {
            "client_id": CLIENT_ID,
            "scope": SCOPES.join(" "),
            "immediate": true
        }, handleAuthResult);
}

// Handle response from authorization server. Gets called after something tries to authorize.
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

// Initiate auth flow in response to user clicking authorize button.
function handleAuthClick() {
    gapi.auth.authorize(
        {client_id: CLIENT_ID, scope: SCOPES.join(" "), immediate: false},
        handleAuthResult);
    return false;
}

// Load Classroom API client library.
function loadClassroomApi() {
    gapi.client.load("classroom", "v1", listCourses);
}

// Get the list of courses using the JavaScript Classroom API, then append this list of courses to the dropdown in
// the menu.
function listCourses(id) {
    // Form HTTP request
    var request = gapi.client.classroom.courses.list({
    });

    // Execute request asynchronously, then callback function os called on finish.
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

// Gets the teacher of a course given the course ID using the JavaScript Google Client API.
function getTeacher(id) {
    var request = gapi.client.request({
        // The path to send the request to
        path: "https://classroom.googleapis.com/v1/courses/" + id + "/teachers",
        // The HTTP parameters to send
        params: {

        }
    });
    request.execute(function(resp) {
        var teachers = resp.teachers;
        teacher = teachers[0];
    });
}

// Appends a course name to the dropdown, along with a function call for onclick in the dropdown.
function appendDropdown(message, id) {
    $("#dropdown").append("<li><a href=\"javascript:selectCourse(" + id + ", '" + message + "')\">" + message + "</a></li>");
}

// Appends a student to the table of students, given a name, ID, and status.
function appendStudent(name, id, status) {
    console.log("name: " + name + " id: " + id + " status: " + status);
    $("#students").find("tbody")
        .append($("<tr>")
            .append($("<td>")
                .text(name)
            ).append($("<td>")
            // If the ID is blank, change what is written to the status section
                .text(id == "" ? "Could not find in .MER file" : status)
                .attr("id", id == "" ? name : id)
                .attr("class", id == "" ? "no" : "id")
            )
        );
}

// Signs out user from Google
function signOut() {
    var auth = gapi.auth.getAuthInstance();
    auth.signOut().then(function () {
        console.log("User signed out.");
    });
}

// Lists students in Google Classroom class selected through ID. Then, refreshes info from server about their status.
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

// Called when course is selected from dropdown. Shows hidden elements, lists students, sets currentCourse variable
function selectCourse(id, name) {
    currentCourse.id = id;
    currentCourse.name = name;
    listStudents(id);
    $("#deadline-span").show();
    $("#requirements-span").show();
    $("#class-name").text(name);
    $("#button-bar").show();
}

// Handles file selection (for the .MER file selection)
function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object
    // If a file is selected and it is a .MER file
    if(typeof files !== "undefined" && files[0].name.split(".")[files[0].name.split(".").length - 1].toLowerCase() == "mer") {
        var reader = new FileReader();
        // When the reader finishes, call the readCSV function on the resulting text string.
        reader.onload = function() {
            readCSV(this.result);
        };
        // Read the text in the file asynchronously.
        reader.readAsText(files[0]);
    } else {
        // If the correct type of file is not selected, alert.
        alert('Please select a .MER file.');
    }

}

function readCSV(csv) {
    console.log(csv);
    // Use the JQuery-CSV library to parse the CSV string
    $.csv.toObjects(csv, {separator: ",", delimiter: "\""}, function(err, objects) {
        // If the LocalStorage exists:
        if(typeof(Storage) !== "undefined") {
            // Store the values as key-value pairs with the name as the key and the ID as the value
            for(var i = 0; i < objects.length; i++) {
                localStorage.setItem(objects[i]["FIRST"] + " " + objects[i]["LAST"], objects[i]["ID"]);
            }
            // Store the time that the .MER file was read, so that if a school year has passed, the teacher can reload
            // the file if they want.
            localStorage.setItem("time", new Date().getTime());
        } else {
            // If no localStorage: store the names as objects in an array
            for(var i = 0; i < objects.length; i++) {
                var obj = {}
                var key = objects[i]["FIRST"] + " " + objects[i]["LAST"];
                obj[key] = objects[i]["ID"];
                currentStudents.push(obj);
            }
        }
        // Set the label to show that a .MER file was selected recently
        $("#mer-file-label").text(".MER file selected just now");
        updateIds();
    });
}


// Check if a file has been read
function fileRead() {
    // If localStorage exists, check that
    if(typeof(Storage) !== "undefined") {
        // If a time has been set, then a file has been read
        if(localStorage.getItem("time") != null && localStorage.getItem("time") != "") {
            return true;
        } else {
            return false;
        }
    } else {
        // If there are elements in currentStudents, a file has been read
        if(currentStudents.length > 0) {
            return true;
        } else {
            return false;
        }
    }
}

// Gets an ID from the full name of a student, given a read file
function getIdFromName(name) {
    // If the file is read, get the ID
    if(fileRead()) {
        // Get ID from name from localStorage or variable depending on which exists
        if(typeof(Storage) !== "undefined") {
            // Try to get name from localStorage
            if(localStorage.getItem(name) != null) {
                return localStorage.getItem(name);
            } else {
                return "";
            }
        } else {
            // Go through currentStudents until name is found, if not found, return ""
            for(var i = 0; i < currentStudents.length; i++) {
                if(currentStudents[i].name == name) {
                    return currentStudents[i].id;
                }
            }
            return "";
        }
    } else {
        // If no file is read, return empty string
        return "";
    }
}

// Update IDs in table by going through localStorage or the local variable
function updateIds() {
    var elements = document.getElementsByClassName("no");
    for(var i = 0; i < elements.length; i++) {
        elements[i].id = getIdFromName(elements[i].id);
        elements[i].setAttribute("class", "id");
    }
    refreshInfoFromServer();
}

// Refresh statuses in the table after new requirements are set or after new IDs are loaded
function refreshInfoFromServer() {
    // Get elements that have the class ID, since those are the ones that have a student ID set
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    // Object to serialize has teacher name and list of IDs to get info for
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids
    };
    // If browser has not defined location.origin, set it
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    // Make an HTTP POST request to /teacher/getInfo to get info for each student
    $.ajax({
        type: "POST",
        // Sends request to /teacher/getInfo
        url: location.origin + "/teacher/getInfo",
        data: $.toJSON(dictToSerialize),
        success: function(data) {
            // On success, sets info in the form (deadline, requirements for worksheets)
            console.log(data);
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
            // Sets status for each person through ID
            elements = document.getElementsByClassName("id");
            for(var i = 0; i < elements.length; i++) {
                elements[i].innerHTML = data.results[elements[i].id];
            }
        },
        // If error, print to log and alert
        error: function(data, error) {
            console.log(data);
            console.log(error);
            alert("There was an error: " + error)
        }
    });
}

// Update requirements for students after teacher changes them
function updateRequirements() {
    // Get list of students to update for
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    // Send teacher name, student IDs, and requirements as data
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids,
        total: $("#total").val(),
        blank: $("#blank").val(),
        scoredOver: $("#scoredOver").val(),
        deadline: $("#deadline").datepicker("getDate").getTime()
    };
    // If browser doesn't have location.origin, set it
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    $.ajax({
        type: "POST",
        url: location.origin + "/teacher/setInfo",
        data: $.toJSON(dictToSerialize),
        // On success, set the info in the form to what everything was set to and refresh info
        success: function(data) {
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
            refreshInfoFromServer();
        },
        // On error, print to log and alert
        error: function(data, error) {
            console.log(data);
            console.log(error);
            alert("There was an error: " + error)
        }
    });
}

// Reset all data for users to whatever requirements are in the form
function resetUsers() {
    // Get list of student IDs
    var elements = document.getElementsByClassName("id");
    var ids = [];
    for(var i = 0; i < elements.length; i++) {
        ids.push(elements[i].id);
    }
    // Send teacher name, ids, and requirements
    var dictToSerialize = {
        teacher: teacher.profile.name.fullName,
        id: ids,
        total: $("#total").val(),
        blank: $("#blank").val(),
        scoredOver: $("#scoredOver").val(),
        deadline: $("#deadline").datepicker("getDate").getTime()
    };
    // If browser doesn't define location.origin, set it
    if (!location.origin)
        location.origin = location.protocol + "//" + location.host;
    console.log($.toJSON(dictToSerialize));
    // Send request
    $.ajax({
        type: "POST",
        url: location.origin + "/teacher/resetUsers",
        data: $.toJSON(dictToSerialize),
        // On success, set requirements that were set in the form, and refresh
        success: function(data) {
            $("#deadline").datepicker("update", new Date(Number(data.deadline)));
            $("#total").val(Number(data.total));
            $("#blank").val(Number(data.blank));
            $("#scoredOver").val(Number(data.scoredOver));
            refreshInfoFromServer();
        },
        // On error, log data and alert
        error: function(data, error) {
            console.log(data);
            console.log(error);
            alert("There was an error: " + error)
        }
    });
}

// Things to do on load
$(function() {
    // If no File APIs, alert to switch browsers
    if (window.File && window.FileReader && window.FileList) {
        // Great success! All the File APIs are supported.
    } else {
        alert('Please use Google Chrome or update your browser, as this site requires newer features not available in this version of your browser.');
    }
    // On file picked, call function handleFileSelect
    document.getElementById('file-picker').addEventListener('change', handleFileSelect, false);
    // If there is localStorage, set the label next to the file picker to the amount of time ago the file was read.
    if(typeof(Storage) !== "undefined") {
       if(localStorage.getItem("time") !== null && localStorage.getItem("time") != "") {
            $("#mer-file-label").text(".MER file loaded from " + moment(Number(localStorage.getItem("time"))).fromNow())
       }
    }
});

