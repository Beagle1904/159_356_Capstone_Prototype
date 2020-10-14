package secapstone.exam;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractExamFunction implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	protected static final DynamoDB DYNAMO_DB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());
	protected static final Table QUESTIONS = DYNAMO_DB.getTable("Questions");
	protected static final Table USERS = DYNAMO_DB.getTable("users");
	protected static final Table RESULTS = DYNAMO_DB.getTable("results");

	protected static Item getUser(JSONObject json) {
		return USERS.getItem("username", json.getJSONObject("context").getString("uzer").toLowerCase());
	}

	// Returns an array
	protected static Object[] shuffleList(List<?> list) {
		return randomChoice(list, list.size());
	}

	// Gets a random selection of the size specified from the list provided
	protected static Object[] randomChoice(List<?> list, int numToGet) {
		Random r = new Random();
		if (numToGet > list.size()) numToGet = list.size();
		Object[] output = new Object[numToGet];

		for (int i = 0; i < numToGet; i++) {
			output[i] = list.remove(r.nextInt(list.size()));
		}

		return output;
	}

	protected static String[] toStringArray(Object[] objects) {
		String[] strings = new String[objects.length];
		for (int i = 0; i < objects.length; i++) {
			strings[i] = (String) objects[i];
		}
		return strings;
	}

	protected static Integer[] toIntArray(Object[] objects) {
		Integer[] ints = new Integer[objects.length];
		for (int i = 0; i < objects.length; i++) {
			ints[i] = (Integer) objects[i];
		}
		return ints;
	}

	protected static boolean examInProgress(Item user) {
		return user.get("exam") != null;
	}
}
