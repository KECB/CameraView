package co.ilife.camerapreview;

import android.media.CamcorderProfile;
import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Media recorder settings
 * Created by KECB on 5/16/16.
 */
public class BCamera {

  @IntDef({QUALITY_HIGH, QUALITY_LOW, QUALITY_480P, QUALITY_720P, QUALITY_1080P, QUALITY_TIME_LAPSE_HIGH, QUALITY_TIME_LAPSE_LOW})
  @Retention(RetentionPolicy.SOURCE)
  public @interface QualityProfile {
  }

  public static final int QUALITY_HIGH = CamcorderProfile.QUALITY_HIGH;
  public static final int QUALITY_LOW = CamcorderProfile.QUALITY_LOW;
  public static final int QUALITY_480P = CamcorderProfile.QUALITY_480P;
  public static final int QUALITY_720P = CamcorderProfile.QUALITY_720P;
  public static final int QUALITY_1080P = CamcorderProfile.QUALITY_1080P;
  public static final int QUALITY_TIME_LAPSE_LOW  = CamcorderProfile.QUALITY_TIME_LAPSE_LOW;
  public static final int QUALITY_TIME_LAPSE_HIGH = CamcorderProfile.QUALITY_TIME_LAPSE_HIGH;

  public static final int STATUS_RECORDED = 1;
  public static final int STATUS_RETRY = 2;




}
