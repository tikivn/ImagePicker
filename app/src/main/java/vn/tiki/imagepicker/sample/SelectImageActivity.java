package vn.tiki.imagepicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import vn.tiki.imagepicker.ImagePickerActivity;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class SelectImageActivity extends AppCompatActivity {

  private static final int IC_PICK_IMAGE = 1010;
  private ImageView ivImage;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_select_image);

    ivImage = ((ImageView) findViewById(R.id.ivImage));

    findViewById(R.id.btPickImage)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            startActivityForResult(new Intent(SelectImageActivity.this, ImagePickerActivity.class), IC_PICK_IMAGE);
          }
        });
  }
}
