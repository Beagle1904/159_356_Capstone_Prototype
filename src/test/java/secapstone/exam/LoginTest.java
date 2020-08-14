package secapstone.exam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LoginTest {

	@Test
	public void testLogin() {
		Login handler = new Login();
		Context ctx = createContext();

		Map<String, String> input = new HashMap<>();
		input.put("id", "100");
		input.put("password", "alicepass");

		Map<String, String> output = handler.handleRequest(input, ctx);

		assertEquals("true", output.get("success"));
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}
