package secapstone.session;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import secapstone.AbstractDynamoTest;
import secapstone.exam.TestContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateSessionTest extends AbstractDynamoTest {
	CreateSessionTest() {
		super(new String[] {"sessions"}, new String[] {"sessionToken"});
	}

	CreateSession func;
	@BeforeEach
	void createFunc() {
		func = new CreateSession();
	}

	@Test
	void handleRequestTest() {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> contextMap = new HashMap<>();
		contextMap.put("uzer", "Test User");
		inputMap.put("context", contextMap);
		addItem("sessions", (String) func.handleRequest(inputMap, context).get("sessionID"));
	}
}