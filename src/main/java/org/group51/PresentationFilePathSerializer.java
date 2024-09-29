package org.group51;

import com.google.gson.*;

import java.lang.reflect.Type;


public class PresentationFilePathSerializer implements JsonSerializer<PresentationFilePath>, JsonDeserializer<PresentationFilePath> {
    /*
     * Our custom filepaths will have the `presentation:` prefix
     */
    @Override
    public JsonElement serialize(PresentationFilePath src, Type typeOfSrc, JsonSerializationContext context) {
        String withPrefix = "presentation:" + src.filename();
        return new JsonPrimitive(withPrefix);
    }

    @Override
    public PresentationFilePath deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String raw = jsonElement.getAsString();
        int prefixEnd = raw.indexOf(":");
        String withoutPrefix = raw.substring(prefixEnd + 1);
        return new PresentationFilePath(withoutPrefix);
    }
}
