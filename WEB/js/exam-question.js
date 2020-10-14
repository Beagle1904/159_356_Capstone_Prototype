var ExamPlatform = window.ExamPlatform || {};
ExamPlatform.map = ExamPlatform.map || {};

(function appScopeWrapper($) {
    const questionNum = new URLSearchParams(window.location.search).get('question');
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

    function fillHtml(result) {

    }

    function loadQuestion() {
        $.ajax({
            method: 'GET',
            url: _config.api.invokeUrl + '/exam/get?question='+(questionNum-1),
            headers: {
                Authorization: authToken
            },
            data: {},
            contentType: 'application/json',
            success: fillHtml,
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    // Register click handler for #request button
    $(function onDocReady() {
        $('#nextQuestionButton').click(function () {
            window.location.href = '/exam-question?question='+(questionNum+1);
        });
        console.log(questionNum);

        loadQuestion(questionNum);

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });

}(jQuery));
