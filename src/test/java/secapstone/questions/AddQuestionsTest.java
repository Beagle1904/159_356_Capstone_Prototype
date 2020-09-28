package secapstone.questions;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class AddQuestionsTest extends AbstractDynamoTest {

	protected AddQuestionsTest() {
		super(new String[]{"Questions"}, new String[] {"ID"});
	}

	AddQuestions func = new AddQuestions();

	@Test
	void addQuestionTest() {
		Map<String, Object> inputMap = defaultInputMap();

		Map<String, Object> testQuestionMap = genTestQuestionMap("");
		inputMap.put("questions", new Object[] {testQuestionMap});

		Map<String, Object> outputMap = func.handleRequest(JSONObject.valueToString(inputMap), context);
		assertEquals(1, ((ArrayList<String>) outputMap.get("IDs")).size());
		for (String id: (ArrayList<String>) outputMap.get("IDs")) {
			addItem("Questions", id);
		}
	}

	@Test
	void incompleteQuestionTest() {
		Map<String, Object> inputMap = defaultInputMap();
		Map<String, Object> incompleteQuestionMap = new HashMap<>();
		incompleteQuestionMap.put("context", "Test failed context");
		inputMap.put("questions", new Object[] {incompleteQuestionMap});

		assertThrows(Error.class, () -> func.handleRequest(JSONObject.valueToString(inputMap), context));
	}

	@Test
	void multipleQuestionsTest() {
		Map<String, Object> inputMap = defaultInputMap();

		final int numQuestions = 10;
		Object[] questions = new Object[numQuestions];
		for (int i=0; i<numQuestions; i++) {
			questions[i] = genTestQuestionMap(" "+i);
		}
		inputMap.put("questions", questions);

		Map<String, Object> outputMap = func.handleRequest(JSONObject.valueToString(inputMap), context);
		assertEquals(numQuestions, ((ArrayList<String>) outputMap.get("IDs")).size());
		for (String id: (ArrayList<String>) outputMap.get("IDs")) {
			addItem("Questions", id);
		}
	}
}