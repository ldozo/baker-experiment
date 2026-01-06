package app.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONing {

    private static ObjectMapper _mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static String toString(Object cust) {
        try {
            return _mapper.writeValueAsString(cust);
        } catch (JsonProcessingException e) {
            System.err.println(e);
        }
        return null;
    }
}
