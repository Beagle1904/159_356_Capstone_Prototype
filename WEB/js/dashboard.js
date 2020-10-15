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

    $(function onDocReady() {
        $("#takeExam").click(loadExamData);
    });

    function loadExamData() {
        $.ajax({
            method: 'GET',
            url: _config.api.invokeUrl + '/exam/summary',
            headers: {
                Authorization: authToken
            },
            data: {},
            contentType: 'application/json',
            success: function (result) {
                if(result.hasOwnProperty("questions")) window.location.href = "/exam_summary.html";
                else window.location.href = "/pre-exam_question_selection.html"
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

}(jQuery));
