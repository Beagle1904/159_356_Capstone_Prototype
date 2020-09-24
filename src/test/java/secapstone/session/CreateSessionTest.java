package secapstone.session;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;

import java.util.HashMap;
import java.util.Map;

@Disabled
class CreateSessionTest extends AbstractDynamoTest {
	CreateSessionTest() {
		super(new String[] {"sessions"}, new String[] {"sessionToken"});
	}

	CreateSession func = new CreateSession();

	@Test
	void handleRequestTest() {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> contextMap = new HashMap<>();
		contextMap.put("uzer", "Test User");
		inputMap.put("context", contextMap);
		addItem("sessions", (String) func.handleRequest(inputMap, context).get("sessionToken"));
	}
}
