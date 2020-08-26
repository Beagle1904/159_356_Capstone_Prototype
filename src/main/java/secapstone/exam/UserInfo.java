package secapstone.exam;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class UserInfo implements RequestHandler<Map<String, String>, Map<String, String>> {

	private DynamoDB dynamoDB;

	public UserInfo() {
		dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion("ap-southeast-2").build());
	}

	@Override
	public Map<String, String> handleRequest(Map<String, String> input, Context context) {
		Map<String, String> response = new HashMap<String, String>();

		Item item = dynamoDB.getTable("users").getItem("username", input.get("username"));

		if (item != null) {
			response.put("name", (String) item.get("name"));
		}

		return response;
	}

}
