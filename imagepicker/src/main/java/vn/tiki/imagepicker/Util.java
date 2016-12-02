package vn.tiki.imagepicker;

import android.content.Context;
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

  static File createImageFile(Context context) throws IOException {
    // Create an image file name
    final String timeStamp = DATE_FORMAT.format(new Date());
    final String imageFileName = "JPEG_" + timeStamp + "_";
    final File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    return File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);
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
