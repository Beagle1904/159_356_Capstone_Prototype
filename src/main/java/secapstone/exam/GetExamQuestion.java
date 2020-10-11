package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

		Map<String, Object> questionMap = getQuestionAttribs(requestQuestion);

		questionMap.put("questionID", requestQuestion.getString("questionID"));
		questionMap.put("chosen", requestQuestion.has("chosen") ? requestQuestion.getInt("chosen") : null);
		return questionMap;
	}

	private Map<String, Object> getQuestionAttribs(JSONObject requestQuestion) {
		String questionID = requestQuestion.getString("questionID");
		Item questionItem = QUESTIONS.getItem("ID", questionID);
		// Only need certain attributes
		final String[] reqAttribs = new String[]{"choices", "context", "details", "questionType", "reason", "tags"};

		Map<String, Object> outputAttribs = new HashMap<>();
		for (String attrib : reqAttribs) {
			if (attrib.equals("choices")) {
				outputAttribs.put(attrib, getAnswersOrder(requestQuestion, questionItem));
			} else outputAttribs.put(attrib, questionItem.get(attrib));
		}

		return outputAttribs;
	}

	private List<String> getAnswersOrder(JSONObject requestQuestion, Item questionItem) {
		JSONArray answerOrder = requestQuestion.getJSONArray("answerOrder");
		List<String> choices = questionItem.getList("choices");
		assert answerOrder.length() == choices.size();

		List<String> answers = new ArrayList<>();
		for (int i = 0; i < answerOrder.length(); i++) {
			answers.add(choices.get(answerOrder.getInt(i)));
		}

		return answers;
	}
}
