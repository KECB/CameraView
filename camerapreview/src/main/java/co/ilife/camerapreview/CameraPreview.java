package co.ilife.camerapreview;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import java.io.IOException;
import java.util.List;

/**
 * Created by KECB on 12/30/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

  /**
   * For log
   */
  private final String TAG = CameraPreview.class.getSimpleName();

  private Context mContext;
  /**
   * Hold the camera preview
   */
  private SurfaceHolder mHolder;

  private BCamera mBCamera;
  private Camera mCamera;
  private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
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

  private int mRatioWidth = 0;
  private int mRatioHeight = 0;
  /**
   * Capture type for storage, current only support video type
   */
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;
  private String currentRecordVideoFileUrl = "";

  public CameraPreview(Context context, @Nullable AttributeSet attrs, BCamera bCamera) {
    super(context, attrs);
    mContext = context;
    mBCamera = bCamera;

    mSupportedSizes = CameraUtil.supportPreviewSizes(mBCamera.getCamera());
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

  /**
   * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
   * calculated from the parameters.
   *
   * @param width  Relative horizontal size
   * @param height Relative vertical size
   */
  public void setAspectRatio(int width, int height) {
    mRatioWidth = width;
    mRatioHeight = height;
    requestLayout();
  }

  public void initHolder() {
    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);
    Log.d(TAG, "initHolder: "+ mHolder.getSurface().toString());
    // deprecated setting, but required on Android versions prior to 3.0
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void destoryHolder(){
    // Destroy previuos Holder
    surfaceDestroyed(mHolder);
  }

  public void setCameraDisplayOrientation(int cameraId, Camera camera) {
    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    Activity activity = (Activity) mContext;
    int rotation = activity.getWindowManager().getDefaultDisplay()
        .getRotation();
    int degrees = 0;
    switch (rotation) {
      case Surface.ROTATION_0: degrees = 0; break;
      case Surface.ROTATION_90: degrees = 90; break;
      case Surface.ROTATION_180: degrees = 180; break;
      case Surface.ROTATION_270: degrees = 270; break;
    }
    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      result = (info.orientation + degrees) % 360;
      result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
  }

  public boolean prepareVideoRecorder() {

    mMediaRecorder = new MediaRecorder();

    // Step 1: Unlock and set camera to MediaRecorder
    mBCamera.getCamera().unlock();
    mMediaRecorder.setCamera(mBCamera.getCamera());
    mMediaRecorder.setOrientationHint(90); // Make output file orientation portrait

    // Step 2: Set sources
    mMediaRecorder.setAudioSource(mBCamera.getAudioSource());
    mMediaRecorder.setVideoSource(mBCamera.getVideoSource());

    mMediaRecorder.setOutputFormat(mBCamera.getOutputFormat());
    mMediaRecorder.setVideoSize(supportedWidth,supportedHeight);
    mMediaRecorder.setVideoFrameRate(mBCamera.getQualityProfile().videoFrameRate);
    mMediaRecorder.setVideoEncodingBitRate(mBCamera.getQualityProfile().videoBitRate); // Set this to make video more clarity
    mMediaRecorder.setVideoEncoder(mBCamera.getQualityProfile().videoCodec);
    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// for support iOS device to play.

    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher
    mMediaRecorder.setProfile(mBCamera.getQualityProfile());

    // Step 4: Set ouput file
    currentRecordVideoFileUrl = CameraUtil.getOutputMediaFile(mContext,mBCamera.getSavePath(),".mp4").getPath();
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
      mCamera.stopPreview();
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

  public String getCurrentRecordVideoFileUrl() {
    return currentRecordVideoFileUrl;
  }

  public void autoFocus(){
    //mCamera.cancelAutoFocus();
    mCamera.autoFocus(new Camera.AutoFocusCallback() {
      @Override public void onAutoFocus(boolean success, Camera camera) {
        if (!success) Log.d(TAG, "onAutoFocus: Failed");
      }
    });
  }

  public int switchCamera(){
    //mHolder = null;
    mCamera.stopPreview();
    mCamera.release();
    mCurrentCameraId = mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
    Log.d(TAG, "switchCamera: "+ mCurrentCameraId);
    initHolder();
    mBCamera.setCamera(mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK);
    mCamera = mBCamera.getCamera();
    return mCurrentCameraId;
  }

  public String getCurrentFileUrl() {
    return currentRecordVideoFileUrl;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    // Set
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    params.gravity = Gravity.CENTER;
    setLayoutParams(params);
    //
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    if (0 == mRatioWidth || 0 == mRatioHeight) {
      setMeasuredDimension(width, height);
    } else {
      if (width < height * mRatioWidth / mRatioHeight) {
        setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
      } else {
        setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
      }
    }
  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    Log.d(TAG, "surfaceCreated: ");
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.d(TAG, "surfaceChanged: ");
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      setCameraDisplayOrientation(mCurrentCameraId, mCamera);
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();
      mCamera.cancelAutoFocus();
      mCamera.autoFocus(new Camera.AutoFocusCallback() {
        @Override public void onAutoFocus(boolean success, Camera camera) {

        }
      });
    } catch (IOException e) {
      Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }

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
      setCameraDisplayOrientation(mCurrentCameraId, mCamera);
      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();
    } catch (Exception e) {
      Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {
    Log.d(TAG, "surfaceDestroyed: ");
    // empty. Take care of releasing the Camera preview in your activity.
    mHolder.removeCallback(this);
  }
}
