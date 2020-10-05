package secapstone.questions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditQuestionsTest extends AbstractDynamoTest {
	// Requires questions to request -- depends on AddQuestions function to provide these
	// Needs to check question data -- depends on GetQuestions to request
	AddQuestions addFunc = new AddQuestions();
	GetQuestions getFunc = new GetQuestions();
	EditQuestions editFunc = new EditQuestions();

	protected EditQuestionsTest() {
		super(new String[]{"Questions"}, new String[]{"ID"});
	}

	@Test
	void changeOneAttribTest() {
		// Create a question
		String questionID = addOneQuestion();

		// Edit the question's context variable
		Map<String, Object> editRequest = defaultInputMap();
		((Map) editRequest.get("body-json")).put("ID", questionID);
		Map<String, String> changesMap = new HashMap<>();
		changesMap.put("context", "Edited context");
		((Map) editRequest.get("body-json")).put("changes", changesMap);
		editFunc.handleRequest(editRequest, context);

		// Check the question's context has changed
		assertEquals("Edited context", getQuestionAttrib(questionID, "context"));
	}

	@Test
	void changeTwoAttribTest() {
		// Create a question
		String questionID = addOneQuestion();

		// Edit the question's context and answer variables (String and int)
		Map<String, Object> editRequest = defaultInputMap();
		((Map) editRequest.get("body-json")).put("ID", questionID);

		// Create map of changes
		Map<String, Object> changesMap = new HashMap<>();
		changesMap.put("context", "Edited context");
		changesMap.put("answer", 0);
		((Map) editRequest.get("body-json")).put("changes", changesMap);
		editFunc.handleRequest(editRequest, context);

		// Check the question's context and answer have changed
		assertEquals(0, ((BigDecimal) getQuestionAttrib(questionID, "answer")).intValue());
		assertEquals("Edited context", getQuestionAttrib(questionID, "context"));
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

	Object getQuestionAttrib(String questionID, String attribName) {
		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("ID", questionID);

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);

		JSONObject questionObject = new JSONObject(getResponse).getJSONArray("questions").getJSONObject(0);
		return questionObject.get(attribName);
	}
}