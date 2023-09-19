package util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapperWithIndentation = initWithIndentation();
    private static final ObjectMapper objectMapperWithoutIndentation = initWithoutIndentation();
    private static final ObjectMapper objectMapperStrict = initStrict();

    private JsonUtil() {
    }

    public static <T> T readValue(URL src, Class<T> valueType) throws IOException {
        try (InputStream inputStream = src.openStream()) {
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return objectMapperWithoutIndentation.readValue(reader, valueType);
        }
    }

    public static <T> T toObject(String value, Class<T> valueType) throws IOException {
        return objectMapperWithoutIndentation.readValue(value, valueType);
    }

    public static <T> T toObjectStrict(String value, Class<T> valueType) throws IOException {
        return objectMapperStrict.readValue(value, valueType);
    }

    public static <T> String toJSON(T valueType) {
        return toJSON(objectMapperWithIndentation, valueType);
    }

    public static <T> String toCompactJSON(T valueType) {
        return toJSON(objectMapperWithoutIndentation, valueType);
    }

    /**
     * Useful for testing. Performs a round trip via a JSON string allowing ser/deser to be tested
     * andv erified.
     *
     * <p>Note that this deliberately uses a very strict {@link ObjectMapper} since we need to be sure
     * that the source object is fully recreated without errors.
     *
     * @param <T>       The object type
     * @param valueType The object to be converted
     * @return A copy of the object performed via JSON.
     * @throws IOException If there are deserialization issues.
     */
    @SuppressWarnings("unchecked")
    public static <T> T viaJSON(T valueType) throws IOException {
        String json = toJSON(objectMapperStrict, valueType);
        log.debug("Converted " + valueType + " to " + json);
        return toObjectStrict(json, (Class<T>) valueType.getClass());
    }

    private static <T> String toJSON(ObjectMapper objectMapper, T valueType) {
        try {
            return objectMapper.writeValueAsString(valueType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Problem serializing " + valueType.getClass();
        }
    }

    private static ObjectMapper initWithIndentation() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static ObjectMapper initWithoutIndentation() {
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static ObjectMapper initStrict() {
        return new ObjectMapper();
    }

}
