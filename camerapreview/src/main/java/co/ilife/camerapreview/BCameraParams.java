package co.ilife.camerapreview;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Media recorder settings
 * Created by KECB on 5/16/16.
 */
public class BCameraParams {

  @IntDef({
      QUALITY_HIGH, QUALITY_LOW, QUALITY_480P, QUALITY_720P, QUALITY_1080P, QUALITY_TIME_LAPSE_HIGH,
      QUALITY_TIME_LAPSE_LOW, QUALITY_HIGH_SPEED_HIGH
  }) @Retention(RetentionPolicy.SOURCE) public @interface QualityProfile {

  }

  public static final int QUALITY_HIGH = CamcorderProfile.QUALITY_HIGH;
  public static final int QUALITY_LOW = CamcorderProfile.QUALITY_LOW;
  public static final int QUALITY_480P = CamcorderProfile.QUALITY_480P;
  public static final int QUALITY_720P = CamcorderProfile.QUALITY_720P;
  public static final int QUALITY_1080P = CamcorderProfile.QUALITY_1080P;
  public static final int QUALITY_TIME_LAPSE_LOW  = CamcorderProfile.QUALITY_TIME_LAPSE_LOW;
  public static final int QUALITY_TIME_LAPSE_HIGH = CamcorderProfile.QUALITY_TIME_LAPSE_HIGH;
  public static final int QUALITY_HIGH_SPEED_HIGH = CamcorderProfile.QUALITY_HIGH_SPEED_480P;

  public static final int STATUS_RECORDED = 1;


  private String mSavePath;
  private boolean mCameraFacingBack = true;

  private long mMaxRecordLength = -1;
  private int mVideoFrameRate = -1;
  private int mVideoEncodingBitRate = -1;
  private int mQualityProfile = -1;

  private int mAudioSource = MediaRecorder.AudioSource.CAMCORDER;
  private int mVideoSource = MediaRecorder.VideoSource.DEFAULT;
  private int mOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
  private int mPreferredWidth = -1;
  private int mPreferredHeight = -1;
  private int mVideoEncoder = MediaRecorder.VideoEncoder.H264;
  private int mAudioEncoder = MediaRecorder.AudioEncoder.AAC;// for support iOS device to play.

  public BCameraParams() {
  }

  //public Camera getCamera() {
  //  return mCamera;
  //}
  //
  //public void setCamera(@Nullable boolean cameraFacingBack) {
  //  // TODO: 6/3/16 判断Camera是否被锁
  //  if (mCamera == null) {
  //    mCamera = mCameraFacingBack ? Camera.open() : Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
  //    return;
  //  }
  //  if (mCameraFacingBack != cameraFacingBack) {
  //    mCamera.release();
  //    setCameraFacingBack(cameraFacingBack);
  //    mCamera = mCameraFacingBack ? Camera.open() : Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
  //  }
  //
  //}

  public String getSavePath() {
    return mSavePath;
  }

  public void setSavePath(String savePath) {
    mSavePath = savePath;
  }

  public boolean isCameraFacingBack() {
    return mCameraFacingBack;
  }

  public void setCameraFacingBack(boolean cameraFacingBack) {
    mCameraFacingBack = cameraFacingBack;
  }

  public long getMaxRecordLength() {
    return mMaxRecordLength;
  }

  public void setMaxRecordLength(long maxRecordLength) {
    mMaxRecordLength = maxRecordLength;
  }

  public int getVideoFrameRate() {
    return mVideoFrameRate == -1 ? getQualityProfile().videoFrameRate : mVideoFrameRate;
  }

  public void setVideoFrameRate(int videoFrameRate) {
    mVideoFrameRate = videoFrameRate;
  }

  public int getVideoEncodingBitRate() {
    return mVideoEncodingBitRate==-1? getQualityProfile().videoBitRate : mVideoEncodingBitRate;
  }

  public void setVideoEncodingBitRate(int videoEncodingBitRate) {
    mVideoEncodingBitRate = videoEncodingBitRate;
  }

  public CamcorderProfile getQualityProfile() {
    return CamcorderProfile.get(mQualityProfile);
  }

  public void setQualityProfile(@QualityProfile int qualityProfile) {
    mQualityProfile = qualityProfile;
  }

  public int getAudioSource() {
    return mAudioSource;
  }

  public void setAudioSource(int audioSource) {
    mAudioSource = audioSource;
  }

  public int getVideoSource() {
    return mVideoSource;
  }

  public void setVideoSource(int videoSource) {
    mVideoSource = videoSource;
  }

  public int getOutputFormat() {
    return mOutputFormat;
  }

  public void setOutputFormat(int outputFormat) {
    mOutputFormat = outputFormat;
  }

  public int getPreferredWidth() {
    return mPreferredWidth;
  }

  public void setPreferredWidth(int preferredWidth) {
    mPreferredWidth = preferredWidth;
  }

  public int getPreferredHeight() {
    return mPreferredHeight;
  }

  public void setPreferredHeight(int preferredHeight) {
    mPreferredHeight = preferredHeight;
  }

  public int getVideoEncoder() {
    return mVideoEncoder;
  }

  public void setVideoEncoder(int videoEncoder) {
    mVideoEncoder = videoEncoder;
  }

  public int getAudioEncoder() {
    return mAudioEncoder;
  }

  public void setAudioEncoder(int audioEncoder) {
    mAudioEncoder = audioEncoder;
  }
}
