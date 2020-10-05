package secapstone.questions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteQuestionsTest extends AbstractDynamoTest {
	// Requires questions to delete -- depends on AddQuestions function to provide these
	// Needs to check question data -- depends on GetQuestions to check if deleted
	AddQuestions addFunc = new AddQuestions();
	GetQuestions getFunc = new GetQuestions();
	DeleteQuestions deleteFunc = new DeleteQuestions();

	protected DeleteQuestionsTest() {
		super(new String[]{"Questions"}, new String[]{"ID"});
	}

	@Test
	void deleteQuestionTest() {
		String questionID = addOneQuestion();

		Map<String, Object> deleteRequest = defaultInputMap();
		((Map) deleteRequest.get("body-json")).put("ID", questionID);
		deleteFunc.handleRequest(deleteRequest, context);

		// Delete request is identical to single-question get request
		JSONObject getResponse = new JSONObject(getFunc.handleRequest(deleteRequest, context));
		assertEquals(0, getResponse.getJSONArray("questions").length());
	}

	@Test
	void deleteBadIDTest() {
		String questionID = "AAAA";

		Map<String, Object> deleteRequest = defaultInputMap();
		((Map) deleteRequest.get("body-json")).put("ID", questionID);
		assertDoesNotThrow(() -> deleteFunc.handleRequest(deleteRequest, context));
	}

	String addOneQuestion() {
		Map<String, Object> addRequest = defaultInputMap();
		Object[] questionArray = new Object[]{genTestQuestionMap("")};
		((Map) addRequest.get("body-json")).put("questions", questionArray);

		Map<String, Object> addResponse = addFunc.handleRequest(addRequest, context);

		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONArray questionsJSONArray = new JSONObject(addResponse).getJSONArray("IDs");
		String newID = questionsJSONArray.getString(0);
		addItem("Questions", newID);
		return newID;
	}
}