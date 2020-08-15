package secapstone.exam;

import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddQuestionTest {

	@Test
	void testCreateQuestion() {
		AddQuestion handler = new AddQuestion();
		Context context = createContext();

		Map<String, Object> input = new JSONObject("{\"context\": \"Question Context\",\"details\": \"Question Details\",\"answer\": \"Correct Answer\",\"reason\": \"Dummy Reason\",\"type\": \"MCQ\",\"choices\": [\"Answer 1\", \"Answer 2\"],\"tags\": [\"tag1\", \"tag2\"]}").toMap();

		Map<String, String> output = handler.handleRequest(input, context);
		System.out.println(output.toString());
		assertEquals("Success", output.get("status"));
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}