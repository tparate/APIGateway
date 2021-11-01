package net.tuparate.gateway.jwt;

public class UserCustomClaims {
  private String userName;
  private String firstName;
  private String lastName;
  private String city;
  
  

  public UserCustomClaims(String userName, String firstName, String lastName, String city) {
    super();
    this.userName = userName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.city = city;
  }
  
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
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
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  
  @Override
  public String toString() {
    return "UserCustomClaims [userName=" + userName + ", firstName=" + firstName + ", lastName="
        + lastName + ", city=" + city + "]";
  }
}
