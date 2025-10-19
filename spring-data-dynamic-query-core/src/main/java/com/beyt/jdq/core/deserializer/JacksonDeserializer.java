package com.beyt.jdq.core.deserializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class JacksonDeserializer implements IDeserializer {

    public static final ObjectMapper mapper;

    static {
        mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <T> T deserialize(Object value, Class<T> clazz) throws Exception {
        if (value.getClass().isAssignableFrom(clazz)) {
            return (T) value;
        }

        if (clazz.isEnum()) {
            return (T) Enum.valueOf((Class<Enum>) clazz, value.toString());
        }
        return mapper.readValue(value.toString(), clazz);
    }
}
