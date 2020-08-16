package secapstone.exam;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.xspec.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.*;

public class GetQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
		// Get DynamoDB client
		AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClient.builder();
		builder.setRegion("ap-southeast-2");
		AmazonDynamoDB client = builder.build();
		Table questionsTable = new DynamoDB(client).getTable("Questions");

		Map<String, Object> result = new HashMap<>();

		String idParam = (String) request.get("ID");

		if (idParam != null) {
			Item question = questionsTable.getItem(new PrimaryKey("ID", idParam));
			result.put("question", question);
		} else {
			String typeParam = (String) request.get("questionType");
			List<String> tagsParam = (List<String>) request.get("tags");

			ScanExpressionSpec xspec = buildScanSpec(typeParam, tagsParam);
			System.out.println(xspec.getFilterExpression());
			ItemCollection<ScanOutcome> items = questionsTable.scan(xspec);
			List<Map<String, Object>> questions = new ArrayList<>();
			for (Item item: items) questions.add(item.asMap());
			result.put("questions", questions);
		}

		result.put("statusCode", 200);
		System.out.print(result);
		return result;
	}

	private ScanExpressionSpec buildScanSpec(String type, List<String> tags) { // Pain
		ExpressionSpecBuilder builder = new ExpressionSpecBuilder();
		Condition typeCondition = null;
		Condition tagsCondition = null;
		if (type!=null) {
			typeCondition = S("questionType").eq(type);
		}
		if (tags != null) {
			// Get the first tag
			tagsCondition = L("tags").contains(tags.get(0));
			// Add the rest of the tags
			if (tags.size() > 1) for (int i=1; i<tags.size(); i++) tagsCondition.or(L("tags").contains(tags.get(i)));
		}

		if (typeCondition == null) {
			builder.withCondition(tagsCondition);
		} else if (tagsCondition == null) {
			builder.withCondition(typeCondition);
			System.out.println(builder.buildForScan().getNameMap());
			System.out.println(builder.buildForScan().getValueMap());
		} else {
			builder.withCondition(typeCondition.and(tagsCondition));
		}

		return builder.buildForScan();
	}
}
