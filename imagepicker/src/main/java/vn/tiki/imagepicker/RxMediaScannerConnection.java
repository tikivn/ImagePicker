package vn.tiki.imagepicker;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.lang.ref.WeakReference;
import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

class RxMediaScannerConnection {
  private WeakReference<Context> contextWeakReference;

  RxMediaScannerConnection(Context context) {
    contextWeakReference = new WeakReference<>(context);
  }

  Observable<Uri> scanFile(final String path) {
    return Observable.fromEmitter(new Action1<AsyncEmitter<Uri>>() {
      @Override public void call(final AsyncEmitter<Uri> uriAsyncEmitter) {
        uriAsyncEmitter.setCancellation(new AsyncEmitter.Cancellable() {
          @Override public void cancel() throws Exception {
            contextWeakReference.clear();
          }
        });

        if (contextWeakReference.get() == null) {
          uriAsyncEmitter.onCompleted();
          return;
        }
        MediaScannerConnection.scanFile(contextWeakReference.get(),
            new String[] { path }, null,
            new MediaScannerConnection.OnScanCompletedListener() {
              @Override
              public void onScanCompleted(final String path, Uri uri) {
                uriAsyncEmitter.onNext(uri);
                uriAsyncEmitter.onCompleted();
              }
            });
      }
    }, AsyncEmitter.BackpressureMode.BUFFER);
  }
}