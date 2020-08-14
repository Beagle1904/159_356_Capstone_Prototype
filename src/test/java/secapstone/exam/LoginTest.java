package secapstone.exam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LoginTest {

	@BeforeClass
	public static void createInput() throws IOException {

	}

	@Test
	public void testLogin() {
		Login handler = new Login();
		Context ctx = createContext();

		Map<String, String> input = new HashMap<String, String>();
		input.put("id", "100");
		input.put("password", "alicepass");

		Map<String, String> output = handler.handleRequest(input, ctx);

		Assert.assertEquals("true", output.get("success"));
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Login");
		return ctx;
	}
}
