package secapstone.session;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateSession implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

		Map<String, Object> result = new HashMap<>();
		Map ctx = (Map) input.get("context");
		Object user = ctx.get("uzer");

		String sessionID = makeNewSession(user);

		result.put("username", user);
		result.put("sessionToken", sessionID);

		return result;
	}

	private String makeNewSession(Object user) {
		Map<String, Object> sessionMap = new HashMap<>();

		String sessionID = UUID.randomUUID().toString();
		sessionMap.put("sessionToken", sessionID);

		String now = LocalDateTime.now(ZoneId.of("UTC")).toString();
		sessionMap.put("timeCreated", now);
		sessionMap.put("lastAccessed", now);

		sessionMap.put("user", user);

		dynamoDB.getTable("sessions").putItem(Item.fromMap(sessionMap));

		return sessionID;
	}
}
