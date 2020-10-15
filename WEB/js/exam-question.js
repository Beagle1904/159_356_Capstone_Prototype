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

    function sendAnswer() {
        var answerNum = $(this)[0].getAttribute("btnnum")
        console.log(answerNum+" selected");

        $.ajax({
            method: 'POST',
            url: _config.api.invokeUrl + '/exam/answer',
            headers: {
                Authorization: authToken
            },
            data: JSON.stringify({
                "question": questionNum-1,
                "answer": answerNum
            }),
            contentType: 'application/json',
            success: function() {
                console.log("Answered");
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error starting exam: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    function genAnswerRadBtn(choiceNum, choice) {
        var radBtn = document.createElement("input");
        radBtn.setAttribute("type", "radio");
        radBtn.setAttribute("name", "optradio");
        radBtn.setAttribute("id", "radBtn"+choiceNum);
        radBtn.setAttribute("btnNum", choiceNum);

        var btnLabel = document.createElement("label");
        btnLabel.appendChild(radBtn);
        btnLabel.appendChild(document.createTextNode(choice));
        return btnLabel;
    }

    function fillHtml(result) {
        document.getElementById("prompt").innerHTML = result.context;
        document.getElementById("details").innerHTML = result.details;
        if (result.hasOwnProperty("image")) {
            document.getElementById("questionImg").setAttribute("src", result.image);
            document.getElementById("questionImg").setAttribute("visibility", "visible");
        }

        var choices = result.choices;
        for (var choice in choices) {
            var newRadBtn = genAnswerRadBtn(choice, choices[choice]);
            document.getElementById("answers").appendChild(newRadBtn);
            document.getElementById("answers").appendChild(document.createElement("br"));
            // document.getElementById("btnLabel"+choice).setAttribute("onclick", "sendAnswer("+choice+")");
            $("#radBtn"+choice).click(sendAnswer);
        }
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

    function getQuestionComplete(questionObj) {
        if (questionObj.hasOwnProperty("answer")) return "✓";
        else return "☐";
    }

    function genQuestionSummary(questionNum, questionObj) {
        var questionSummary = document.createElement("tr");

        var questionLink = document.createElement("a");
        questionLink.setAttribute("href", "/question_view_single.html?question="+questionNum);
        var questionNumCol = document.createElement("td");
        questionNumCol.appendChild(document.createTextNode(String(questionNum)));
        questionLink.appendChild(questionNumCol);
        questionSummary.appendChild(questionLink);

        var completeCol = document.createElement("td");
        completeCol.appendChild(document.createTextNode(getQuestionComplete(questionObj)));
        questionSummary.appendChild(completeCol);

        return questionSummary;
    }

    function fillSummary(result) {
        console.log(result)

        var summaryTable = document.getElementById("summaryTable");
        for (var question in result.questions) {
            summaryTable.appendChild(genQuestionSummary(parseInt(question)+1, result.questions[question]))
        }

        var numQuestions = result.questions.length;
        console.log(questionNum);
        if(questionNum > 1) $("#previousQuestion").click(function() {window.location.href = "/question_view_single.html?question="+(parseInt(questionNum)-1)});
        if(questionNum < numQuestions) $("#nextQuestion").click(function() {window.location.href = "/question_view_single.html?question="+(parseInt(questionNum)+1)});
        else $("#nextQuestion").click(function() {window.location.href = "/exam_summary.html"});
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
            success: fillSummary,
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
        loadExamData();

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });

}(jQuery));
