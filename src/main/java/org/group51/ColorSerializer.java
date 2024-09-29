package org.group51;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorSerializer implements JsonDeserializer<Color>, JsonSerializer<Color> {
    /**
     * A deserializer to help the Gson library to decode fields of type java.awt.Color
     *
     * @see https://github.com/michael-andre/PaintSample/blob/master/src/com/ecp/sio/paintsample/gson/ColorDeserializer.java
     */
    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Color.decode(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getRGB());
    }
}
