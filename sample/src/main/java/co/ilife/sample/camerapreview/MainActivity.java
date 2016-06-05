package co.ilife.sample.camerapreview;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private final String TAG = MainActivity.class.getSimpleName();

  public static int RECORD_CODE = 9001;
  public static String VIDEO_URL = "video_url";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (getIntent().hasExtra("error")) {
      TextView error = (TextView) findViewById(R.id.tv_error);
      error.setText(getIntent().getStringExtra("error"));
    }

    Button normalSample = (Button) findViewById(R.id.button_normal);
    normalSample.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), NormalActivity.class);
        startActivity(intent);
      }
    });

    Button countdownSample = (Button) findViewById(R.id.button_countdown);
    countdownSample.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), CountdownActivity.class);
        startActivity(intent);
      }
    });

    Button wechatSample = (Button) findViewById(R.id.button_wechat);
    wechatSample.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), WechatActivity.class);
        startActivityForResult(intent, RECORD_CODE);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
    if (requestCode == RECORD_CODE) {
      if (resultCode == RESULT_OK) {
        String videoUrl =  data.getStringExtra(VIDEO_URL);
        TextView tv = (TextView) findViewById(R.id.tv_video_url);
        tv.setText( videoUrl);
        ImageView iv = (ImageView ) findViewById(R.id.iv_video_preview);
        ContentResolver crThumb = getContentResolver();
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoUrl,
            MediaStore.Images.Thumbnails.MINI_KIND);
        iv.setImageBitmap(thumb);
      }
    }
  }
}
