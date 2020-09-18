package secapstone.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LoginTest {

//	@Test
//	public void testLogin() {
//		Login handler = new Login();
//		Context ctx = createContext();
//
//		Map<String, String> input = new HashMap<>();
//		input.put("username", "alice");
//		input.put("password", "alicepass");
//
//		Map<String, String> output = handler.handleRequest(input, ctx);
//
//		assertEquals("true", output.get("success"));
//		assertNotNull(output.get("sessionToken"));
//	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}
