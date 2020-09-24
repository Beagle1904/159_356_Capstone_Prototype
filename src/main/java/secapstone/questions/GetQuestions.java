package secapstone.questions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class GetQuestions implements RequestHandler<Map<String, Object>, Map<String, Object>> {
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> stringObjectMap, Context context) {
		return null;
	}
}
