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
import co.ilife.camerapreview.BCamera;
import co.ilife.camerapreview.CameraPreview;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WechatActivity extends AppCompatActivity {

  private final String TAG = WechatActivity.class.getSimpleName();

  private BCamera mBCamera;

  private final int MAX_PROGRESS = 8000;
  private final int MINIMUM_PROGRESS = 3000;
  private ImageButton mRecordButton;
  private RoundCornerProgressBar leftProgress, rightProgress;
  private long mStartTime, mDuration;
  private boolean isRecordFinished = false;
  float Y = 0;

  private FrameLayout mCameraContainer;
  private CameraPreview mCameraPreview;
  private Point mWindowSize;
  private View mProgress;

  Handler mHandler = new Handler();
  Runnable run = new Runnable() {
    @Override public void run() {
      mDuration = (System.currentTimeMillis() - mStartTime);
      leftProgress.setProgress((int)mDuration);
      rightProgress.setProgress((int)mDuration);
      resizeProgressView(mDuration);
      if (mDuration<MAX_PROGRESS){
        mHandler.postDelayed(this, 0);
      }else {
        //mCameraPreview.stopReocrd();
        //mCameraPreview.releaseMediaRecorder();
        //mCameraPreview.unlockCamera();
        resetProgress();
        isRecordFinished = true;
        recordFinish();
      }
    }
  };

  private void resizeProgressView(long duration) {
    Log.d(TAG, "resizeProgressView: "+(MAX_PROGRESS - duration)*100/MAX_PROGRESS*mWindowSize.x/100);
    int width = (int) (MAX_PROGRESS - duration)*100/MAX_PROGRESS*mWindowSize.x/100;
    //Log.d(TAG, "resizeProgressView: " + width);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, mProgress.getHeight());
    params.gravity = Gravity.CENTER_HORIZONTAL;
    mProgress.setLayoutParams(params);
  }

  private void recordFinish() {
    mHandler.removeCallbacks(run);
    String url = mCameraPreview.getCurrentFileUrl();
    Intent intent = new Intent();
    intent.putExtra(MainActivity.VIDEO_URL, url);
    Log.d(TAG, "run: "+ url);
    setResult(RESULT_OK, intent);
    mCameraPreview.stopReocrd();
    finish();
  }

  private void resetProgress() {
    leftProgress.setProgress(0);
    rightProgress.setProgress(0);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_wechat);
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    hide();

    mProgress = (View) findViewById(R.id.progress);

    if (mWindowSize == null)
      mWindowSize = new Point();
    getWindowManager().getDefaultDisplay().getSize(mWindowSize);

    mBCamera = new BCamera(this);
    mBCamera.setCamera(true);
    mBCamera.setQualityProfile(BCamera.QUALITY_480P);

    mCameraPreview = new CameraPreview(this,null,mBCamera);

    mCameraPreview.setAspectRatio(mWindowSize.x, mWindowSize.y);
    mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        mCameraPreview.autoFocus();
        return false;
      }
    });
    mCameraContainer = (FrameLayout) findViewById(R.id.camera_preview);

    mRecordButton = (ImageButton) findViewById(R.id.record_button);
    mRecordButton.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            Y = event.getY();
            if (!isRecordFinished) {
              event.setLocation(0,0);
              mStartTime = System.currentTimeMillis();
              mCameraPreview.prepareVideoRecorder();
              mCameraPreview.startRecord();
              mHandler.post(run);
            }
            break;
          case MotionEvent.ACTION_UP:
            mHandler.removeCallbacks(run);
            if (mDuration > MINIMUM_PROGRESS) {
              recordFinish();
              break;
            }
            if (!isRecordFinished){
              mCameraPreview.stopReocrd();
            }
            isRecordFinished = false;
            resetProgress();
            break;
          case MotionEvent.ACTION_MOVE:
            if (event.getY() - Y < -300) {
              Y = 0;
              mHandler.removeCallbacks(run);
              isRecordFinished = true;
              if (!isRecordFinished){
                mCameraPreview.stopReocrd();
              }
              resetProgress();
            }
            break;
        }
        return false;
      }
    });

    leftProgress = (RoundCornerProgressBar) findViewById(R.id.left_progress);
    rightProgress = (RoundCornerProgressBar) findViewById(R.id.right_progress);
    leftProgress.setMax(MAX_PROGRESS);
    rightProgress.setMax(MAX_PROGRESS);
  }

  @Override public void onResume() {
    super.onResume();
    mCameraContainer.removeAllViews();
    mCameraContainer.addView(mCameraPreview);
    Log.d(TAG, "onResume: "+TAG);
    mCameraPreview.initHolder();
  }

  @Override public void onPause() {
    super.onPause();
    mCameraPreview.releaseMediaRecorder();
  }

  private void hide() {
    // Hide UI first
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }
  }

}
