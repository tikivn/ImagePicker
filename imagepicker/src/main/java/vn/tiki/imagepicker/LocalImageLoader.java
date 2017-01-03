package vn.tiki.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import vn.tiki.imagepicker.entity.Image;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

class LocalImageLoader {

  private static final String[] PROJECTION = new String[] {
      MediaStore.Images.Media.DATA,
  };
  private WeakReference<Context> contextWeakReference;

  Observable<List<Image>> loadImage(final Context context) {
    contextWeakReference = new WeakReference<>(context);

    return Observable.create(new Observable.OnSubscribe<List<Image>>() {
      @Override public void call(Subscriber<? super List<Image>> subscriber) {
        subscriber.add(new MainThreadSubscription() {
          @Override protected void onUnsubscribe() {
            if (contextWeakReference != null) {
              contextWeakReference.clear();
            }
          }
        });

        if (contextWeakReference == null || contextWeakReference.get() == null) {
          return;
        }

        Cursor cursor = null;
        try {
          cursor = contextWeakReference.get()
              .getContentResolver()
              .query(
                  MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                  null, null, MediaStore.Images.Media.DATE_ADDED);

          if (cursor == null || cursor.getCount() == 0) {
            subscriber.onError(new NoSuchElementException());
            return;
          }

          final ArrayList<Image> images = new ArrayList<>(cursor.getCount());
          File file;
          if (cursor.moveToLast()) {
            do {
              if (Thread.interrupted()) {
                return;
              }

              String path = cursor.getString(cursor.getColumnIndex(PROJECTION[0]));

              file = new File(path);
              if (file.exists()) {
                final Image image = new Image(path, false);
                images.add(image);
              }
            } while (cursor.moveToPrevious());
          }
          subscriber.onNext(images);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        } finally {
          if (cursor != null) {
            cursor.close();
          }
          if (contextWeakReference != null) {
            contextWeakReference.clear();
          }
        }
      }
    });
  }
}
