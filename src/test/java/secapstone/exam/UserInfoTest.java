package secapstone.exam;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserInfoTest {

	@Test
	public void testUserInfo() {
		UserInfo handler = new UserInfo();
		Context ctx = createContext();

		Map<String, String> output = handler.handleRequest(Collections.singletonMap("userid", "100"), ctx);

		assertEquals("Alice", output.get("name"));
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("UserInfo");
		return ctx;
	}
}
