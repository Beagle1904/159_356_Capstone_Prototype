package secapstone.exam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AddQuestion implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private DynamoDB dynamoDB;

	public AddQuestion() {
		dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion("ap-southeast-2").build());
	}

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
		Map<String, Object> questionItem;
		try {
			questionItem = genQuestionMap(request);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("statusCode", 400);
			errorResponse.put("status", "Failed");
			errorResponse.put("message", e.getLocalizedMessage());
			return errorResponse;
		}

		// Put question to questions table
		Table questionTable = dynamoDB.getTable("Questions");
		questionTable.putItem(Item.fromMap(questionItem));

		// Response message
		Map<String, Object> response = new HashMap<>();
		response.put("statusCode", 200);
		response.put("status", "Success");
		response.put("message", "Question added successfully");
		response.put("newQuestionID", questionItem.get("ID"));

		return response;
	}

	private Map<String, Object> genQuestionMap(Map<String, Object> request) throws Exception {
		Map<String, Object> questionMap = new HashMap<>();
		final String[] params = new String[] { "context", "details", "answer", "reason", "questionType", "choices",
				"tags" };
		questionMap.put("ID", UUID.randomUUID().toString());

		for (String param : params) {
			if (!request.containsKey(param)) {
				throw new Exception("MALFORMED REQUEST\n\tMissing param: " + param);
			}

			questionMap.put(param, request.get(param));
		}

		// Add image link if provided
		if (request.containsKey("imageLink")) {
			questionMap.put("imageLink", request.get("imageLink"));
		}

		return questionMap;
	}
}
