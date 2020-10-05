package secapstone;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * A simple mock implementation of the {@code Context} interface. Default values
 * are stubbed out, and setters are provided so you can customize the context
 * before passing it to your function.
 */
public class TestContext implements Context {

	/**
	 * A simple {@code LambdaLogger} that prints everything to stderr.
	 */
	private static class TestLogger implements LambdaLogger {

		@Override
		public void log(byte[] message) {
			System.err.println(message);
		}

		@Override
		public void log(String message) {
			System.err.println(message);
		}
	}

	private String awsRequestId = "EXAMPLE";
	private ClientContext clientContext;
	private String functionName = "EXAMPLE";
	private CognitoIdentity identity;
	private String logGroupName = "EXAMPLE";
	private String logStreamName = "EXAMPLE";
	private LambdaLogger logger = new TestLogger();
	private int memoryLimitInMB = 128;
	private int remainingTimeInMillis = 15000;
	private String functionVersion = "EXAMPLE";

	private String invokedFunctionArn = "EXAMPLE";

	@Override
	public String getAwsRequestId() {
		return awsRequestId;
	}

	@Override
	public ClientContext getClientContext() {
		return clientContext;
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public String getFunctionVersion() {
		return functionVersion;
	}

	@Override
	public CognitoIdentity getIdentity() {
		return identity;
	}

	@Override
	public String getInvokedFunctionArn() {
		return invokedFunctionArn;
	}

	@Override
	public LambdaLogger getLogger() {
		return logger;
	}

	@Override
	public String getLogGroupName() {
		return logGroupName;
	}

	@Override
	public String getLogStreamName() {
		return logStreamName;
	}

	@Override
	public int getMemoryLimitInMB() {
		return memoryLimitInMB;
	}

	@Override
	public int getRemainingTimeInMillis() {
		return remainingTimeInMillis;
	}

	public void setAwsRequestId(String value) {
		awsRequestId = value;
	}

	public void setClientContext(ClientContext value) {
		clientContext = value;
	}

	public void setFunctionName(String value) {
		functionName = value;
	}

	public void setFunctionVersion(String value) {
		functionVersion = value;
	}

	public void setIdentity(CognitoIdentity value) {
		identity = value;
	}

	public void setInvokedFunctionArn(String value) {
		invokedFunctionArn = value;
	}

	public void setLogger(LambdaLogger value) {
		logger = value;
	}

	public void setLogGroupName(String value) {
		logGroupName = value;
	}

	public void setLogStreamName(String value) {
		logStreamName = value;
	}

	public void setMemoryLimitInMB(int value) {
		memoryLimitInMB = value;
	}

	public void setRemainingTimeInMillis(int value) {
		remainingTimeInMillis = value;
	}
}
