package edu.self.indy.indycloud;

import java.util.HashMap;
import java.util.Map;

public enum Role
{
  TRUSTEE("trustee"),
  AUTHOR("author"),
  ENDORSER("endorser");

  private String roleName;

  Role(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }

  @Override
  public String toString() {
    return getRoleName();
  }

  //****** Reverse Lookup Implementation************//

  //Lookup table
  private static final Map<String, Role> lookup = new HashMap<>();

  //Populate the lookup table on loading time
  static
  {
    for(Role role : Role.values())
    {
      lookup.put(role.getRoleName(), role);
    }
  }

  //This method can be used for reverse lookup purpose
  public static Role get(String roleName)
  {
    return lookup.get(roleName);
  }
}