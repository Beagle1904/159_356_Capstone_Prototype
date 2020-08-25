package secapstone.exam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class UserInfo implements RequestHandler<Map<String, String>, Map<String, String>> {

	@Override
	public Map<String, String> handleRequest(Map<String, String> input, Context context) {
		Map<String, String> response = new HashMap<String, String>();

		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClient.builder();
		builder.setRegion("ap-southeast-2");
		AmazonDynamoDB dynamoDB = builder.build();
		GetItemResult dbResult = dynamoDB.getItem("users",
				Collections.singletonMap("username", new AttributeValue(input.get("username"))));

		if (dbResult.getItem() != null) {
			response.put("name", dbResult.getItem().get("name").getS());
		}

		return response;
	}

}
