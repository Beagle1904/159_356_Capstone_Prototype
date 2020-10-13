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


    function startPracticeExam() {
        var questionData = [];
        for (var i=0; i</*TODO*/3; i++) {
            questionData.push("");
        }

        $.ajax({
            method: 'POST',
            url: _config.api.invokeUrl + '/exam/start',
            headers: {
                Authorization: authToken
            },
            data: JSON.stringify({
                type: "PRACTICE",
                questions: questionData
            }),
            contentType: 'application/json',
            success: function () {
                window.location.href = '/exam/summary.html';
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    // Register click handler for #request button
    $(function onDocReady() {
        $('#startExamButton').click(startPracticeExam);

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });

}(jQuery));
