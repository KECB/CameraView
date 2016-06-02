package co.ilife.camerapreview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import java.util.List;

/**
 * Created by KECB on 6/2/16.
 */

public class CameraUtil {

  public static boolean hasCamera(@NonNull Context context) {
    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
        || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public static boolean hasCamera2(@NonNull Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;
    CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    try {
      String[] idList = cameraManager.getCameraIdList();
      if (idList.length == 0) {
        return false;
      } else {
        for (String str : idList) {
          if (str == null || str.trim().isEmpty()) {
            return false;
          }
          CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
          final int supportLevel =
              cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
          if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return false;
          }
        }
      }
      return true;
    } catch (CameraAccessException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Camera.Size> supportPreviewSizes(Camera camera) {
    return camera.getParameters().getSupportedPreviewSizes();
  }
}
