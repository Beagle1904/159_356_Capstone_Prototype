package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONObject;

import java.util.Map;

public class StartExam extends AbstractExamFunction {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);
		String username = user.getString("username");

		// Check if an exam is in progress
		if (examInProgress(user)) {
			throw new Error("Exam already in progress");
		}

		// Check the type of exam
		String examType = inputJSON.getJSONObject("body-json").getString("type");

		// Create the exam object

		// Push the exam object to the database
		return null;
	}

	private boolean examInProgress(Item user) {
		System.out.println(user.get("exam"));
		return user.get("exam") != null;
	}
}
