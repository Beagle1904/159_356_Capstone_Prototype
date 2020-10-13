package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class AnswerQuestion extends AbstractExamFunction {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);

		// Check that there is an exam in progress
		if (!examInProgress(user)) {
			throw new Error("No exam in progress");
		}

		int questionNum = inputJSON.getJSONObject("body-json").getInt("question");
		int answer = inputJSON.getJSONObject("body-json").getInt("answer");
		Map<String, Object> examMap = answerQuestion(user.getMap("exam"), questionNum, answer);
		UpdateItemSpec updateSpec = new UpdateItemSpec().withPrimaryKey("username", user.getString("username"))
				.addAttributeUpdate(new AttributeUpdate("exam").put(examMap));
		USERS.updateItem(updateSpec);

		return null;
	}

	private Map<String, Object> answerQuestion(Map<String, Object> exam, int questionNum, int answer) {
		JSONObject examJSON = new JSONObject(exam);
		JSONArray questions = examJSON.getJSONArray("questions");
		questions.getJSONObject(questionNum).put("answer", answer);
		return examJSON.toMap();
	}
}
