package secapstone.exam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.tz.FixedDateTimeZone;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.StreamRecord;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Helper utilities for testing Lambda functions.
 */
public class TestUtils {

	private static class DateTimeDeserializer extends JsonDeserializer<DateTime> {

		@Override
		public DateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {

			return dateTimeFormatter.parseDateTime(parser.getText());
		}
	}

	private static class DateTimeSerializer extends JsonSerializer<DateTime> {

		@Override
		public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {

			gen.writeString(dateTimeFormatter.print(value));
		}
	}

	private static interface DynamodbEventMixin {
		static interface AttributeValueMixIn {
			@JsonProperty(B)
			public ByteBuffer getB();

			@JsonProperty(BOOL)
			public Boolean getBOOL();

			@JsonProperty(BS)
			public List<String> getBS();

			@JsonProperty(L)
			public List<Object> getL();

			@JsonProperty(M)
			public Map<String, Object> getM();

			@JsonProperty(N)
			public String getN();

			@JsonProperty(NS)
			public List<String> getNS();

			@JsonProperty(S)
			public String getS();

			@JsonProperty(SS)
			public List<String> getSS();

			@JsonProperty(NULL)
			public Boolean isNULL();

			@JsonProperty(B)
			public void setB(ByteBuffer b);

			@JsonProperty(BOOL)
			public void setBOOL(Boolean bO);

			@JsonProperty(BS)
			public void setBS(List<String> bS);

			@JsonProperty(L)
			public void setL(List<Object> val);

			@JsonProperty(M)
			public void setM(Map<String, Object> val);

			@JsonProperty(N)
			public void setN(String n);

			@JsonProperty(NS)
			public void setNS(List<String> nS);

			@JsonProperty(NULL)
			public void setNULL(Boolean nU);

			@JsonProperty(S)
			public void setS(String s);

			@JsonProperty(SS)
			public void setSS(List<String> sS);
		}

		static interface RecordMixin {
			@JsonProperty(AWS_REGION)
			public String getAwsRegion();

			@JsonProperty(DYNAMODB)
			public Object getDynamodb();

			@JsonProperty(EVENT_ID)
			public String getEventID();

			@JsonProperty(EVENT_NAME)
			public String getEventName();

			@JsonProperty(EVENT_SOURCE)
			public String getEventSource();

			@JsonProperty(EVENT_SOURCE_ARN)
			public String getEventSourceArn();

			@JsonProperty(EVENT_VERSION)
			public String getEventVersion();

			@JsonProperty(AWS_REGION)
			public void setAwsRegion(String awsRegion);

			@JsonProperty(DYNAMODB)
			public void setDynamodb(Object dynamodb);

			@JsonProperty(EVENT_ID)
			public void setEventID(String eventID);

			@JsonProperty(EVENT_NAME)
			public void setEventName(String eventName);

			@JsonProperty(EVENT_SOURCE)
			public void setEventSource(String eventSource);

			@JsonProperty(EVENT_SOURCE_ARN)
			public void setEventSourceArn(String eventSourceArn);

			@JsonProperty(EVENT_VERSION)
			public void setEventVersion(String eventVersion);
		}

		static interface StreamRecordMixin {

			@JsonProperty(APPROXIMATE_CREATION_DATE_TIME)
			public Date getApproximateCreationDateTime();

			@JsonProperty(KEYS)
			public Map<String, Object> getKeys();

			@JsonProperty(NEW_IMAGE)
			public Map<String, Object> getNewImage();

			@JsonProperty(OLD_IMAGE)
			public Map<String, Object> getOldImage();

			@JsonProperty(SEQUENCE_NUMBER)
			public String getSequenceNumber();

			@JsonProperty(SIZE_BYTES)
			public Long getSizeBytes();

			@JsonProperty(STREAM_VIEW_TYPE)
			public String getStreamViewType();

			@JsonProperty(APPROXIMATE_CREATION_DATE_TIME)
			public void setApproximateCreationDateTime(Date approximateCreationDateTime);

			@JsonProperty(KEYS)
			public void setKeys(Map<String, Object> keys);

			@JsonProperty(NEW_IMAGE)
			public void setNewImage(Map<String, Object> newImage);

			@JsonProperty(OLD_IMAGE)
			public void setOldImage(Map<String, Object> oldImage);

			@JsonProperty(SEQUENCE_NUMBER)
			public void setSequenceNumber(String sequenceNumber);

			@JsonProperty(SIZE_BYTES)
			public void setSizeBytes(Long sizeBytes);

			@JsonProperty(STREAM_VIEW_TYPE)
			public void setStreamViewType(String streamViewType);
		}

		public static final String L = "L";
		public static final String M = "M";
		public static final String BS = "BS";
		public static final String NS = "NS";
		public static final String SS = "SS";
		public static final String BOOL = "BOOL";
		public static final String NULL = "NULL";
		public static final String B = "B";
		public static final String N = "N";
		public static final String S = "S";
		public static final String OLD_IMAGE = "OldImage";
		public static final String NEW_IMAGE = "NewImage";
		public static final String STREAM_VIEW_TYPE = "StreamViewType";
		public static final String SEQUENCE_NUMBER = "SequenceNumber";
		public static final String SIZE_BYTES = "SizeBytes";
		public static final String KEYS = "Keys";
		public static final String AWS_REGION = "awsRegion";
		public static final String DYNAMODB = "dynamodb";
		public static final String EVENT_ID = "eventID";
		public static final String EVENT_NAME = "eventName";
		public static final String EVENT_SOURCE = "eventSource";

		public static final String EVENT_VERSION = "eventVersion";

		public static final String EVENT_SOURCE_ARN = "eventSourceARN";

		public static final String APPROXIMATE_CREATION_DATE_TIME = "ApproximateCreationDateTime";

		@JsonProperty(value = "Records")
		public List<?> getRecords();
	}

	private static class TestJacksonMapperModule extends SimpleModule {

		private static final long serialVersionUID = 1L;

		public TestJacksonMapperModule() {
			super("TestJacksonMapperModule");

			super.addSerializer(DateTime.class, new DateTimeSerializer());
			super.addDeserializer(DateTime.class, new DateTimeDeserializer());
		}
	}

	private static class UpperCaseRecordsPropertyNamingStrategy
			extends PropertyNamingStrategy.PropertyNamingStrategyBase {

		private static final long serialVersionUID = 1L;

		@Override
		public String translate(String propertyName) {
			if (propertyName.equals("records")) {
				return "Records";
			}
			return propertyName;
		}
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final ObjectMapper snsEventMapper = new ObjectMapper();

	private static final ObjectMapper dynamodbEventMapper = new ObjectMapper();

	static {
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.setPropertyNamingStrategy(new UpperCaseRecordsPropertyNamingStrategy());
		mapper.registerModule(new TestJacksonMapperModule());

		snsEventMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		snsEventMapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
		snsEventMapper.registerModule(new TestJacksonMapperModule());

		dynamodbEventMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		dynamodbEventMapper.setPropertyNamingStrategy(new UpperCaseRecordsPropertyNamingStrategy());
		dynamodbEventMapper.registerModule(new TestJacksonMapperModule());
		dynamodbEventMapper.addMixIn(Record.class, DynamodbEventMixin.RecordMixin.class);
		dynamodbEventMapper.addMixIn(StreamRecord.class, DynamodbEventMixin.StreamRecordMixin.class);
		dynamodbEventMapper.addMixIn(AttributeValue.class, DynamodbEventMixin.AttributeValueMixIn.class);
	}

	private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime()
			.withZone(new FixedDateTimeZone("GMT", "GMT", 0, 0));

	/**
	 * Helper method that parses a JSON object from a resource on the classpath as
	 * an instance of the provided type.
	 *
	 * @param resource
	 *            the path to the resource (relative to this class)
	 * @param clazz
	 *            the type to parse the JSON into
	 */
	public static <T> T parse(String resource, Class<T> clazz) throws IOException {

		InputStream stream = TestUtils.class.getResourceAsStream(resource);
		try {
			if (clazz == S3Event.class) {
				String json = IOUtils.toString(stream);
				S3EventNotification event = S3EventNotification.parseJson(json);

				@SuppressWarnings("unchecked")
				T result = (T) new S3Event(event.getRecords());
				return result;

			} else if (clazz == SNSEvent.class) {
				return snsEventMapper.readValue(stream, clazz);
			} else if (clazz == DynamodbEvent.class) {
				return dynamodbEventMapper.readValue(stream, clazz);
			} else {
				return mapper.readValue(stream, clazz);
			}
		} finally {
			stream.close();
		}
	}

	static String createSession(String username) {
		DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().withRegion("ap-southeast-2").build());
		Map<String, Object> sessionMap = new HashMap<String, Object>();
		String sessionToken = UUID.randomUUID().toString();
		sessionMap.put("token", sessionToken);
		sessionMap.put("username", username);
		dynamoDB.getTable("sessions").putItem(Item.fromMap(sessionMap));
		return sessionToken;
	}
}
