package co.ilife.sample.camerapreview;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.ilife.camerapreview.BCameraParams;
import co.ilife.camerapreview.CameraPreview;
import co.ilife.camerapreview.RecorderStateListener;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WechatActivity extends AppCompatActivity implements RecorderStateListener {

  private final String TAG = WechatActivity.class.getSimpleName();

  private BCameraParams mBCameraParams;

  private final int MAX_PROGRESS = 8000;
  private final int MINIMUM_PROGRESS = 3000;
  private ImageButton mRecordButton;
  private long mStartTime, mDuration;
  private boolean isRecording = false;
  private boolean isReachCancelMark = false;
  private boolean isFinished = false;
  float Y = 0;

  private FrameLayout mCameraContainer;
  private CameraPreview mCameraPreview;
  private Point mWindowSize;
  private View mProgress;
  private TextView swipeStopTV, looseStopTV;

  Handler mHandler = new Handler();
  Runnable run = new Runnable() {
    @Override public void run() {
      mDuration = (System.currentTimeMillis() - mStartTime);
      resizeProgressView(mDuration);
      if (mDuration < MAX_PROGRESS) {
        mHandler.postDelayed(this, 0);
      } else {
        //mCameraPreview.stopReocrd();
        //mCameraPreview.releaseMediaRecorder();
        //mCameraPreview.unlockCamera();
        recordFinish();
      }
    }
  };

  private void resizeProgressView(long duration) {
    //Log.d(TAG, "resizeProgressView: "+(MAX_PROGRESS - duration)*100/MAX_PROGRESS*mWindowSize.x/100);
    int width = (int) (MAX_PROGRESS - duration) * 100 / MAX_PROGRESS * mWindowSize.x / 100;
    //Log.d(TAG, "resizeProgressView: " + width);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, mProgress.getHeight());
    params.gravity = Gravity.CENTER_HORIZONTAL;
    mProgress.setLayoutParams(params);
    if (isReachCancelMark) {
      mProgress.setBackgroundColor(getResources().getColor(R.color.red));
      //控制textview
      swipeStopTV.setVisibility(View.INVISIBLE);
      looseStopTV.setVisibility(View.VISIBLE);
    } else {
      mProgress.setBackgroundColor(getResources().getColor(R.color.green));
      //控制textview
      swipeStopTV.setVisibility(View.VISIBLE);
      looseStopTV.setVisibility(View.INVISIBLE);
    }
    if (duration == 0) {
      mProgress.setBackgroundColor(getResources().getColor(R.color.green));
      swipeStopTV.setVisibility(View.INVISIBLE);
      looseStopTV.setVisibility(View.INVISIBLE);
    }
  }

  private void recordFinish() {
    mHandler.removeCallbacks(run);
    stopRecord();
    //Log.d(TAG, "recordFinish: "+ mDuration);
    if (mDuration > MINIMUM_PROGRESS && isReachCancelMark == false || mDuration >= MAX_PROGRESS) {
      String url = mCameraPreview.getCurrentFileUrl();
      Intent intent = new Intent();
      intent.putExtra(MainActivity.VIDEO_URL, url);
      Log.d(TAG, "run: " + url);
      setResult(RESULT_OK, intent);
      finish();
      isFinished = true;
      mCameraPreview.releaseMediaRecorder();
      mCameraPreview.releaseCamera();
      return;
    }else {
      mCameraPreview.initial();
    }

  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_wechat);
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    hide();

    initView();
  }

  private void initView() {
    mProgress = (View) findViewById(R.id.progress);

    if (mWindowSize == null) mWindowSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(mWindowSize);

    mBCameraParams = new BCameraParams();
    mBCameraParams.setQualityProfile(BCameraParams.QUALITY_480P);
    mBCameraParams.setVideoFrameRate(30);
    mBCameraParams.setVideoEncodingBitRate(1 * 1024 * 1024);

    mCameraPreview = new CameraPreview(this, null, mBCameraParams, this);

    mCameraPreview.setAspectRatio(mWindowSize.x, mWindowSize.y);
    mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        //mCameraPreview.autoFocus();
        return false;
      }
    });

    mCameraContainer = (FrameLayout) findViewById(R.id.camera_preview);

    mRecordButton = (ImageButton) findViewById(R.id.record_button);
    mRecordButton.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            swipeStopTV.setVisibility(View.VISIBLE);
            isReachCancelMark = false;
            Y = event.getY();
            event.setLocation(0, 0);
            mStartTime = System.currentTimeMillis();
            mCameraPreview.startRecording();
            mHandler.post(run);
            break;
          case MotionEvent.ACTION_UP:
            if (isFinished) return false;
            recordFinish();
            break;
          case MotionEvent.ACTION_MOVE:
            if (isFinished) return false;
            if (event.getY() - Y < -300) {
              Y = 0;
              isReachCancelMark = true;
              looseStopTV.setVisibility(View.VISIBLE);
              mProgress.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
              isReachCancelMark = false;
              looseStopTV.setVisibility(View.INVISIBLE);
              mProgress.setBackgroundColor(getResources().getColor(R.color.green));
            }
            break;
        }
        return false;
      }
    });

    swipeStopTV = (TextView) findViewById(R.id.swipe_stop_tv);
    looseStopTV = (TextView) findViewById(R.id.loose_stop_tv);

    swipeStopTV.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: swipstop");
        return false;
      }
    });
    
    looseStopTV.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: loose Stop");
        return false;
      }
    });
  }

  private void stopRecord() {
    // 当小于8秒,上滑则取消录制
    // 小于3秒,取消录制
    resizeProgressView(0);
    mCameraPreview.stopRecording();
  }

  @Override public void onResume() {
    super.onResume();
    mCameraContainer.removeAllViews();
    mCameraContainer.addView(mCameraPreview);
    Log.d(TAG, "onResume: " + TAG);
    mCameraPreview.initHolder();
  }

  @Override public void onPause() {
    super.onPause();
    if (isFinished) return;
    mCameraPreview.releaseMediaRecorder();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (isFinished) return;
    mCameraPreview.releaseCamera();
  }

  private void hide() {
    // Hide UI first
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
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
