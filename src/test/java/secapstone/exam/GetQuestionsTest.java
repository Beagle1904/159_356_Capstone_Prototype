package secapstone.exam;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GetQuestionsTest {
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
		context = createContext();
	}

	@Test
	void testGetQuestions() {
		HashMap<String, Object> request = new HashMap<>();
		request.put("questionType", "MCQ");
		new GetQuestions().handleRequest(request, context);
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}