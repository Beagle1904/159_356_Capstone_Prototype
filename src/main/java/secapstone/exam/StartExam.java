package secapstone.exam;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.xspec.Condition;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.L;

public class StartExam extends AbstractExamFunction {
	private static final Table QUESTIONS = DYNAMO_DB.getTable("Questions");

	// Returns an array
	protected static Object[] shuffleList(List<?> list) {
		return randomChoice(list, list.size());
	}

	// Gets a random selection of the size specified from the list provided
	private static Object[] randomChoice(List<?> list, int numToGet) {
		Random r = new Random();
		if (numToGet > list.size()) numToGet = list.size();
		Object[] output = new Object[numToGet];

		for (int i = 0; i < numToGet; i++) {
			output[i] = list.remove(r.nextInt(list.size()));
		}

		return output;
	}

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

	private static String[] toStringArray(Object[] objects) {
		String[] strings = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			strings[i] = (String) objects[i];
		}
		return strings;
	}

	private static Integer[] toIntArray(Object[] objects) {
		Integer[] ints = new Integer[objects.length];
		for (int i = 0; i < objects.length; i++) {
			ints[i] = (Integer) objects[i];
		}
		return ints;
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
		String username = user.getString("username");

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
				questions = getQuestions(inputJSON.getJSONObject("body-json"));
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

	private boolean examInProgress(Item user) {
		return user.get("exam") != null;
	}

	private Question[] getQuestions(JSONObject inputJSON) {
		List<Question> questionsList = new ArrayList<>();
		for (String tagName : inputJSON.getJSONObject("questions").keySet()) {
			for (Question question : getTagQuestions(tagName, inputJSON.getJSONObject("questions").getInt(tagName))) {
				if (!questionsList.contains(question)) questionsList.add(question);
			}
		}
		return toQuestionArray(shuffleList(questionsList));
	}

	private List<Question> getTagQuestions(String tagName, int numQuestions) {
		ScanExpressionSpec scanSpec = getScanSpec(tagName);
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

	private ScanExpressionSpec getScanSpec(String tagName) {
		ExpressionSpecBuilder builder = new ExpressionSpecBuilder();

		Condition listContainsCondition = L("tags").contains(tagName);
		builder.withCondition(listContainsCondition);
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
