package co.ilife.sample.camerapreview;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import co.ilife.camerapreview.BCameraParams;
import co.ilife.camerapreview.CameraPreview;
import co.ilife.camerapreview.RecorderStateListener;

public class NormalActivity extends AppCompatActivity implements RecorderStateListener{

  private final String TAG = NormalActivity.class.getSimpleName();

  public static final int MEDIA_TYPE_VIDEO = 2;

  private BCameraParams mBCameraParams;
  private CameraPreview mCameraPreview;
  private ImageButton switchCameraButton;
  private ImageButton captureButton;
  private TextView timerText;

  private boolean isRecording = false;
  private long mStartTime = 0;
  FrameLayout mCameraContainer;
  private Point mWindowSize;


  //runs without a timer by reposting this handler at the end of the runnable
  Handler timerHandler = new Handler();
  Runnable timerRunnable = new Runnable() {

    @Override
    public void run() {
      long millis = System.currentTimeMillis() - mStartTime;
      int seconds = (int) (millis / 1000);
      int minutes = seconds / 60;
      seconds = seconds % 60;

      timerText.setText(String.format("%d:%02d", minutes, seconds));

      timerHandler.postDelayed(this, 500);
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_normal);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM,true);
     //Create our Preview view and set it as the content of our activity.
    mBCameraParams = new BCameraParams();
    mBCameraParams.setQualityProfile(BCameraParams.QUALITY_TIME_LAPSE_HIGH);

    mCameraPreview = new CameraPreview(this, null, mBCameraParams, this);

    if (mWindowSize == null) mWindowSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(mWindowSize);
    mCameraPreview.setAspectRatio(mWindowSize.x, mWindowSize.y);
    mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        //mCameraPreview.autoFocus();
        return false;
      }
    });

    mCameraContainer = (FrameLayout) findViewById(R.id.camera_preview);

    captureButton = (ImageButton) findViewById(R.id.button_capture);
    captureButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isRecording) {
          // stop recording and release camera
          mCameraPreview.stopRecording();
          // inform the user that recording has stopped
          setCaptureButtonBackground(R.mipmap.record);
          isRecording = false;
        }else {
          mCameraPreview.startRecording();
          setCaptureButtonBackground(R.mipmap.stop);
          isRecording = true;
        }
        countRecordTime(isRecording);
      }
    });

    switchCameraButton = (ImageButton) findViewById(R.id.button_switch);
    switchCameraButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int currentCamera = mCameraPreview.switchCamera();
        Log.d(TAG, "current camera id:" + currentCamera);
        mCameraContainer.removeAllViews();
        mCameraPreview.initHolder();
        mCameraPreview.prepareVideoRecorder();
        mCameraContainer.addView(mCameraPreview);
        switchCameraButton.setBackgroundResource(currentCamera==0? R.mipmap.back_camera: R.mipmap.front_camera);
      }
    });

    timerText = (TextView) findViewById(R.id.tv_timer);

  }

  private void countRecordTime(boolean isRecording) {
    if (isRecording){
      mStartTime = System.currentTimeMillis();
      timerHandler.postDelayed(timerRunnable, 0);
    }else {
      timerHandler.removeCallbacks(timerRunnable);
    }
  }

  private void setCaptureButtonBackground(int resId) {
    captureButton.setBackgroundResource(resId);
  }

  @Override public void onResume() {
    super.onResume();
    mCameraContainer.removeAllViews();
    mCameraContainer.addView(mCameraPreview);
    Log.d(TAG, "onResume: " + TAG);
    mCameraPreview.initHolder();
  }

  @Override protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause: "+TAG);
    mCameraPreview.releaseMediaRecorder();
  }
  @Override protected void onDestroy(){
    super.onDestroy();
    Log.d(TAG, "onDestroy: "+TAG);
    mCameraPreview.releaseCamera();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onCameraPrepared() {

  }

  @Override public void onRecorderPrepared() {

  }

  @Override public void onRecorderStateChanged(int code) {
    switch (code) {
      case CODE_OF_STATE_INITIALIZED:
        Log.d(TAG, "onRecorderStateChanged: initialized");
        mCameraPreview.setOutputFormat();
        break;
      case CODE_OF_STATE_OUTPUT_SET:
        Log.d(TAG, "onRecorderStateChanged: output set");
        mCameraPreview.dataSourceConfigure();
        break;
      case CODE_OF_STATE_DATASOURCE_CONFIGURED:
        Log.d(TAG, "onRecorderStateChanged: configured");
        mCameraPreview.prepare();
        break;
      case CODE_OF_STATE_PREPARED:
        Log.d(TAG, "onRecorderStateChanged: prepared");

        break;
      case CODE_OF_STATE_RECORDING:
        Log.d(TAG, "onRecorderStateChanged: recording");
        isRecording = true;
        break;
      case CODE_OF_STATE_STOP:
        Log.d(TAG, "onRecorderStateChanged: stop");
        isRecording = false;
        break;
      case CODE_OF_STATE_RELEASED:
        Log.d(TAG, "onRecorderStateChanged: released");
        break;
      case CODE_OF_STATE_RESETED:
        Log.d(TAG, "onRecorderStateChanged: reseted");
        break;
    }
  }

  @Override public void onError(int code) {
    Log.d(TAG, "onError code: " + code);
    switch (code) {
      case ERROR_CODE_OF_OPEN_CAMERA_FAILED:
        Log.d(TAG, "onError: open camera failed");
        break;
      case ERROR_CODE_OF_OPEN_MIC_FAILED:
        Log.d(TAG, "onError: open mic failed");
        break;
      case ERROR_CODE_OF_SET_AUDIO_VIDEO_SOURCE_AFTER_SET_OUTPUT:
        Log.d(TAG, "onError: set audio video source after set ouput");
        break;
      case ERROR_CODE_CONFIG_DATASOURCE_AFTER_PREPARED_BEFORE_OUTPUT:
        Log.d(TAG, "onError: config datasource after prepared before output");
        break;
      case ERROR_CODE_OF_PREPARE_RECORD_FAILED:
        Log.d(TAG, "onError: prepared record failed");
        break;
      case ERROR_CODE_OF_START_BEFORE_PREPARE:
        Log.d(TAG, "onError: start before prepare");
        break;
    }
  }
}
