package vn.tiki.imagepicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import vn.tiki.imagepicker.Callback;
import vn.tiki.imagepicker.GalleryPicker;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private final GalleryPicker galleryPicker = new GalleryPicker();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    galleryPicker.setCallback(new Callback() {
      @Override public void onSuccess(String imagePath) {
        Log.d(TAG, "onSuccess: " + imagePath);
      }

      @Override public void onError(Throwable throwable) {
        throwable.printStackTrace();
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    galleryPicker.handleResult(this, requestCode, resultCode, data);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    galleryPicker.release();
  }

  public void openCamera(View view) {
  }

  public void openGallery(View view) {
    if (galleryPicker.isSupported(this)) {
      galleryPicker.openPicker(this);
    } else {
      Toast.makeText(this, "Your device not support", Toast.LENGTH_SHORT).show();
    }
  }
}
