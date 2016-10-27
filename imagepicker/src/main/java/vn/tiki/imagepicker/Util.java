package vn.tiki.imagepicker;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Giang Nguyen on 10/26/16.
 */

class Util {
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
}
