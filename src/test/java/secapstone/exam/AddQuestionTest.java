package secapstone.exam;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddQuestionTest {
	ArrayList<String> questionIDs;
	Context context;
	static Table questionsTable;

	@BeforeAll
	static void startUp() {
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClient.builder();
		builder.setRegion("ap-southeast-2");
		AmazonDynamoDB client = builder.build();
		DynamoDB dynamoDB = new DynamoDB(client);

		questionsTable = dynamoDB.getTable("Questions");
	}

	@BeforeEach
	void setUp() {
		questionIDs = new ArrayList<>();
		context = createContext();
	}

	@Test
	void testCreateQuestion() {
		AddQuestion handler = new AddQuestion();

		Map<String, Object> input = new JSONObject("{\"context\": \"Question Context\",\"details\": \"Question Details\",\"answer\": \"Correct Answer\",\"reason\": \"Dummy Reason\",\"type\": \"MCQ\",\"choices\": [\"Answer 1\", \"Answer 2\"],\"tags\": [\"tag1\", \"tag2\"]}").toMap();

		Map<String, Object> output = handler.handleRequest(input, context);
		questionIDs.add((String) output.get("newQuestionID"));
		assertEquals("Success", output.get("status"));
	}

	@AfterEach
	void cleanUp() {
		for (String questionID: questionIDs) {
			questionsTable.deleteItem(new DeleteItemSpec().withPrimaryKey(new PrimaryKey("ID", questionID)));
		}
		questionIDs.clear();
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}