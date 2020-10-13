var ExamPlatform = window.ExamPlatform || {};

(function scopeWrapper($) {

	//Bind events to buttons on page load
	$(function onDocReady(){
		$('submitQuestions').click(startExam);
		$('addTag').click(addNewSubject);
	}
	
	var i = 0;
	
	//For adding new subjects to exam
	function addNewSubject(event){
		$('.form-group:last').clone().insertAfter(".form-group:last");
		$(".form-group:last").attr();
		i++;
	}
	
	
	//For submitting the tags and start the exam (TBD by Roman)
	function startExam(event){
		
	}

}(jquery));