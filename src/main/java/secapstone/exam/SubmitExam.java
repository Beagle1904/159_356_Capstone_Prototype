package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitExam extends AbstractExamFunction {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);

		// Check that there is an exam in progress
		if (!examInProgress(user)) {
			throw new Error("No exam in progress");
		}

		Item resultItem = genResultItem(user);
		RESULTS.putItem(resultItem);
		return null;
	}

	private Item genResultItem(Item user) {
		Item result = new Item()
				.withPrimaryKey("user", user.getString("username"), "time", LocalDateTime.now().toString());

		JSONObject examObject = new JSONObject(user.getMap("exam"));
		List<Map<String, Object>> questions = getQuestionMaps(examObject.getJSONArray("questions"));

		result = result.withList("questions", questions).withString("type", examObject.getString("type"));
		return result;
	}

	private List<Map<String, Object>> getQuestionMaps(JSONArray questions) {
		List<Map<String, Object>> questionMaps = new ArrayList<>();
		for (int i = 0; i < questions.length(); i++) {
			questionMaps.add(getQuestionMap(questions.getJSONObject(i)));
		}
		return questionMaps;
	}

	private Map<String, Object> getQuestionMap(JSONObject question) {
		String questionID = question.getString("questionID");
		Item questionItem = QUESTIONS.getItem("ID", questionID);

		int actualChosen = question.getJSONArray("answerOrder").getInt(question.getInt("chosen"));
		int correctAnswer = questionItem.getInt("answer");

		Map<String, Object> output = new HashMap<>();
		output.put("questionID", questionID);
		output.put("correct", actualChosen == correctAnswer);
		return output;
	}
}
