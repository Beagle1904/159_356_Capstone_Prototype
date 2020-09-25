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
	static final protected DynamoDB DYNAMO_DB  = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	protected Context context;

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
		Map<String, Object> contextMap = new HashMap<>();
		contextMap.put("uzer", "Test User");
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
