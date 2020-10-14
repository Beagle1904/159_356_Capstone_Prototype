package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONObject;

import java.util.Map;

public class ExamSummary extends AbstractExamFunction {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);

		// Check that there is an exam in progress
		try {
			if (!examInProgress(user)) {
				throw new Error("No exam in progress");
			}
		} catch (Exception e) {
			throw new Error(user.toJSON());
		}

		Map<String, Object> output = user.getMap("exam");
		output.remove("answerOrder");

		return output;
	}
}
