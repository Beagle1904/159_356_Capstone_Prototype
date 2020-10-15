var ExamPlatform = window.ExamPlatform || {};
ExamPlatform.map = ExamPlatform.map || {};

(function appScopeWrapper($) {
    var authToken;
    var numQuestions;

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
        console.log(result);
        numQuestions = result.questions.length;
        for(var i=0; i<numQuestions; i++) {
            var newRow = document.createElement("tr");

            var questionNumCol = document.createElement("td");
            var questionLink = document.createElement("a");
            questionLink.setAttribute("href", "/question_view_single.html?question="+(i+1));
            questionLink.appendChild(document.createTextNode(String(i+1)));
            questionNumCol.appendChild(questionLink);

            var answeredCol = document.createElement("td");
            var answered = result.questions[i].answer != null;
            answeredCol.appendChild(document.createTextNode(String(answered)));

            newRow.append(questionNumCol, answeredCol);
            document.getElementById("examSummaryRows").appendChild(newRow);
        }
    }

    function loadExamData() {
        $.ajax({
            method: 'GET',
            url: _config.api.invokeUrl + '/exam/summary',
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

    function submitExam() {
        $.ajax({
            method: 'POST',
            url: _config.api.invokeUrl + '/exam/submit',
            headers: {
                Authorization: authToken
            },
            data: {},
            contentType: 'application/json',
            success: function() {
                delete ExamPlatform.map.numQuestions;
                window.location.href = "/index.html";
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    // Register click handler for #request button
    $(function onDocReady() {
        // Fill out questions table
        loadExamData();
        $('#submitButton').click(submitExam);

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });

}(jQuery));
