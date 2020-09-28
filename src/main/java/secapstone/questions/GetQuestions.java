package secapstone.questions;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.Condition;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.L;

public class GetQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	private final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		Map<String, Object> output = new HashMap<>();
		Table questionsTable = dynamoDB.getTable("Questions");

		ArrayList<Object> questionsArray = new ArrayList<>();
		if (input.get("ID") != null) questionsArray.add(questionsTable.getItem("ID", input.get("ID")).asMap());
		else {
			ScanExpressionSpec xSpec = buildScanSpec(input);
		}

		output.put("questions", questionsArray);
		return output;
	}

	private ScanExpressionSpec buildScanSpec(Map<String, Object> input) {
		ExpressionSpecBuilder builder = new ExpressionSpecBuilder();
		Condition stateCondition = getListCondition(input, "state", false);
		Condition tagsCondition = getListCondition(input, "tags", true);

		if (stateCondition == null) {
			builder.withCondition(tagsCondition);
		} else if (tagsCondition == null) {
			builder.withCondition(stateCondition);
		} else {
			builder.withCondition(stateCondition.and(tagsCondition));
		}

		return builder.buildForScan();
	}

	private Condition getListCondition(Map<String, Object> input, String name, boolean exclusive) {
		Condition condition = null;
		String[] items = (String[]) input.get(name);
		if (items != null) {
			// Get the first tag
			condition = L(name).contains(items[0]);
			// Add the rest of the items
			if (items.length > 1) {
				for (int i = 1; i < items.length; i++) {
					if (exclusive) condition.or(L(name).contains(items[i]));
					else condition.and(L(name).contains(items[i]));
				}
			}
		}

		return condition;
	}
}
