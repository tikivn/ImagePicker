package vn.tiki.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.os.OperationCanceledException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Giang Nguyen on 10/26/16.
 */

public class CameraPicker {

  private static final int CAMERA_REQUEST_CODE = 24032;
  private static final int PERMISSIONS_REQUEST_CODE = 1091;
  private Callback callback;
  private File imageFile;

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public boolean isSupported(Context context) {
    return cameraIntent().resolveActivity(context.getPackageManager()) != null;
  }

  public void openPicker(Activity activity) {
    if (!hasPermission(activity)) {
      requestPermission(activity);
      return;
    }
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

  public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
      int[] grantResults) {

    if (requestCode != PERMISSIONS_REQUEST_CODE) {
      return;
    }

    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      openPicker(activity);
    } else if (callback != null) {
      callback.onError(
          new SecurityException("permission " + Manifest.permission.CAMERA + " is not granted"));
    }
  }

  private void requestPermission(Activity activity) {
    ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA},
        PERMISSIONS_REQUEST_CODE);
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

  private boolean hasPermission(Activity thisActivity) {
    if (!isCameraPermissionRequested(thisActivity)) {
      return true;
    }

    return ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED;
  }

  private boolean isCameraPermissionRequested(Activity activity) {
    try {
      final PackageInfo packageInfo = activity.getPackageManager()
          .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
      final String[] permissions = packageInfo.requestedPermissions;
      for (String permission : permissions) {
        if (permission.equals(Manifest.permission.CAMERA)) {
          return true;
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  @NonNull private Intent cameraIntent() {
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }
}

