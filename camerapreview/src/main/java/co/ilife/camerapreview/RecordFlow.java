package co.ilife.camerapreview;

import android.media.MediaRecorder;
import android.view.Surface;
import java.io.FileDescriptor;

/**
 * Based on https://developer.android.com/reference/android/media/MediaRecorder.html
 * Create a record flow, initial -> initialized -> dataSourceConfigure -> dataSourceConfigured -> prepare -> prepared -> start -> recording -> stop/reset
 * Created by KECB on 6/3/16.
 */

public interface RecordFlow{
  /**
   * Set 
   * {@link MediaRecorder#setAudioSource(int)}
   * {@link MediaRecorder#setVideoSource(int)}
   */
  public void initial();

  /**
   * Set
   * {@link MediaRecorder#setOutputFormat(int)}, but this only can set once or it will cause illegal exception.
   */
  public void dataSourceConfigure();

  /**
   * Set
   * {@link MediaRecorder#setAudioEncoder(int)}
   * {@link MediaRecorder#setVideoEncoder(int)}
   * {@link MediaRecorder#setOutputFile(FileDescriptor)}
   * {@link MediaRecorder#setVideoSize(int, int)}
   * {@link MediaRecorder#setVideoFrameRate(int)}
   * {@link MediaRecorder#setPreviewDisplay(Surface)}
   */
  public void dataSourceConfigured() throws IllegalStateException;

  /**
   * Just {@link MediaRecorder#prepare()} for now
   */
  public void prepare();

  /**
   * Just after {@link MediaRecorder#start()} for now
   */
  public void recording();

  /**
   * Just {@link MediaRecorder#release()} for now
   */
  public void release();

  /**
   * Handle errors and then {@link MediaRecorder#reset()}
   */
  public void error();
}
