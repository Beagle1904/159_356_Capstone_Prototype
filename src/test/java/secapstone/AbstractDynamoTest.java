package secapstone;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDynamoTest {
	static final protected DynamoDB DYNAMO_DB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());
	static final protected Table USERS = DYNAMO_DB.getTable("users");
	static final protected String TEST_USERNAME = "testUser";

	protected Context context;

	static protected Map<String, Object> genTestQuestionMap(String itemSuffix) {
		Map<String, Object> testQuestionMap = new HashMap<>();

		testQuestionMap.put("context", "Test Context" + itemSuffix);
		testQuestionMap.put("details", "Test Details" + itemSuffix);
		testQuestionMap.put("reason", "Test Reason" + itemSuffix);
		testQuestionMap.put("image", "https://cdn.pixabay.com/photo/2014/06/03/19/38/road-sign-361514_1280.png"); // Test image - free for commercial use
		testQuestionMap.put("questionType", "MCQ");
		testQuestionMap.put("choices", new String[] {"Choice 0", "Choice 1", "Choice 2"});
		testQuestionMap.put("answer", 1);
		testQuestionMap.put("tags", new String[] {"Test Tag 1", "Test Tag 2"});

		return testQuestionMap;
	}

	private static class TableItems {
		public String primaryKeyName;

		public ArrayList<String> newItems;
		TableItems(String pkn) {
			primaryKeyName = pkn;
			newItems = new ArrayList<>();
		}
	}

	private final Map<String, TableItems> tables = new HashMap<>();

	protected AbstractDynamoTest(String[] tableNames, String[] primaryKeyNames) {
		assert (tableNames.length == primaryKeyNames.length);
		for (int i=0; i<tableNames.length; i++) {
			tables.put(tableNames[i], new TableItems(primaryKeyNames[i]));
		}
	}

	protected void addItem(String tableName, String itemID) {
		assert tables.containsKey(tableName);
		tables.get(tableName).newItems.add(itemID);
	}

	protected static Map<String, Object> defaultInputMap() {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> bodyMap = new HashMap<>();
		inputMap.put("body-json", bodyMap);
		Map<String, Object> paramsMap = new HashMap<>();
		inputMap.put("params", paramsMap);
		Map<String, Object> contextMap = new HashMap<>();
		contextMap.put("uzer", "testUser");
		inputMap.put("context", contextMap);
		return inputMap;
	}

	@BeforeEach
	void createContext() {
		context = new TestContext();
		((TestContext) context).setFunctionName("Login");
	}

	@AfterEach
	void clearTables() {
		for (String tableName : tables.keySet()) {
			Table dbTable = DYNAMO_DB.getTable(tableName);
			TableItems table = tables.get(tableName);
			for (String item : table.newItems) {
				dbTable.deleteItem(new PrimaryKey(table.primaryKeyName, item));
			}
			table.newItems.clear();
		}
	}
}
