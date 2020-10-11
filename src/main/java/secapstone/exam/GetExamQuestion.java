package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetExamQuestion extends AbstractExamFunction {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);

		// Check that there is an exam in progress
		if (!examInProgress(user)) {
			throw new Error("No exam in progress");
		}

		// Get the requested question
		int questionNumber;
		try {
			questionNumber = inputJSON.getJSONObject("params").getInt("question");
		} catch (JSONException e) {
			throw new Error("No position parameter provided");
		}

		return getExamQuestion(user, questionNumber);
	}

	private Map<String, Object> getExamQuestion(Item user, int questionNumber) {
		JSONObject exam = new JSONObject(user.getMap("exam"));
		JSONObject requestQuestion;
		try {
			requestQuestion = exam.getJSONArray("questions").getJSONObject(questionNumber);
		} catch (JSONException e) {
			throw new Error("Index out of bounds");
		}
		String questionID = requestQuestion.getString("questionID");

		Map<String, Object> questionMap = getQuestionAttribs(questionID);
		questionMap.put("questionID", questionID);
		return questionMap;
	}

	private Map<String, Object> getQuestionAttribs(String questionID) {
		Item questionItem = QUESTIONS.getItem("ID", questionID);
		// Only need certain attributes
		final String[] reqAttribs = new String[]{"choices", "context", "details", "questionType", "reason", "tags"};

		Map<String, Object> outputAttribs = new HashMap<>();
		for (String attrib : reqAttribs) {
			outputAttribs.put(attrib, questionItem.get(attrib));
		}

		return outputAttribs;
	}
}
