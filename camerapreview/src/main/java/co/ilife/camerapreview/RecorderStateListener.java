package co.ilife.camerapreview;

/**
 * Created by KECB on 6/8/16.
 */

public interface RecorderStateListener {

  public static final int CODE_OF_OPERATION_SUCCEED = 1001;
  public static final int CODE_OF_OPERATION_FAILED = 1002;
  public static final int ERROR_CODE_OF_OPEN_CAMERA_FAILED = 2001;
  public static final int ERROR_CODE_OF_OPEN_MIC_FAILED = 2002;
  public static final int ERROR_CODE_OF_PREPARE_RECORD_FAILED = 2003;
  public static final int ERROR_CODE_OF_SET_AUDIO_VIDEO_SOURCE_AFTER_SET_OUTPUT = 2004;


  public static final int CODE_OF_STATE_INITIALIZED = 9001;
  public static final int CODE_OF_STATE_OUTPUT_SET = 9002;
  public static final int CODE_OF_STATE_DATASOURCE_CONFIGURED = 9003;
  public static final int CODE_OF_STATE_PREPARED = 9004;
  public static final int CODE_OF_STATE_RECORDING = 9005;
  public static final int CODE_OF_STATE_RELEASED = 9006;
  public static final int CODE_OF_STATE_RESETED = 9007;
  public static final int CODE_OF_STATE_STOP = 9008;

  public static final int ERROR_CODE_SET_OUTPUT = 3001;
  public static final int ERROR_CODE_CONFIG_DATASOURCE_AFTER_PREPARED_BEFORE_OUTPUT = 3002;
  public static final int ERROR_CODE_OF_PREPARE_AFTER_START_BEFORE_OUTPUT = 3003;
  public static final int ERROR_CODE_OF_START_BEFORE_PREPARE = 3004;
  public static final int ERROR_CODE_OF_RELEASE = 3005;

  public void onCameraPrepared();

  public void onRecorderPrepared();

  public void onRecorderStateChanged(int code);

  public void onError(int code);

}
