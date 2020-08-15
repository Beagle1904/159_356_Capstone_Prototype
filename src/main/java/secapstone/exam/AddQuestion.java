package secapstone.exam;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.*;

public class AddQuestion implements RequestHandler<Map<String, Object>, Map<String, String>> {
	@Override
	public Map<String, String> handleRequest(Map<String, Object> request, Context context) {
		Map<String, Object> questionItem;
		ArrayList<String> answerItems;
		ArrayList<String> tagItems;
		try {
			questionItem = genQuestionMap(request);
			answerItems = getArrayItems(request, "choices");
			tagItems = getArrayItems(request, "tags");
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("status", "Failed");
			errorResponse.put("message", e.getLocalizedMessage());
			return errorResponse;
		}

		// Get DynamoDB context
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClient.builder();
		builder.setRegion("ap-southeast-2");
		AmazonDynamoDB client = builder.build();
		DynamoDB dynamoDB = new DynamoDB(client);

		// Put question to questions table
		Table questionTable = dynamoDB.getTable("Questions");
		questionTable.putItem(Item.fromMap(questionItem));

		// Put answers to answers table
		Table answersTable = dynamoDB.getTable("Answers");
		for (String answer: answerItems) {
			answersTable.putItem(Item.fromMap(genAnswerMap(answer, (String) questionItem.get("ID"))));
		}

		// Put tags to tag table
		Table tagsTable = dynamoDB.getTable("Tags");
		for (String tag: tagItems) {
			tagsTable.putItem(Item.fromMap(genTagMap(tag, (String) questionItem.get("ID"))));
		}

		// Response message
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "Success");
		response.put("message", "Question added successfully");

		return response;
	}

	private Map<String, Object> genTagMap(String tag, String questionID) {
		Map<String, Object> tagMap = new HashMap<>();
		tagMap.put("ID", UUID.randomUUID().toString());
		tagMap.put("questionID", questionID);
		tagMap.put("tag", tag.toLowerCase());

		return tagMap;
	}

	private Map<String, Object> genAnswerMap(String answer, String questionID) {
		Map<String, Object> answerMap = new HashMap<>();
		answerMap.put("ID", UUID.randomUUID().toString());
		answerMap.put("questionID", questionID);
		answerMap.put("answerText", answer);

		return answerMap;
	}

	private ArrayList<String> getArrayItems(Map<String, Object> request, String param) throws Exception {
		if (!request.containsKey(param)) throw new Exception("MALFORMED REQUEST\n\tMissing param: "+param);

		return (ArrayList<String>) request.get(param);
	}

	private Map<String, Object> genQuestionMap(Map<String, Object> request) throws Exception {
		Map<String, Object> questionMap = new HashMap<>();
		final String[] params = new String[] {"context", "details", "answer", "reason", "type"};
		questionMap.put("ID", UUID.randomUUID().toString());

		for (String param: params) {
			if (!request.containsKey(param)) throw new Exception("MALFORMED REQUEST\n\tMissing param: "+param);

			questionMap.put(param, (String) request.get(param));
		}

		// Add image link if provided
		if (request.containsKey("imageLink")) questionMap.put("imageLink", request.get("imageLink"));

		return questionMap;
	}
}
