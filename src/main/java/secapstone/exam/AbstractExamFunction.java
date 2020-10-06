package secapstone.exam;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONObject;

import java.util.Map;

public abstract class AbstractExamFunction implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	protected static final DynamoDB DYNAMO_DB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());
	protected static final Table USERS = DYNAMO_DB.getTable("users");

	protected static Item getUser(JSONObject json) {
		return USERS.getItem("username", json.getJSONObject("context").getString("uzer"));
	}
}
