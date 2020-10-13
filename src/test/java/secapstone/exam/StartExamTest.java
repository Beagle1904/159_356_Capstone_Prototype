package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;
import secapstone.questions.AddQuestions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartExamTest extends AbstractDynamoTest {
	Item testUser;

	// Requires questions to request -- depends on AddQuestions function to provide these
	AddQuestions addFunc = new AddQuestions();
	StartExam startFunc = new StartExam();

	protected StartExamTest() {
		super(new String[]{"Questions"}, new String[]{"ID"});
	}

	@BeforeEach
	void setUp() {
		updateUser();
	}

	@Test
	void createPracticeTest() {
		// Generate test questions
		addMultipleQuestions(10);

		Map<String, Object> startMap = defaultInputMap();
		((Map) startMap.get("body-json")).put("type", "PRACTICE");
		Map<String, Integer> questionsMap = new HashMap<>();
		questionsMap.put("Test Tag 1", 10);
		((Map) startMap.get("body-json")).put("questions", questionsMap);

		startFunc.handleRequest(startMap, context);

		assertTrue(checkExam());
	}

	@Test
	void createPracticeNoDupes() {
		// Generate test questions
		addMultipleQuestions(10);

		Map<String, Object> startMap = defaultInputMap();
		((Map) startMap.get("body-json")).put("type", "PRACTICE");
		Map<String, Integer> questionsMap = new HashMap<>();
		questionsMap.put("Test Tag 1", 10);
		questionsMap.put("Test Tag 2", 10);
		((Map) startMap.get("body-json")).put("questions", questionsMap);

		startFunc.handleRequest(startMap, context);

		assertTrue(checkExam());
		assertFalse(checkDupes());
	}

	@Test
	void createPracticeInsufficientQuestions() {
		// Generate test questions
		addMultipleQuestions(10);

		Map<String, Object> startMap = defaultInputMap();
		((Map) startMap.get("body-json")).put("type", "PRACTICE");
		Map<String, Integer> questionsMap = new HashMap<>();
		questionsMap.put("Test Tag 1", 12);
		((Map) startMap.get("body-json")).put("questions", questionsMap);

		startFunc.handleRequest(startMap, context);

		assertTrue(checkExam());
		assertFalse(checkDupes());
	}

	//todo Mock exam tests

	@BeforeAll
	static void clearExams() {
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username", TEST_USERNAME);
		updateItemSpec.addAttributeUpdate(new AttributeUpdate("exam").delete());
		USERS.updateItem(updateItemSpec);
	}

	@AfterEach
	void cleanUp() {
		clearExams();
	}

	// Utility functions
	private void updateUser() {
		testUser = USERS.getItem("username", TEST_USERNAME);
	}

	private void addMultipleQuestions(int numQuestions) {
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
	}

	private boolean checkDupes() {
		// Returns true if any duplicate questions exist in the current exam in progress
		updateUser();
		List<Object> questions = ((List) testUser.getMap("exam").get("questions"));

		List<String> questionIDs = new ArrayList<>();
		for (Object question : questions) {
			String questionID = (String) ((Map) question).get("questionID");
			if (questionIDs.contains(questionID)) return true;
			else questionIDs.add(questionID);
		}

		return false;
	}

	private boolean checkExam() {
		updateUser();
		return testUser.getMap("exam") != null;
	}
}