package secapstone.exam;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LoginCognito implements RequestHandler<Map<String, String>, Map<String, String>> {

	private DynamoDB dynamoDB;

	public LoginCognito() {
		dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build());
	}

	@Override
	public Map<String, String> handleRequest(Map<String, String> input, Context context) {
		Map<String, String> output = new HashMap<String, String>();

		String authCode = input.get("authCode");
		String accessToken = getAccessToken(authCode, context);

		// Create new session.
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		String sessionToken = UUID.randomUUID().toString();
		sessionMap.put("sessionToken", sessionToken);
		sessionMap.put("accessToken", accessToken);
		dynamoDB.getTable("sessions").putItem(Item.fromMap(sessionMap));

		output.put("sessionToken", sessionToken);
		return output;
	}

	/**
	 * Exchanges an authorization code for an access token at the TOKEN HTTP
	 * endpoint.
	 * 
	 * @param authCode
	 *            The authorization code.
	 * @return The access token.
	 */
	private String getAccessToken(String authCode, Context context) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {

			HttpPost request = new HttpPost("https://mockexam.auth.ap-southeast-2.amazoncognito.com/oauth2/token");
			request.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(
					"4clnkbs0d41m678c79c55hjjo6:1nkk4jk0mjo50jdjiq1dg9mk74teq71sq67ner73m6j0172e1j9a".getBytes()));
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			params.add(new BasicNameValuePair("client_id", "4clnkbs0d41m678c79c55hjjo6"));
			params.add(new BasicNameValuePair("redirect_uri",
					"https://master.d2fodol9zy2g3c.amplifyapp.com/cognito.html"));
			params.add(new BasicNameValuePair("code", authCode));
			request.setEntity(new UrlEncodedFormEntity(params));

			context.getLogger().log(request.toString());
			try (CloseableHttpResponse response = client.execute(request)) {
				String responseBody = EntityUtils.toString(response.getEntity());
				context.getLogger().log("Response Body: " + responseBody);
				JSONObject tokens = new JSONObject(responseBody);
				return tokens.getString("access_token");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
