package vn.tiki.imagepicker.sample;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Giang Nguyen on 10/27/16.
 */

public class App extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);
  }
}
