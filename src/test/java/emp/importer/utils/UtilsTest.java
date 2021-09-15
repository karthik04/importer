package emp.importer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import emp.importer.payload.Employee;
import emp.importer.payload.KafkaPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static emp.importer.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

  @Test
  void testSerializeKafkaPayload() {
    String response = serializeKafkaPayload(
      new KafkaPayload("test", "test", "test", "test"));
    assertEquals("{\"objectID\":\"test\",\"objectType\":\"test\",\"eventType\":\"test\",\"newPayload\":\"test\"}", response);
  }

  @Test
  void testDeserializeEmployee() throws JsonProcessingException {
    Employee emp = deserializeEmployee("{\"employeeId\":\"eid14\",\"firstName\":\"eid11\",\"lastName\":\"eid1\",\"email\":\"eid14\",\"location\":\"eid1\"}");
    assertEquals("eid14", emp.getEmployeeId());
  }

  @Test
  void testDeserializeEmpList() throws JsonProcessingException {
    List<Employee> response = deserializeEmpList("[]");
    assertEquals(0, response.size());
  }
}
