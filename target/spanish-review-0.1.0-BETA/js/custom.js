/**
 * Created by plato2000 on 4/21/16.
 */
$(function() {
    $(".row").height("100%");
});

function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());
    var xhr = new XMLHttpRequest();
    var id_token = googleUser.getAuthResponse().id_token;
    console.log("id_token: " + id_token);
    $.post('/login', {"idtoken": id_token}).done( function(data) {
            console.log('Signed in as: ' + xhr.responseText);
        });
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