package org.sunyuyangg.service.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Util {

    private static ObjectMapper objectMapper;

    public static ObjectMapper objectMapper() {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    public static String getActionStr(String request) {
        int spaceIndex = request.indexOf(" ");
        String action;
        if (spaceIndex != -1) {
            action = request.substring(0, spaceIndex);
        } else {
            action = request;
        }
        return action.toUpperCase();
    }
}
