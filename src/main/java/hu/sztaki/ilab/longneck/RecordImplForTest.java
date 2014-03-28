package hu.sztaki.ilab.longneck;

/**
*
* @author Geszler Döme <gdome@ilab.sztaki.hu>
*/

public class RecordImplForTest extends RecordImpl implements Record {

  public enum Role {
    SOURCE, TARGET, ERROR
  };

  private Role role;

  public Role getRole() {
    return role;
  }

  public String getRoleStr() {
    return role.toString();
  }
  public void setRole(String roleStr) {
    if (roleStr.equals("source")) {
      role = Role.SOURCE;
    } else if (roleStr.equals("target")) {
      role = Role.TARGET;
    } else if (roleStr.equals("error-target")) {
      role = Role.ERROR;
    } else {
      throw new RuntimeException();
    }
  }
}
