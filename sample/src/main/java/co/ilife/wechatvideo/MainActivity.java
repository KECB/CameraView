package co.ilife.wechatvideo;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import co.ilife.camerapreview.CameraPreview;

public class MainActivity extends AppCompatActivity {

  private final String TAG = MainActivity.class.getSimpleName();

  public static final int MEDIA_TYPE_VIDEO = 2;

  private CameraPreview mPreview;
  Button captureButton,switchCameraButton;

  private boolean isRecording = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ((AudioManager)getSystemService(Context.AUDIO_SERVICE)).setStreamMute(AudioManager.STREAM_SYSTEM,true);
     //Create our Preview view and set it as the content of our activity.
    mPreview = new CameraPreview(this,null);
    mPreview.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent event) {
        mPreview.autoFocus();
        return false;
      }
    });
    final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    preview.addView(mPreview);
    //mPreview = (CameraPreview) findViewById(R.id.camera_preview);

    captureButton = (Button) findViewById(R.id.button_capture);
    captureButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (isRecording) {
          // stop recording and release camera
          mPreview.stopReocrd();      // stop the recording
          mPreview.releaseMediaRecorder();     // release the MediaRecorder object
          mPreview.lockCamera();             // take camera access back from MediaRecorder

          // inform the user that recording has stopped
          setCaptureButtonText("Capture");
        }else {
          // initialize video camera
          if (mPreview.prepareVideoRecorder(1)) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mPreview.startRecord();

            // inform the user that recording has started
            setCaptureButtonText("Stop");
            isRecording = true;
          } else {
            // prepare didn't work, release the camera
            mPreview.releaseMediaRecorder();
            // inofrm user
          }
        }
      }
    });

    switchCameraButton = (Button) findViewById(R.id.button_switch);
    switchCameraButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mPreview.switchCamera();
        preview.removeAllViews();
        preview.addView(mPreview);
      }
    });

  }

  private void setCaptureButtonText(String text) {
    captureButton.setText(text);
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
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
