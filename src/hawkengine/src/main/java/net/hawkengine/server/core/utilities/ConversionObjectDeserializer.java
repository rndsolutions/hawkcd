package net.hawkengine.server.core.utilities;

import com.google.gson.*;
import net.hawkengine.model.dto.ConversionObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversionObjectDeserializer implements JsonDeserializer<ConversionObject> {
	private List<String> requiredFields;

	public ConversionObjectDeserializer() {
		this.requiredFields = new ArrayList<>(Arrays.asList("packageName", "object"));
	}

	@Override
	public ConversionObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		for (String fieldName : requiredFields) {
			if (jsonObject.get(fieldName) == null) {
				throw new JsonParseException("Required field not found");
			}
		}

		String packageName = jsonObject.get("packageName").getAsString();
		String object = jsonObject.get("object").getAsString();
		if (packageName == null || packageName.trim().length() == 0 || object == null) {
			return null;
		}

		ConversionObject result = new ConversionObject();
		result.setPackageName(packageName);
		result.setObject(object);
		return result;
	}
}
