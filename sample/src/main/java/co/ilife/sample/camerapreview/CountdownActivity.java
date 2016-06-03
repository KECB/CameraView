package co.ilife.sample.camerapreview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import co.ilife.camerapreview.BCamera;
import co.ilife.camerapreview.CameraPreview;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class CountdownActivity extends AppCompatActivity {

  private final String TAG = CountdownActivity.class.getSimpleName();

  private CameraPreview mCameraPreview;
  private ImageButton mRecordButton;
  private RoundCornerProgressBar mProgressBar;

  private long mStartTime = 0;
  private boolean isRecording = false;

  Handler mHandler = new Handler();
  Runnable run = new Runnable() {
    @Override public void run() {
      long status = (System.currentTimeMillis() - mStartTime);
      mProgressBar.setProgress((int)status);
      Log.d(TAG, "run: "+ status);
      if (status<10000){
        mHandler.postDelayed(this, 0);
      }else {
        mCameraPreview.stopReocrd();
        mCameraPreview.releaseMediaRecorder();
        mCameraPreview.unlockCamera();
        mHandler.removeCallbacks(this);
        setCaptureButtonBackground(R.mipmap.record);
        isRecording = false;
      }
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_countdown);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    BCamera bCamera = new BCamera(this);
    bCamera.setQualityProfile(BCamera.QUALITY_480P);
    mCameraPreview = new CameraPreview(this, null, bCamera);
    mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        mCameraPreview.autoFocus();
        return false;
      }
    });
    final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.camera_preview);
    frameLayout.addView(mCameraPreview);

    mRecordButton = (ImageButton) findViewById(R.id.button_capture);
    mRecordButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!isRecording){
          if (mCameraPreview.prepareVideoRecorder()) {
            mCameraPreview.startRecord();
            mStartTime = System.currentTimeMillis();
            mHandler.postDelayed(run, 0);
            isRecording = true;
            setCaptureButtonBackground(R.mipmap.stop);
          }else {
            mCameraPreview.releaseMediaRecorder();
          }
        }else {
          mCameraPreview.stopReocrd();
          mCameraPreview.releaseMediaRecorder();
          mCameraPreview.unlockCamera();
          mHandler.removeCallbacks(run);
          isRecording = false;
          setCaptureButtonBackground(R.mipmap.record);
        }

      }
    });
    //mRecordButton.setOnTouchListener(new View.OnTouchListener() {
    //  @Override public boolean onTouch(View v, MotionEvent event) {
    //    switch (event.getAction()) {
    //      case MotionEvent.ACTION_DOWN:
    //        break;
    //      case MotionEvent.ACTION_UP:
    //        break;
    //    }
    //    return true;
    //  }
    //});
    mProgressBar = (RoundCornerProgressBar) findViewById(R.id.progress_countdown);
    mProgressBar.setMax(10000);
  }

  private void setCaptureButtonBackground(int resId) {
    mRecordButton.setBackgroundResource(resId);
  }
}
