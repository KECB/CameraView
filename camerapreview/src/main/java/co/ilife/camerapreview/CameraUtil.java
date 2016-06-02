package co.ilife.camerapreview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  /** Create a File for saving an video */
  public static File getOutputMediaFile(Context context, @Nullable String path, String extension){
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.
    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return null;
    if (path == null) {
      path = context.getExternalCacheDir().getAbsolutePath();
    }
    File mediaStorageDir = new File(path);
    // Create the storage directory if it does not exist
    if (! mediaStorageDir.exists()){
      if (! mediaStorageDir.mkdirs()){
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }
    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "VID_"+ timeStamp + extension);
    return mediaFile;
  }

  public static List<Camera.Size> supportPreviewSizes(Camera camera) {
    return camera.getParameters().getSupportedPreviewSizes();
  }
}
