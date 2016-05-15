/**
 * Created by plato2000 on 4/21/16.
 */


function start() {
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: '453755821502-1k95kijujmdh4g16opd1qpaqn6miboro.apps.googleusercontent.com',
            // Scopes to request in addition to 'profile' and 'email'
            scope: 'profile email https://www.googleapis.com/auth/classroom.courses.readonly https://www.googleapis.com/auth/classroom.rosters.readonly https://www.googleapis.com/auth/classroom.profile.emails'
        });
    });
}

$('#g-signin2').click(function() {
    // signInCallback defined in step 6.
    auth2.grantOfflineAccess({'redirect_uri': 'postmessage'}).then(signInCallback);
});

function signInCallback(authResult) {
    if (authResult['code']) {

        // Hide the sign-in button now that the user is authorized, for example:
        $('#signinButton').attr('style', 'display: none');

        // Send the code to the server
        $.ajax({
            type: 'POST',
            url: '/teacherlogin',
            contentType: 'application/octet-stream; charset=utf-8',
            success: function(data) {
                // Handle or verify the server response.
                if(data['sign_in'] == "false") {
                    signOut();
                    $("#signin-failure").modal();
                } else {
                    $('#signinButton').attr('style', 'display: none');
                }
            },
            processData: false,
            data: {"idtoken": authResult['code']}
        });
    } else {
        signOut();
        $("#signin-failure").modal();
        // There was an error.
    }
}

function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
    $(".g-signin2").click(function() {
        //signOut();
    });
}