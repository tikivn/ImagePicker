package vn.tiki.imagepicker;

import android.databinding.BindingAdapter;
import android.os.Environment;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Giang Nguyen on 10/26/16.
 */

public class Util {
  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

  private Util() {
    //no instance
  }

  static File createImageFile(String directory) throws IOException {
    // External sdcard location
    File mediaStorageDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        directory);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        throw new IOException("Can't create image directory");
      }
    }

    // Create a media file name
    String timeStamp = DATE_FORMAT.format(new Date());
    String imageFileName = "IMG_" + timeStamp;

    return File.createTempFile(imageFileName, ".jpg", mediaStorageDir);
  }

  @BindingAdapter("filePath")
  public static void loadImageFileToImageView(ImageView imageView, String filePath) {
    final int width = imageView.getMeasuredWidth();
    final int height = imageView.getMeasuredHeight();
    final RequestCreator requestCreator = Picasso.with(imageView.getContext())
        .load(new File(filePath));

    if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
      requestCreator.resize(width, height).centerCrop();
    } else if (imageView.getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
      requestCreator.resize(width, height).centerInside();
    } else {
      requestCreator.fit();
    }
    requestCreator.into(imageView);
  }
}
