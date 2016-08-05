package sec;

import java.security.Permission;

class TestSecurityManager extends SecurityManager {
  @Override
  public void checkPackageAccess(String pkg) {
    System.out.println(pkg);
    super.checkPackageAccess(pkg);
  }

  @Override
  public void checkPermission(Permission perm, Object context) {
    System.out.println(perm);
    super.checkPermission(perm, context);
  }
}
