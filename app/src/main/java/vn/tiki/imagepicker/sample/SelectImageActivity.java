package vn.tiki.imagepicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import vn.tiki.imagepicker.ImagePickerActivity;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class SelectImageActivity extends AppCompatActivity {

  private static final String TAG = "SelectImageActivity";
  private static final int IC_PICK_IMAGE = 1010;

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
        for (String imagePath : imagePaths) {
          Log.d(TAG, "onActivityResult: " + imagePath);
        }
      } else {
        Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
