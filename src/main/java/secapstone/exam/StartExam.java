package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.xspec.Condition;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.L;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

public class StartExam extends AbstractExamFunction {

	private static Map<String, Object> genExamMap(String examType, Question[] questions) {
		Map<String, Object> examMap = new HashMap<>();
		examMap.put("type", examType);
		JSONArray questionsJSONArray = new JSONArray();
		for (Question q : questions) {
			questionsJSONArray.put(q.getExamQuestionMap());
		}
		examMap.put("questions", questionsJSONArray.toList());
		return examMap;
	}

	private static Question[] toQuestionArray(Object[] objects) {
		Question[] questions = new Question[objects.length];
		for (int i = 0; i < objects.length; i++) {
			questions[i] = (Question) objects[i];
		}
		return questions;
	}

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		JSONObject inputJSON = new JSONObject(input);
		Item user = getUser(inputJSON);
		String username;
		try {
			username = user.getString("username");
		} catch (Exception e) {
			throw new Error(inputJSON.toString());
		}

		// Check if an exam is in progress
		if (examInProgress(user)) {
			throw new Error("Exam already in progress");
		}

		// Check the type of exam
		String examType = inputJSON.getJSONObject("body-json").getString("type");

		// Get questions for the exam
		Question[] questions;
		switch (examType) {
			case "PRACTICE":
				questions = getQuestions(inputJSON.getJSONObject("body-json"), user.getString("role"));
				break;
			case "":
				throw new Error("No exam type provided");
			default:
				throw new Error("Unsupported exam type");
		}
		// Create the exam object
		Map<String, Object> examMap = genExamMap(examType, questions);

		// Push the exam object to the database
		UpdateItemSpec updateSpec = new UpdateItemSpec().withPrimaryKey("username", username)
				.addAttributeUpdate(new AttributeUpdate("exam").put(examMap));
		USERS.updateItem(updateSpec);

		return null;
	}

	private Question[] getQuestions(JSONObject inputJSON, String userRole) {
		List<Question> questionsList = new ArrayList<>();
		for (String tagName : inputJSON.getJSONObject("questions").keySet()) {
			for (Question question : getTagQuestions(tagName, inputJSON.getJSONObject("questions").getInt(tagName), userRole)) {
				if (!questionsList.contains(question)) questionsList.add(question);
			}
		}

		return toQuestionArray(shuffleList(questionsList));
	}

	private List<Question> getTagQuestions(String tagName, int numQuestions, String userRole) {
		ScanExpressionSpec scanSpec = getScanSpec(tagName, userRole);
		ItemCollection<ScanOutcome> items = QUESTIONS.scan(scanSpec);
		List<String> questionIDs = new ArrayList<>();
		for (Item item : items) {
			questionIDs.add(item.getString("ID"));
		}

		String[] chosenQuestions = toStringArray(randomChoice(questionIDs, numQuestions));
		List<Question> questions = new ArrayList<>();
		for (String questionID : chosenQuestions) {
			Item item = QUESTIONS.getItem("ID", questionID);
			questions.add(new Question(questionID, item.getList("choices").size()));
		}

		return questions;
	}

	private ScanExpressionSpec getScanSpec(String tagName, String userRole) {
		ExpressionSpecBuilder builder = new ExpressionSpecBuilder();

		if (!tagName.equals("")) {
			Condition listContainsCondition = L("tags").contains(tagName);
			builder.withCondition(listContainsCondition);
		}

		Condition stateCondition = userRole.equals("TEST_USER") ? S("state").eq("TEST") : S("state").eq("ACTIVE");
		builder.withCondition(stateCondition);

		builder.addProjections("ID");

		return builder.buildForScan();
	}

	private static class Question {
		String id;
		Integer[] answerOrder;

		Question(String id, int numAnswers) {
			this.id = id;

			// Create an ArrayList of integers
			ArrayList<Integer> ints = new ArrayList<>(numAnswers);
			for (int i = 0; i < numAnswers; i++) {
				ints.add(i);
			}

			// Randomize the order of integers into a new array
			answerOrder = toIntArray(shuffleList(ints));
		}

		@Override
		public boolean equals(Object obj) {
			return obj.getClass() == Question.class && this.id.equals(((Question) obj).id);
		}

		Map<String, Object> getExamQuestionMap() {
			Map<String, Object> output = new HashMap<>();

			output.put("questionID", id);
			output.put("answerOrder", answerOrder);

			return output;
		}
	}
}
