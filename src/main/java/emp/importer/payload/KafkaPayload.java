package emp.importer.payload;

public class KafkaPayload {
  String ObjectID;
  String ObjectType;
  String EventType;

  public KafkaPayload(String objectID, String objectType, String eventType) {
    ObjectID = objectID;
    ObjectType = objectType;
    EventType = eventType;
  }

  public String getObjectID() {
    return ObjectID;
  }

  public void setObjectID(String objectID) {
    ObjectID = objectID;
  }

  public String getObjectType() {
    return ObjectType;
  }

  public void setObjectType(String objectType) {
    ObjectType = objectType;
  }

  public String getEventType() {
    return EventType;
  }

  public void setEventType(String eventType) {
    EventType = eventType;
  }
}
