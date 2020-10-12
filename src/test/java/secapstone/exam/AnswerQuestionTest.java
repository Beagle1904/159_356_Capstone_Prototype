package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;
import secapstone.questions.AddQuestions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AnswerQuestionTest extends AbstractDynamoTest {
	// Requires questions to add - depends on AddQuestions
	AddQuestions addFunc = new AddQuestions();
	// Requires exam to test - depends on StartExam
	StartExam startFunc = new StartExam();
	AnswerQuestion answerFunc = new AnswerQuestion();

	protected AnswerQuestionTest() {
		super(new String[]{"Questions"}, new String[]{"ID"});
	}

	@BeforeAll
	static void clearExams() {
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("username", TEST_USERNAME);
		updateItemSpec.addAttributeUpdate(new AttributeUpdate("exam").delete());
		USERS.updateItem(updateItemSpec);
	}

	@Test
	void testAnswerQuestion() {
		startPractice();

		Map<String, Object> answerRequest = defaultInputMap();
		((Map) answerRequest.get("body-json")).put("question", 0);
		((Map) answerRequest.get("body-json")).put("answer", 0);

		assertDoesNotThrow(() -> answerFunc.handleRequest(answerRequest, context));
	}

	@Test
	void testChangeAnswer() {
		startPractice();

		Map<String, Object> answerRequest = defaultInputMap();
		((Map) answerRequest.get("body-json")).put("question", 0);
		((Map) answerRequest.get("body-json")).put("answer", 0);
		assertDoesNotThrow(() -> answerFunc.handleRequest(answerRequest, context));

		((Map) answerRequest.get("body-json")).put("answer", 1);
		assertDoesNotThrow(() -> answerFunc.handleRequest(answerRequest, context));
	}

	@AfterEach
	void cleanUp() {
		clearExams();
	}

	// Utility functions
	private void startPractice() {
		addMultipleQuestions(10);
		Map<String, Object> startRequest = defaultInputMap();
		((Map) startRequest.get("body-json")).put("type", "PRACTICE");
		Map<String, Object> questionsMap = new HashMap<>();
		questionsMap.put("Test tag 1", 10);
		((Map) startRequest.get("body-json")).put("questions", questionsMap);

		startFunc.handleRequest(startRequest, context);
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
}