var ExamPlatform = window.ExamPlatform || {};

(function scopeWrapper($) {

	//Bind events to buttons on page load
	$(function onDocReady(){
		$('#addTag').click(addNewSubject);
	});

	var i = 0;

	//For adding new subjects to exam
	function addNewSubject(){
		$('.form-group:last').clone().insertAfter(".form-group:last");
		$(".form-group:last").attr();
		i++;
	}

}(jQuery));