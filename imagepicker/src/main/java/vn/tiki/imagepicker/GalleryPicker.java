package vn.tiki.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Giang Nguyen on 10/26/16.
 */

public class GalleryPicker {
  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
  private static final int GALLERY_REQUEST_CODE = 24031;
  private Callback callback;
  private CompositeSubscription subscriptions = new CompositeSubscription();

  private static File createImageFile(Context context) throws IOException {
    // Create an image file name
    final String timeStamp = DATE_FORMAT.format(new Date());
    final String imageFileName = "JPEG_" + timeStamp + "_";
    final File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    return File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public boolean isSupported(Context context) {
    return galleryIntent().resolveActivity(context.getPackageManager()) != null;
  }

  public void openPicker(Activity activity) {
    // BEGIN_INCLUDE (use_open_document_intent)
    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
    final Intent intent = galleryIntent();

    activity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    // END_INCLUDE (use_open_document_intent)
  }

  public void handleResult(Context context, int requestCode, int resultCode, Intent data) {
    // The ACTION_OPEN_DOCUMENT intent was sent with the request code
    // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
    // response to some other intent, and the code below shouldn't run at all.

    if (callback != null
        && requestCode == GALLERY_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {
      // The document selected by the user won't be returned in the intent.
      // Instead, a URI to that document will be contained in the return intent
      // provided to this method as a parameter.
      // Pull that URI using resultData.getData().
      if (data == null) {
        callback.onError(new IOException("no data"));
      } else {
        final Uri uri = data.getData();
        subscriptions.add(saveUriToFile(context, uri).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
              @Override public void call(String s) {
                if (callback != null) {
                  callback.onSuccess(s);
                }
              }
            }, new Action1<Throwable>() {
              @Override public void call(Throwable throwable) {
                if (callback != null) {
                  callback.onError(throwable);
                }
              }
            }));
      }
    }
  }

  public void release() {
    callback = null;
    subscriptions.clear();
  }

  @NonNull private Intent galleryIntent() {
    Intent intent;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
      intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    } else {
      intent = new Intent(Intent.ACTION_PICK);
    }

    // Filter to only show results that can be "opened", such as a file (as opposed to a list
    // of contacts or timezones)
    //intent.addCategory(Intent.CATEGORY_OPENABLE);

    // Filter to show only images, using the image MIME data type.
    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
    // To search for all documents available via installed storage providers, it would be
    // "*/*".
    intent.setType("image/*");
    return intent;
  }

  private Observable<String> saveUriToFile(final Context context, final Uri uri) {
    return Observable.fromCallable(new UriFileSaving(context, uri));
  }

  private static class UriFileSaving implements Callable<String> {
    private WeakReference<Context> contextRef;
    private Uri uri;

    UriFileSaving(Context context, Uri uri) {
      this.contextRef = new WeakReference<>(context);
      this.uri = uri;
    }

    @Override public String call() throws Exception {
      if (contextRef.get() == null) {
        return null;
      }
      final File imageFile = createImageFile(contextRef.get());
      ParcelFileDescriptor parcelFileDescriptor = null;
      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
        if (contextRef.get() == null) {
          return null;
        }
        parcelFileDescriptor = contextRef.get().getContentResolver().openFileDescriptor(uri, "r");
        if (parcelFileDescriptor == null) {
          throw new IOException("can't open uri " + uri);
        }
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        inputStream = new FileInputStream(fileDescriptor);
        outputStream = new FileOutputStream(imageFile);

        final byte[] buffer = new byte[2024];
        int length;

        while ((length = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, length);
        }
      } finally {
        try {
          if (parcelFileDescriptor != null) {
            parcelFileDescriptor.close();
          }
          if (outputStream != null) {
            outputStream.flush();
            outputStream.close();
          }
          if (inputStream != null) {
            inputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return imageFile.getAbsolutePath();
    }
  }
}
