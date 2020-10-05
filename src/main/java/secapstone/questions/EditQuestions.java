package secapstone.questions;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class EditQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		// Convert input to JSON object
		JSONObject inputJSON = new JSONObject((Map) input.get("body-json"));

		Table table = dynamoDB.getTable("Questions");

		JSONObject changes = inputJSON.getJSONObject("changes");
		UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("ID", inputJSON.getString("ID"));

		for (String change : changes.keySet()) {
			updateItemSpec.addAttributeUpdate(new AttributeUpdate(change).put(changes.get(change)));
		}

		String now = LocalDateTime.now(ZoneId.of("UTC")).toString();
		updateItemSpec.addAttributeUpdate(new AttributeUpdate("timeUpdated").put(now));

		table.updateItem(updateItemSpec);
		return null;
	}
}
