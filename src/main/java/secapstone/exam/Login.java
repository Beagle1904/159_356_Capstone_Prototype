package secapstone.exam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Login implements RequestHandler<Map<String, String>, Map<String, String>> {

	private DynamoDB dynamoDB;

	public Login() {
		dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion("ap-southeast-2").build());
	}

	@Override
	public Map<String, String> handleRequest(Map<String, String> input, Context context) {
		Map<String, String> response = new HashMap<String, String>();

		Item item = dynamoDB.getTable("users").getItem("username", input.get("username"));
		if (item != null && item.get("password").equals(input.get("password"))) {

			// Create new session.
			Map<String, Object> sessionMap = new HashMap<String, Object>();
			String sessionToken = UUID.randomUUID().toString();
			sessionMap.put("token", sessionToken);
			sessionMap.put("username", item.get("username"));
			dynamoDB.getTable("sessions").putItem(Item.fromMap(sessionMap));

			// On success return session token.
			response.put("success", "true");
			response.put("sessionToken", sessionToken);
		} else {
			// Return failure.
			response.put("success", "false");
		}

		return response;
	}

}
