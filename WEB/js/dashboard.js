/*global WildRydes _config*/

var ExamPlatform = window.ExamPlatform || {};
ExamPlatform.map = ExamPlatform.map || {};

(function appScopeWrapper($) {
    var authToken;
    ExamPlatform.authToken.then(function setAuthToken(token) {
        if (token) {
            authToken = token;
        } else {
            window.location.href = '/signin.html';
        }
    }).catch(function handleTokenError(error) {
        alert(error);
        window.location.href = '/signin.html';
    });

    $(function onDocReady() {});

}(jQuery));
