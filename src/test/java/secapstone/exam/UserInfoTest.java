package secapstone.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class UserInfoTest {



	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("UserInfo");
		return ctx;
	}
}
