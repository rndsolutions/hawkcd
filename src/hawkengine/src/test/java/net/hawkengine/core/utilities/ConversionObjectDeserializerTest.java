package net.hawkengine.core.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.hawkengine.model.dto.ConversionObject;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

public class ConversionObjectDeserializerTest {
	private Gson jsonConverter;
	private ConversionObjectDeserializer deserializer;

	@Before
	public void setUp() throws Exception {
		this.jsonConverter = new Gson();
		this.deserializer = new ConversionObjectDeserializer();
	}

	@Test
	public void deserialize_WithValidJson() throws Exception {
		//Arrange
		String jsonAsString = "{\n" +
				"\"packageName\": \"testPackage\",\n" +
				"\"object\": \"testObject\"\n" +
				"}";
		JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

		ConversionObject expectedResult = new ConversionObject();
		expectedResult.setPackageName("testPackage");
		expectedResult.setObject("testObject");

		//Act
		ConversionObject actualResult = this.deserializer.deserialize(jsonElement, null, null);

		//Assert
		ReflectionAssert.assertReflectionEquals(expectedResult, actualResult);
	}

	@Test(expected = JsonParseException.class)
	public void deserialize_JsonWithMissingField() {
		//Arrange
		String jsonAsString = "{\n" +
				"\"object\": \"testObject\"\n" +
				"}";
		JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

		//Act
		this.deserializer.deserialize(jsonElement, null, null);
	}

	@Test(expected = JsonParseException.class)
	public void deserialize_JsonWithMisspelledField() {
		//Arrange
		String jsonAsString = "{\n" +
				"\"packageNameError\": \"testPackage\",\n" +
				"\"object\": \"testObject\"\n" +
				"}";
		JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

		//Act
		this.deserializer.deserialize(jsonElement, null, null);
	}
}