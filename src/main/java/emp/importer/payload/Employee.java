package emp.importer.payload;

public class Employee {
  private String employeeId;
  private String firstName;
  private String lastName;
  private String email;
  private String location;

  public Employee() {
    super();
  }

  public Employee(String employeeId, String firstName, String lastName, String email, String location) {
    this.employeeId = employeeId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.location = location;
  }

  public String getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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
