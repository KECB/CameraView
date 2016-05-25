package co.ilife.sample.camerapreview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
        startActivity(intent);
      }
    });
  }
}
