package secapstone.exam;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserInfoTest {

	static Map<String, String> input;

	@BeforeClass
	public static void createInput() throws IOException {}

	@Test
	public void testUserInfo() {
		UserInfo handler = new UserInfo();
		Context ctx = createContext();

		Map<String, String> output = handler.handleRequest(Collections.singletonMap("userid", "100"), ctx);

		Assert.assertEquals("Bob", output.get("name"));
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("UserInfo");
		return ctx;
	}
}
