package net.reini.cdi.se;

import java.lang.reflect.AccessibleObject;
import java.security.PrivilegedAction;

import javax.enterprise.inject.Typed;

@Typed()
public class SetAccessiblePrivilegedAction implements PrivilegedAction<Void> {
  private final AccessibleObject member;

  public SetAccessiblePrivilegedAction(AccessibleObject member) {
    this.member = member;
  }

  @Override
  public Void run() {
    member.setAccessible(true);
    return null;
  }
}
