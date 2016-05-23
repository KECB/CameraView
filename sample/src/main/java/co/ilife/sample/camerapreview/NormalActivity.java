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
import co.ilife.camerapreview.CameraPreview;

public class NormalActivity extends AppCompatActivity {

  private final String TAG = NormalActivity.class.getSimpleName();

  public static final int MEDIA_TYPE_VIDEO = 2;

  private CameraPreview mPreview;
  private ImageButton switchCameraButton;
  private ImageButton captureButton;
  private TextView timerText;

  private boolean isRecording = false;
  private long mStartTime = 0;
  FrameLayout preview;
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
    mPreview = new CameraPreview(this,null);
    if (mWindowSize == null)
      mWindowSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(mWindowSize);
    mPreview.setAspectRatio(mWindowSize.x, mWindowSize.y);
    mPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        mPreview.autoFocus();
        return false;
      }
    });
    preview = (FrameLayout) findViewById(R.id.camera_preview);
    preview.addView(mPreview);
    //mPreview = (CameraPreview) findViewById(R.id.camera_preview);

    captureButton = (ImageButton) findViewById(R.id.button_capture);
    captureButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isRecording) {
          // stop recording and release camera
          mPreview.stopReocrd();      // stop the recording
          mPreview.releaseMediaRecorder();     // release the MediaRecorder object
          mPreview.lockCamera();             // take camera access back from MediaRecorder

          // inform the user that recording has stopped
          setCaptureButtonBackground(R.mipmap.record);
          isRecording = false;
        }else {
          // initialize video camera
          if (mPreview.prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mPreview.startRecord();

            // inform the user that recording has started
            setCaptureButtonBackground(R.mipmap.stop);
            isRecording = true;
          } else {
            // prepare didn't work, release the camera
            mPreview.releaseMediaRecorder();
            // inofrm user
          }
        }
        countRecordTime(isRecording);
      }
    });

    switchCameraButton = (ImageButton) findViewById(R.id.button_switch);
    switchCameraButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        int currentCamera = mPreview.switchCamera();
        Log.d(TAG, "current camera id:" + currentCamera);
        preview.removeAllViews();
        mPreview.initHolder();
        mPreview.prepareVideoRecorder();
        preview.addView(mPreview);
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
    preview.removeAllViews();
    preview.addView(mPreview);
    Log.d(TAG, "onResume: "+TAG);
    mPreview.initHolder();
    //mPreview.prepareVideoRecorder(0);
  }

  @Override protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause: "+TAG);

    mPreview.releaseMediaRecorder();
    mPreview.releaseCamera();
    mPreview.destoryHolder();
  }
  @Override protected void onDestroy(){
    super.onDestroy();
    Log.d(TAG, "onDestroy: "+TAG);
    mPreview.releaseMediaRecorder();
    mPreview.releaseCamera();
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
}
