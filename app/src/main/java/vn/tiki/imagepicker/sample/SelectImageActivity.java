package vn.tiki.imagepicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import vn.tiki.imagepicker.ImagePickerActivity;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class SelectImageActivity extends AppCompatActivity {

  private static final String TAG = "SelectImageActivity";
  private static final int IC_PICK_IMAGE = 1010;

  @SuppressWarnings("ResultOfMethodCallIgnored") public static void rm(File file) {
    if (file.isDirectory()) {
      for (File item : file.listFiles()) {
        rm(item);
      }
    }
    file.delete();
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_select_image);

    findViewById(R.id.btPickImage)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            startActivityForResult(
                new Intent(SelectImageActivity.this, ImagePickerActivity.class),
                IC_PICK_IMAGE);
          }
        });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == IC_PICK_IMAGE) {
      if (resultCode == RESULT_OK) {

        final ArrayList<String> imagePaths = data.getStringArrayListExtra("imagePaths");
        final File cacheDir = getExternalCacheDir();

        Observable.from(imagePaths)
            .map(new Func1<String, File>() {
              @Override public File call(String s) {
                return new File(s);
              }
            })
            .map(new Func1<File, String>() {
              @Override public String call(File file) {
                final File outputFile = new File(cacheDir, file.getName());
                BitmapUtil.compressImage(file.getPath(), outputFile.getPath());
                return outputFile.getAbsolutePath();
              }
            })
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<String>>() {
              @Override public void call(List<String> strings) {
                for (String filePath : strings) {
                  Log.d(TAG, "call: " + filePath);
                }
              }
            });
      } else {
        Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
