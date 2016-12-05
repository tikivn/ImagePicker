package vn.tiki.imagepicker.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Giang Nguyen on 12/5/16.
 */

public class BitmapUtil {
  private BitmapUtil() {
    //no instance
  }

  private static int calculateInSampleSize(
      BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      final int halfHeight = height / 2;
      final int halfWidth = width / 2;

      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight
          && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }

    return inSampleSize;
  }

  private static Bitmap scaleByExif(String inputFilePath, Bitmap bitmap) throws IOException {
    ExifInterface exif = new ExifInterface(inputFilePath);
    int orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION, 0);
    Matrix matrix = new Matrix();
    if (orientation == 6) {
      matrix.postRotate(90);
    } else if (orientation == 3) {
      matrix.postRotate(180);
    } else if (orientation == 8) {
      matrix.postRotate(270);
    }
    final Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
        bitmap.getWidth(), bitmap.getHeight(), matrix,
        true);
    bitmap.recycle();
    return scaledBitmap;
  }

  public static void compressImage(String inputFilePath, String outputFilePath) {
    Bitmap bmp = null;
    try {
      BitmapFactory.Options options = new BitmapFactory.Options();
      //      by setting this field as true, the actual bitmap pixels are not loaded in the memory.
      // Just the bounds are loaded. If
      //      you try the use the bitmap here, you will get null.
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(inputFilePath, options);

      int actualHeight = options.outHeight;
      int actualWidth = options.outWidth;

      // max Height and width values of the compressed image is taken as 816x612
      float maxHeight = 1280;
      float maxWidth = 720;
      float imgRatio = actualWidth / actualHeight;
      float maxRatio = maxWidth / maxHeight;

      // width and height values are set maintaining the aspect ratio of the image
      if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
          imgRatio = maxHeight / actualHeight;
          actualWidth = (int) (imgRatio * actualWidth);
          actualHeight = (int) maxHeight;
        } else if (imgRatio > maxRatio) {
          imgRatio = maxWidth / actualWidth;
          actualHeight = (int) (imgRatio * actualHeight);
          actualWidth = (int) maxWidth;
        } else {
          actualHeight = (int) maxHeight;
          actualWidth = (int) maxWidth;
        }
      }

      //      setting inSampleSize value allows to load a scaled down version of the original image
      options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
      //      inJustDecodeBounds set to false to load the actual bitmap
      options.inJustDecodeBounds = false;
      //      this options allow android to claim the bitmap memory if it runs low on memory
      options.inPurgeable = true;
      options.inInputShareable = true;
      options.inTempStorage = new byte[16 * 1024];

      //          load the bitmap from its path
      bmp = BitmapFactory.decodeFile(inputFilePath, options);
      Bitmap scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
      float ratioX = actualWidth / (float) options.outWidth;
      float ratioY = actualHeight / (float) options.outHeight;
      float middleX = actualWidth / 2.0f;
      float middleY = actualHeight / 2.0f;

      Matrix scaleMatrix = new Matrix();
      scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

      Canvas canvas = new Canvas(scaledBitmap);
      canvas.setMatrix(scaleMatrix);
      canvas.drawBitmap(
          bmp,
          middleX - bmp.getWidth() / 2,
          middleY - bmp.getHeight() / 2,
          new Paint(Paint.FILTER_BITMAP_FLAG));
      bmp.recycle();
      scaledBitmap = scaleByExif(inputFilePath, scaledBitmap);
      compressAndSaveToFile(outputFilePath, scaledBitmap);
    } catch (OutOfMemoryError exception) {
      exception.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (bmp != null) {
        bmp.recycle();
      }
    }
  }

  private static void compressAndSaveToFile(String outputFilePath, Bitmap scaledBitmap)
      throws FileNotFoundException {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(outputFilePath);
      scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
      scaledBitmap.recycle();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      closeQuietly(out);
    }
  }

  private static void closeQuietly(Closeable closable) {
    if (closable != null) {
      try {
        closable.close();
      } catch (IOException e) {
        // Quietly
      }
    }
  }
}
