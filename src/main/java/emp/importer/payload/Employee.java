package emp.importer.payload;

public class Employee {
  private String employeeId;
  private String name;
  private String email;
  private String location;

  public Employee() {
    super();
  }
  public Employee(String employeeId, String name, String email, String location) {
    this.employeeId = employeeId;
    this.name = name;
    this.email = email;
    this.location = location;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
