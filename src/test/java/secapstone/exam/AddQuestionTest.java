package secapstone.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.lambda.runtime.Context;

class AddQuestionTest {
	static Table questionsTable;

	@BeforeAll
	static void startUp() {
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClient.builder();
		builder.setRegion("ap-southeast-2");
		AmazonDynamoDB client = builder.build();
		DynamoDB dynamoDB = new DynamoDB(client);

		questionsTable = dynamoDB.getTable("Questions");
	}

	ArrayList<String> questionIDs;

	Context context;

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}

	@AfterEach
	void cleanUp() {
		for (String questionID : questionIDs) {
			questionsTable.deleteItem(new DeleteItemSpec().withPrimaryKey(new PrimaryKey("ID", questionID)));
		}
		questionIDs.clear();
	}

	@BeforeEach
	void setUp() {
		questionIDs = new ArrayList<>();
		context = createContext();
	}

	@Test
	void testCreateQuestion() {
		AddQuestion handler = new AddQuestion();

		Map<String, Object> input = new JSONObject(
				"{\"context\": \"Question Context\",\"details\": \"Question Details\",\"answer\": \"Correct Answer\",\"reason\": \"Dummy Reason\",\"questionType\": \"MCQ\",\"choices\": [\"Answer 1\", \"Answer 2\"],\"tags\": [\"test\"]}")
						.toMap();

		Map<String, Object> output = handler.handleRequest(input, context);
		questionIDs.add((String) output.get("newQuestionID"));
		assertEquals("Success", output.get("status"));
	}
}