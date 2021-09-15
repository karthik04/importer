package emp.importer.payload;

public class KafkaPayload {
  String objectID;
  String objectType;
  String eventType;
  Object newPayload;

  public KafkaPayload(String objectID, String objectType, String eventType, Object newPayload) {
    this.objectID = objectID;
    this.objectType = objectType;
    this.eventType = eventType;
    this.newPayload = newPayload;
  }

  public String getObjectID() {
    return objectID;
  }

  public void setObjectID(String objectID) {
    this.objectID = objectID;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Object getNewPayload() {
    return newPayload;
  }

  public void setNewPayload(Object newPayload) {
    this.newPayload = newPayload;
  }
}
