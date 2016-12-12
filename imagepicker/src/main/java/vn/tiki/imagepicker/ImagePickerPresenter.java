package vn.tiki.imagepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import vn.tiki.imagepicker.entity.Image;
import vn.tiki.imagepicker.entity.PickerItem;
import vn.tiki.imagepicker.mvp.MvpPresenter;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

class ImagePickerPresenter extends MvpPresenter<ImagePickerView> {

  private static final String TAG = "ImagePickerPresenter";
  private final LocalImageLoader imageLoader;
  private final boolean cameraSupported;
  private final int max;
  private CompositeSubscription subscription = new CompositeSubscription();
  private List<Object> items = Collections.emptyList();
  private int count = 0;

  ImagePickerPresenter(@NonNull LocalImageLoader imageLoader, boolean cameraSupported, int max) {
    this.imageLoader = imageLoader;
    this.cameraSupported = cameraSupported;
    this.max = max;
  }

  void toggleSelect(final Object item) {
    subscription.add(Observable.from(this.items)
        .map(new Func1<Object, Object>() {
          @Override public Object call(Object o) {
            if (o.equals(item) && o instanceof Image) {
              final int index;
              if (((Image) o).isSelected()) {
                index = 0;
                count--;
              } else if (count >= max) {
                return o;
              } else {
                count++;
                index = count;
              }
              return new Image(((Image) o).getPath(), index);
            }
            return o;
          }
        })
        .toList().doOnNext(cache()).subscribe(new Action1<List<Object>>() {
          @Override public void call(List<Object> objects) {
            final ImagePickerView view = getView();
            if (view != null) {
              view.showItems(objects);
              view.showCount(count);
            }
          }
        }));
  }

  void loadImages(Context context) {
    if (getView() == null) {
      return;
    }

    getView().showLoading();

    subscription.add(imageLoader.loadImage(context)
        .flatMap(new Func1<List<Image>, Observable<Image>>() {
          @Override public Observable<Image> call(List<Image> images) {
            return Observable.from(images);
          }
        })
        .map(new Func1<Image, Image>() {
          @Override public Image call(Image image) {
            if (items.isEmpty()) {
              return image;
            }
            for (Object item : items) {
              if (item.equals(image)) {
                return (Image) item;
              }
            }
            return image;
          }
        })
        .toList()
        .map(new Func1<List<Image>, List<Object>>() {
          @Override public List<Object> call(List<Image> images) {
            final int size = images.size();
            final ArrayList<Object> items = new ArrayList<>(size);
            if (cameraSupported) {
              items.add(new PickerItem());
            }
            if (size > 0) {
              items.addAll(images);
            }
            return items;
          }
        })
        .doOnNext(cache())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Object>>() {
          @Override public void call(List<Object> images) {
            final ImagePickerView view = getView();
            if (view == null) {
              return;
            }

            if (images.isEmpty()) {
              view.showEmpty();
            } else {
              view.showItems(images);
            }
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            if (getView() != null) {
              getView().hideLoading();
            }
            Log.e(TAG, "call: load images", throwable);
          }
        }));
  }

  @NonNull private Action1<List<Object>> cache() {
    return new Action1<List<Object>>() {

      @Override public void call(List<Object> objects) {
        items = objects;
      }
    };
  }

  @Override public void detachView() {
    super.detachView();
    subscription.clear();
  }

  ArrayList<String> getSelectedImagePaths() {
    if (items.isEmpty()) {
      return new ArrayList<>(0);
    }
    final ArrayList<String> selectedImagePaths = new ArrayList<>();
    for (Object item : items) {
      if (item instanceof Image && ((Image) item).isSelected()) {
        selectedImagePaths.add(((Image) item).getPath());
      }
    }
    return selectedImagePaths;
  }
}
