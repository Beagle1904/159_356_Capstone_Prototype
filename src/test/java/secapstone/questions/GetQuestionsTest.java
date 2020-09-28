package secapstone.questions;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GetQuestionsTest extends AbstractDynamoTest {
	// Requires questions to request -- depends on AddQuestions function to provide these
	AddQuestions addFunc = new AddQuestions();
	GetQuestions getFunc = new GetQuestions();

	protected GetQuestionsTest() {
		super(new String[]{"Questions"}, new String[] {"ID"});
	}

	@Test
	void getSingleQuestion() {
		Map<String, Object> addRequest = defaultInputMap();

		Map<String, Object> testQuestionMap = genTestQuestionMap("");
		addRequest.put("questions", new Object[] {testQuestionMap});

		Map<String, Object> addResponse = addFunc.handleRequest(JSONObject.valueToString(addRequest), context);
		String questionID = ((ArrayList<String>) addResponse.get("IDs")).get(0);
		addItem("Questions", questionID);

		Map<String, Object> getRequest = defaultInputMap();
		getRequest.put("ID", questionID);

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONObject json = new JSONObject(getResponse);
		// Check the ID is correct ("questions":[{"ID":"XXXXXXXX"...}])
		assertEquals(questionID, json.getJSONArray("questions").getJSONObject(0).getString("ID"));
	}

	// todo Test: Get based on tags
	// todo Test: Get based on other criteria?
	// todo Test: Get nothing
}