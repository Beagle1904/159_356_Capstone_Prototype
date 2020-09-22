package secapstone.session;

import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DeleteSessionTest extends AbstractDynamoTest {
	protected DeleteSessionTest() {
		super(new String[]{}, new String[]{});
	}

	DeleteSession func = new DeleteSession();

	@Test
	void handleRequestTest() {
		// Depends on CreateSessionTest
		Map<String, Object> createInputMap = new HashMap<>();
		Map<String, Object> createContextMap = new HashMap<>();
		createContextMap.put("uzer", "Test User");
		createInputMap.put("context", createContextMap);
		String sessionID = (String) new CreateSession().handleRequest(createInputMap, context).get("sessionID");

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("sessionID", sessionID);
		func.handleRequest(inputMap, context);
	}
}
