package vn.tiki.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.os.OperationCanceledException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Giang Nguyen on 10/26/16.
 */

public class CameraPicker {

  private static final int CAMERA_REQUEST_CODE = 24032;
  private static final String TAG = "CameraPicker";
  private Callback callback;
  private File imageFile;

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public boolean isSupported(Context context) {
    return cameraIntent().resolveActivity(context.getPackageManager()) != null;
  }

  public void openPicker(Activity activity) {
    // Ensure that there's a camera activity to handle the intent
    try {
      imageFile = Util.createImageFile(activity);
      final Uri photoURI =
          FileProvider.getUriForFile(activity, activity.getPackageName() + ".file_provider",
              imageFile);
      final Intent takePictureIntent = cameraIntent();
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
      activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    } catch (IOException | IllegalArgumentException e) {
      // Error occurred while creating the File
      if (callback != null) {
        callback.onError(e);
      }
    }
  }

  @NonNull private Intent cameraIntent() {
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }

  public void handleResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAMERA_REQUEST_CODE) {
      if (callback != null && imageFile != null) {
        if (resultCode == Activity.RESULT_OK) {
          callback.onSuccess(imageFile.getAbsolutePath());
        } else if (resultCode == Activity.RESULT_CANCELED) {
          callback.onError(new OperationCanceledException("action is canceled"));
        } else {
          callback.onError(new IOException("can not taking picture"));
        }
      }
      imageFile = null;
    }
  }

  public void release() {
    callback = null;
  }
}
