package secapstone.session;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class DeleteSession implements RequestHandler<Map<String, Object>, String> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	@Override
	public String handleRequest(Map<String, Object> input, Context context) {
		return null;
	}
}
