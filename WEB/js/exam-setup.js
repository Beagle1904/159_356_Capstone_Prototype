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
        var cards = document.getElementsByClassName("tagHolder");
        var questionData = {};
        for (var i=0; i<cards.length; i++) {
            var tagName = cards[i].getElementsByTagName("input")[0].value;
            console.log("TAG: "+tagName);
            questionData[tagName] = cards[i].getElementsByTagName("input")[1].value;
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
                window.location.href = '/exam_summary.html';
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    function addTagPair() {
        $(".form-group:last").clone().insertAfter(".form-group:last");
        $(".form-group:last").attr();
    }

    // Register click handler for #request button
    $(function onDocReady() {
        $('#submitQuestions').click(startPracticeExam);
        $('#addTag').click(addTagPair);

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });

}(jQuery));
