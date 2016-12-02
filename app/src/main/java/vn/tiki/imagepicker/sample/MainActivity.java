package vn.tiki.imagepicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import vn.tiki.imagepicker.Callback;
import vn.tiki.imagepicker.CameraPicker;
import vn.tiki.imagepicker.GalleryPicker;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";
  private final GalleryPicker galleryPicker = new GalleryPicker();
  private final CameraPicker cameraPicker = new CameraPicker();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate: ");
    setContentView(R.layout.activity_main);
    galleryPicker.setCallback(new Callback() {
      @Override public void onSuccess(String imagePath) {
        Log.d(TAG, "onSuccess: " + imagePath);
      }

      @Override public void onError(Throwable throwable) {
        throwable.printStackTrace();
      }
    });

    cameraPicker.setCallback(new Callback() {
      @Override public void onSuccess(String imagePath) {
        Log.d(TAG, "onSuccess: " + imagePath);
      }

      @Override public void onError(Throwable throwable) {
        throwable.printStackTrace();
      }
    });
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.d(TAG, "onSaveInstanceState: ");
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    Log.d(TAG, "onRestoreInstanceState: ");

  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult: ");
    galleryPicker.handleResult(this, requestCode, resultCode, data);
    cameraPicker.handleResult(requestCode, resultCode, data);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    cameraPicker.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    galleryPicker.release();
    cameraPicker.release();
  }

  public void openCamera(View view) {
    if (cameraPicker.isSupported(this)) {
      cameraPicker.openPicker(this);
    } else {
      Toast.makeText(this, "Your device not support", Toast.LENGTH_SHORT).show();
    }
  }

  public void openGallery(View view) {
    if (galleryPicker.isSupported(this)) {
      galleryPicker.openPicker(this);
    } else {
      Toast.makeText(this, "Your device not support", Toast.LENGTH_SHORT).show();
    }
  }
}
