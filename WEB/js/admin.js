/*global WildRydes _config*/

var ExamPlatform = window.ExamPlatform || {};
ExamPlatform.map = ExamPlatform.map || {};

(function appScopeWrapper($) {
    var authToken;
    var questionObject = {};
    var data = {};

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

    function saveQuestion() {
        // context, details, reason, questionType, choices, answer, tags
        $.ajax({
            method: 'POST',
            url: _config.api.invokeUrl + '/questions/add',
            headers: {
                Authorization: authToken
            },
            data: JSON.stringify({
                questions: [
                    {
                        context: questionObject.context,
                        reason: questionObject.reason,
                        choices: questionObject.choices,
                        questionType: questionObject.questionType,
                        answer: questionObject.answer,
                        tags: questionObject.tags,
                        details: questionObject.details
                    }]
            }),
            contentType: 'application/json',
            success: function () {
                alert("Saved!");
            },
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error saving question: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
            }
        });
    }

    function receivedQuestions(result) {
        data.questions = result.questions;
        var rows = "";
        var tags;
        $("#questionsTableBody").html("");
        for (var i = 0; i < result.questions.length; i++) {
            tags = result.questions[i].tags.join();
            rows = rows + "<tr class='questionRow'><td>" + result.questions[i].ID + "</td><td>" + result.questions[i].details + "</td><td>" + tags + "</td></tr>"
        }
        $("#questionsTableBody").append(rows);
        $(".questionRow").click(function (e) {
            $(".questionRow").css("background-color", "white");
            $(this).css("background-color", "#eee");
            var questionId = $(this).children()[0].innerHTML;
            for (var i = 0; i < data.questions.length; i++) {
                if (data.questions[i].ID == questionId) {
                    questionObject = data.questions[i];
                }
            }
        });
    }

    // Register click handler for #request button
    $(function onDocReady() {
        $('#updateQuestionButton').click(updateQuestionBtn);
        $('#refreshQuestionsButton').click(refreshQuestionsList);
        $('#newQuestionButton').click(newQuestionDialog);
        $('#editQuestionButton').click(editQuestionDialog);


        var maxGroup = 10;

        //add more fields group
        $(".addMore").click(function () {
            if ($('body').find('.fieldGroup').length < maxGroup) {
                var fieldHTML = '<div class="form-group fieldGroup">' + $(".fieldGroupCopy").html() + '</div>';
                $('body').find('.fieldGroup:last').after(fieldHTML);
            } else {
                alert('Maximum ' + maxGroup + ' groups are allowed.');
            }
        });

        //remove fields group
        $("body").on("click", ".remove", function () {
            $(this).parents(".fieldGroup").remove();
        });

        if (!_config.api.invokeUrl) {
            $('#noApiMessage').show();
        }
    });


    function refreshQuestionsList() {
        $.ajax({
            method: 'POST',
            url: _config.api.invokeUrl + '/questions/get',
            //  crossDomain: true,
            headers: {
                Authorization: authToken
            },
            data: {},
            contentType: 'application/json',
            success: receivedQuestions,
            error: function ajaxError(jqXHR, textStatus, errorThrown) {
                console.error('Error refreshing: ', textStatus, ', Details: ', errorThrown);
                console.error('Response: ', jqXHR.responseText);
                alert('An error occured while refreshing list:\n' + jqXHR.responseText);
            }
        });
    }

    function updateQuestionBtn(event) {
        event.preventDefault();
        var choices = [];
        var choicesInputs = $("input[name='choice[]']");
        for (var i = 0; i < choicesInputs.length - 1; i++) {
            choices.push(choicesInputs[i].value);
        }
        questionObject.details = $("#details")[0].value;
        questionObject.context = $("#context")[0].value;
        questionObject.answer = $("#answer")[0].value;
        questionObject.questionType = $("#type")[0].value;
        questionObject.tags = $("#tags")[0].value.split(",");
        questionObject.choices = choices;
        if (questionObject.ID) {
            alert("Not implemented!")
        } else {
            saveQuestion();
        }
    }

    function newQuestionDialog(event) {
        // context, details, reason, questionType, choices, answer, tags
        questionObject.ID = "";
        questionObject.context = "";
        questionObject.details = "";
        questionObject.reason = "";
        questionObject.questionType = "MCQ";
        questionObject.choices = [];
        questionObject.answer = "";
        questionObject.tags = [];

        syncQuestionDialogData();

        // show dialog for new question
        $("#editQuestionModal").modal();

    }

    function syncQuestionDialogData() {
        $("#details")[0].value = questionObject.details;
        $("#context")[0].value = questionObject.context;
        $("#answer")[0].value = questionObject.answer;
        $("#type")[0].value = questionObject.questionType;
        $("#tags")[0].value = questionObject.tags.join(",");
        $('form .remove').parent().parent().parent().remove();
        for (var i = 0; i < questionObject.choices.length; i++) {
            if (i == 0) {
                $('body').find('.fieldGroup:last').find('input')[0].value = questionObject.choices[i];
                continue;
            }
            var fieldHTML = '<div class="form-group fieldGroup">' + $(".fieldGroupCopy").html() + '</div>';
            $('body').find('.fieldGroup:last').after(fieldHTML);
            $('body').find('.fieldGroup:last').find('input')[0].value = questionObject.choices[i];
        }
    }

    function editQuestionDialog(event) {
        // show dialog for editing a question
        syncQuestionDialogData();

        $("#editQuestionModal").modal();
        console.log(questionObject);

    }

    function displayUpdate(text) {
        $('#updates').append($('<li>' + text + '</li>'));
    }
}(jQuery));
