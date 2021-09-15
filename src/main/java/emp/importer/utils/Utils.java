package emp.importer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import emp.importer.payload.Employee;
import emp.importer.payload.KafkaPayload;

import java.util.Arrays;
import java.util.List;


public class Utils {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  public static Employee deserializeEmployee(String jsonString) throws JsonProcessingException {
    return new ObjectMapper().readValue(jsonString, Employee.class);
  }

  public static List<Employee> deserializeEmpList(String jsonString) throws JsonProcessingException {
    return Arrays.asList(new ObjectMapper().readValue(jsonString, Employee[].class));
  }

  public static String serializeKafkaPayload(KafkaPayload obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
