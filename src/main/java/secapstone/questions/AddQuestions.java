package secapstone.questions;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	private static final String[] REQUIRED_PARAMS = {"context", "details", "reason", "questionType", "choices", "answer", "tags"};
	private static final String[] OPTIONAL_PARAMS = {"image"};

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		ArrayList<Map<String, Object>> questions = (ArrayList<Map<String, Object>>) input.get("questions");

		// Check required parameters exist for all questions in the input
		checkParams(questions);

		// Generate database items
		ArrayList<Map<String, Object>> newQuestions = new ArrayList<>();
		ArrayList<String> newIDs = new ArrayList<>();
		for (Map<String, Object> question: questions) {
			// Generate question ID
			String newQuestionID = UUID.randomUUID().toString();
			Map<String, Object> dbQuestionObject = new HashMap<>(question);
			dbQuestionObject.put("ID", newQuestionID);
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

	private void checkParams(ArrayList<Map<String, Object>> questions) {
		for (Map<String, Object> question : questions) {
			for (String param : REQUIRED_PARAMS) {
				if (!question.containsKey(param)) {
					throw new Error("Missing parameter: " + param);
				}
			}
		}
	}
}
