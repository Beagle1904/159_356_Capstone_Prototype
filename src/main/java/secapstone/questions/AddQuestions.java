package secapstone.questions;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class AddQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	private static final String[] REQUIRED_PARAMS = {"context", "details", "reason", "questionType", "choices", "answer", "tags"};

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> inputJSON, Context context) {
		// Convert input to JSON object
		JSONObject input = new JSONObject(inputJSON);

		// Get the user requesting
		String user = input.getJSONObject("context").getString("uzer");
		// Get the user's permission level
		String perms = dynamoDB.getTable("users").getItem("username", user).getString("role");

		// Get questions array
		JSONArray questions = input.getJSONArray("questions");

		// Get current time
		String now = LocalDateTime.now(ZoneId.of("UTC")).toString();

		// Check required parameters exist for all questions in the input
		checkParams(questions);

		// Create ArrayList of question JSONObjects
		List<JSONObject> questionObjects = new ArrayList<>();
		for (int i = 0; i < questions.length(); i++) questionObjects.add(questions.getJSONObject(i));

		// Generate database items
		ArrayList<Map<String, Object>> newQuestions = new ArrayList<>();
		List<String> newIDs = new ArrayList<>();
		for (JSONObject question : questionObjects) {
			// Generate question ID
			String newQuestionID = UUID.randomUUID().toString();
			Map<String, Object> dbQuestionObject = question.toMap();
			dbQuestionObject.put("ID", newQuestionID);
			dbQuestionObject.put("timeCreated", now);
			dbQuestionObject.put("timeUpdated", now);

			// Change question type based on user
			switch (perms) {
				case "TEST_USER":
					dbQuestionObject.put("state", "TEST");
					break;
				case "ADMINISTRATOR":
				case "EXAMINER":
					dbQuestionObject.put("state", "ACTIVE");
					break;
				case "SUBMITTER":
					dbQuestionObject.put("state", "PENDING");
					break;
				default:
					throw new Error("Invalid Credentials");
			}

			dbQuestionObject.put("addedBy", user);

			newQuestions.add(dbQuestionObject);
			newIDs.add(newQuestionID);
		}

		// Add items to database
		Table questionsTable = dynamoDB.getTable("Questions");
		for (Map<String, Object> question: newQuestions) {
			questionsTable.putItem(Item.fromMap(question));
		}

		Map<String, Object> output = new HashMap<>();
		output.put("IDs", newIDs);

		return output;
	}

	private void checkParams(JSONArray questionsJSONArray) {
		List<JSONObject> questions = new ArrayList<>();
		for (int i=0; i<questionsJSONArray.length(); i++) questions.add(questionsJSONArray.getJSONObject(i));
		for (JSONObject question : questions) {
			for (String param : REQUIRED_PARAMS) {
				if (!question.keySet().contains(param)) {
					throw new Error("Missing parameter: " + param);
				}
			}
		}
	}
}
