package secapstone.questions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		((Map) addRequest.get("body-json")).put("questions", new Object[]{testQuestionMap});

		Map<String, Object> addResponse = addFunc.handleRequest(addRequest, context);
		String questionID = ((ArrayList<String>) addResponse.get("IDs")).get(0);
		addItem("Questions", questionID);

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("ID", questionID);

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONObject json = new JSONObject(getResponse);
		// Check the ID is correct ("questions":[{"ID":"XXXXXXXX"...}])
		assertEquals(questionID, json.getJSONArray("questions").getJSONObject(0).getString("ID"));
	}

	@Test
	void getQuestionsWithTag() {
		final int numTestQuestions = 10;
		String[] questionIDs = addMultipleQuestions(numTestQuestions);

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("tags", new String[]{"Test Tag 1"});

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONObject json = new JSONObject(getResponse);
		JSONArray questionsJSONArray = json.getJSONArray("questions");
		assertEquals(numTestQuestions, questionsJSONArray.length());
	}

	@Test
	void getQuestionsWithTwoTags() {
		final int numTestQuestions = 10;
		String[] questionIDs = addMultipleQuestions(numTestQuestions);

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("tags", new String[]{"Test Tag 1", "Test Tag 2"});

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONObject json = new JSONObject(getResponse);
		JSONArray questionsJSONArray = json.getJSONArray("questions");
		assertEquals(numTestQuestions, questionsJSONArray.length());
	}

	@Test
	void getQuestionsWithBadTags() {
		final int numTestQuestions = 10;
		String[] questionIDs = addMultipleQuestions(numTestQuestions);

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("tags", new String[]{"Test Tag 1", "Bad Test Tag"});

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONObject json = new JSONObject(getResponse);
		JSONArray questionsJSONArray = json.getJSONArray("questions");
		assertEquals(0, questionsJSONArray.length());
	}

	@Test
	void getBadID() {
		String questionID = UUID.randomUUID().toString();

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("ID", questionID);

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		assertEquals(0, ((ArrayList<Object>) getResponse.get("questions")).size());
	}

	@Test
	void getEmptyRequest() {
		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("tags", new String[]{"Test Tag 1", "Bad Test Tag"});

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Check that questions are returned
		assertEquals(0, ((ArrayList<Object>) getResponse.get("questions")).size());
	}

	@Test
	void getTestStateQuestions() {
		final int numTestQuestions = 10;
		String[] questionIDs = addMultipleQuestions(numTestQuestions);

		Map<String, Object> getRequest = defaultInputMap();
		((Map) getRequest.get("body-json")).put("state", new String[]{"TEST"});

		Map<String, Object> getResponse = getFunc.handleRequest(getRequest, context);
		// Check that the correct number of questions are returned
		assertTrue(((ArrayList<Object>) getResponse.get("questions")).size() >= 10);
	}

	// Utility functions
	private String[] addMultipleQuestions(int numQuestions) {
		Map<String, Object> addRequest = defaultInputMap();

		Object[] questions = new Object[numQuestions];
		for (int i = 0; i < numQuestions; i++) {
			questions[i] = genTestQuestionMap(" " + i);
		}

		((Map) addRequest.get("body-json")).put("questions", questions);
		Map<String, Object> addResponse = addFunc.handleRequest(addRequest, context);

		// Convert the map to a JSONObject to allow easier traversal of JSON structure
		JSONArray questionsJSONArray = new JSONObject(addResponse).getJSONArray("IDs");
		String[] newIDs = new String[numQuestions];
		for (int i = 0; i < numQuestions; i++) {
			String questionID = questionsJSONArray.getString(i);
			newIDs[i] = questionID;
			addItem("Questions", questionID);
		}

		return newIDs;
	}
}