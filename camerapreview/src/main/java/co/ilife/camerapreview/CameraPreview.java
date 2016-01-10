package co.ilife.camerapreview;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by KECB on 12/30/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  /**
   * For log
   */
  private final String TAG = CameraPreview.class.getSimpleName();

  /**
   * Hold the camera preview
   */
  private SurfaceHolder mHolder;

  private Camera mCamera;

  /**
   * Recorder video
   */
  private MediaRecorder mMediaRecorder;

  /**
   * supported preview sizes in current devices camera.
   */
  private List<Camera.Size> mSupportedSizes;
  private int supportedWidth;
  private int supportedHeight;

  /**
   * Capture type for storage, current only support video type
   */
  public static final int MEDIA_TYPE_VIDEO = 2;

  private String currentRecordVideoFileUrl = "";

  //public CameraPreview(Context context) {
  //  super(context);
  //  mCamera = getCameraInstance();
  //
  //  // Install a SurfaceHolder.Callback so we get notified when the
  //  // underlying surface is created and destroyed.
  //  mHolder = getHolder();
  //  mHolder.addCallback(this);
  //
  //  // deprecated setting, but required on Android versions prior to 3.0
  //  mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  //
  //  mSupportedSizes = mCamera.getParameters().getSupportedPreviewSizes();
  //  for (Camera.Size size : mSupportedSizes) {
  //    Log.d(TAG, "size width:" + size.width + "; size height:" + size.height);
  //  }
  //}

  public CameraPreview(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mCamera = getCameraInstance();

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);

    // deprecated setting, but required on Android versions prior to 3.0
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    mSupportedSizes = mCamera.getParameters().getSupportedPreviewSizes();
    for (Camera.Size size : mSupportedSizes) {
      if (640 <= size.width & size.width <= 1280) {
        supportedWidth = size.width;
        supportedHeight = size.height;
        //parameters.setPreviewSize(size.width, size.height);
        //parameters.setPictureSize(size.width, size.height);
        break;
      }
      Log.d(TAG, "size width:" + size.width + "; size height:" + size.height);
    }
  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      mCamera.setDisplayOrientation(90);
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // If your preview can change or rotate, take care of those events here.
    // Make sure to stop the preview before resizing or reformating it.

    if (mHolder.getSurface() == null) {
      // preview surface does not exist
      return;
    }

    // stop preview before making changes
    try {
      mCamera.stopPreview();
    } catch (Exception e) {
      // ignore: tried to stop a non-existent preview
    }

    // set preview size and make any resize, rotate or
    // reformatting changes here

    // start preview with new settings
    try {
      if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
        mCamera.setDisplayOrientation(90);
      }else {
        mCamera.setDisplayOrientation(0);
      }
      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();
    } catch (Exception e) {
      Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    // empty. Take care of releasing the Camera preview in your activity.
  }

  /** A safe way to get an instance of the Camera object. */
  public static Camera getCameraInstance() {
    Camera c = null;
    try {
      c = Camera.open(); // attempt to get a Camera instance
    } catch (Exception e) {
      // Camera is not available (in use or does not exist)
    }
    return c; // returns null if camera is unavailable
  }

  public boolean prepareVideoRecorder() {

    if (mCamera == null) {
      mCamera = getCameraInstance();
    }
    mMediaRecorder = new MediaRecorder();

    // Step 1: Unlock and set camera to MediaRecorder
    mCamera.unlock();
    mMediaRecorder.setCamera(mCamera);
    mMediaRecorder.setOrientationHint(90); // Make output file orientation portrait

    // Step 2: Set sources
    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    mMediaRecorder.setVideoSize(supportedWidth,supportedHeight);
    mMediaRecorder.setVideoFrameRate(30);
    mMediaRecorder.setVideoEncodingBitRate(
        1 * 1024 * 1024); // Set this to make video more clarity
    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher
    //CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
    //mMediaRecorder.setProfile(profile);

    // Step 4: Set ouput file
    currentRecordVideoFileUrl = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
    mMediaRecorder.setOutputFile(currentRecordVideoFileUrl);

    // Step 5: Set the preview output
    mMediaRecorder.setPreviewDisplay(getHolder().getSurface());

    // Step 6: Prepare configured MediaRecorder
    try {
      mMediaRecorder.prepare();
    } catch (IllegalStateException e) {
      Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
      releaseMediaRecorder();
      return false;
    } catch (IOException e) {
      Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
      releaseMediaRecorder();
      return false;
    }
    return true;
  }

  public void releaseMediaRecorder() {
    if (mMediaRecorder != null) {
      mMediaRecorder.reset();   // clear recorder configuration
      mMediaRecorder.release(); // release the recorder object
      mMediaRecorder = null;
      mCamera.lock();           // lock camera for later use
    }
  }

  public void releaseCamera() {
    if (mCamera != null) {
      mCamera.release();        // release the camera for other applications
      mCamera = null;
    }
  }

  public void lockCamera() {
    mCamera.lock();
  }

  public void unlockCamera() {
    mCamera.unlock();
  }

  public void startRecord() {
    mMediaRecorder.start();
  }

  public void stopReocrd() {
    mMediaRecorder.stop();
  }

  /** Create a file Uri for saving an image or video */
  private static Uri getOutputMediaFileUri(int type){
    return Uri.fromFile(getOutputMediaFile(type));
  }

  /** Create a File for saving an image or video */
  private static File getOutputMediaFile(int type){
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES), "MyCameraApp");
    //File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
    //    + File.separator
    //    + getContext().getPackageName()
    //    + File.separator);
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (! mediaStorageDir.exists()){
      if (! mediaStorageDir.mkdirs()){
        Log.d("MyCameraApp", "failed to create directory");
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File mediaFile;
    if(type == MEDIA_TYPE_VIDEO) {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
          "VID_"+ timeStamp + ".mp4");
    } else {
      return null;
    }

    return mediaFile;
  }

  public String getCurrentRecordVideoFileUrl() {
    return currentRecordVideoFileUrl;
  }
}
