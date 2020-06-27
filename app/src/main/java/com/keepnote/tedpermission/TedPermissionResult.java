package com.keepnote.tedpermission;




import com.keepnote.tedpermission.util.ObjectUtils;

import java.util.List;

class TedPermissionResult {

  private final boolean granted;
  private final List<String> deniedPermissions;

  public TedPermissionResult(List<String> deniedPermissions) {
    this.granted = ObjectUtils.isEmpty(deniedPermissions);
    this.deniedPermissions = deniedPermissions;
  }

  public boolean isGranted() {
    return granted;
  }

  public List<String> getDeniedPermissions() {
    return deniedPermissions;
  }
}
