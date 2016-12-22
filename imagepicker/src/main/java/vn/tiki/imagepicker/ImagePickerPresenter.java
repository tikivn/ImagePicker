package vn.tiki.imagepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import java.util.ArrayList;
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
  private ArrayList<String> selectedPaths;
  private Observable<Object> itemsObservable;

  ImagePickerPresenter(@NonNull LocalImageLoader imageLoader, boolean cameraSupported, int max,
      ArrayList<String> selectedPaths) {
    this.imageLoader = imageLoader;
    this.cameraSupported = cameraSupported;
    this.max = max;
    this.selectedPaths = selectedPaths == null ? new ArrayList<String>() : selectedPaths;
  }

  @Override public void detachView() {
    super.detachView();
    subscription.clear();
  }

  void toggleSelect(final Object item) {
    if (!(item instanceof Image)) {
      return;
    }

    final Image image = ((Image) item);
    if (image.isSelected()) {
      deselect(image);
    } else if (selectedPaths.size() >= max) {
      getView().showExceededNotification();
    } else {
      select(image);
    }
  }

  void loadImages(Context context) {
    Observable<Image> localImagesObservable = imageLoader.loadImage(context)
        .flatMap(new Func1<List<Image>, Observable<Image>>() {
          @Override public Observable<Image> call(List<Image> images) {
            return Observable.from(images);
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());

    if (!selectedPaths.isEmpty()) {
      localImagesObservable = localImagesObservable.map(new Func1<Image, Image>() {
        @Override public Image call(Image image) {
          if (selectedPaths.contains(image.getPath())) {
            return new Image(image.getPath(), true);
          }
          return image;
        }
      });
    }

    final Observable<Object> itemsObservable;
    if (cameraSupported) {
      itemsObservable = Observable.<Object>just(new PickerItem())
          .mergeWith(localImagesObservable.cast(Object.class));
    } else {
      itemsObservable = localImagesObservable.cast(Object.class);
    }

    if (getView() == null) {
      return;
    }

    getView().showLoading();

    subscription.add(itemsObservable.toList()
        .doOnNext(cache())
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
              view.showCount(selectedPaths.size());
            }
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            if (getView() != null) {
              getView().showError();
            }
          }
        }));
  }

  ArrayList<String> getSelectedImagePaths() {
    return selectedPaths;
  }

  @NonNull private Action1<List<Object>> cache() {
    return new Action1<List<Object>>() {
      @Override public void call(List<Object> objects) {
        ImagePickerPresenter.this.itemsObservable = Observable.from(objects);
      }
    };
  }

  private void select(final Image image) {
    itemsObservable = itemsObservable.map(new Func1<Object, Object>() {
      @Override public Object call(Object o) {
        if (o.equals(image)) {
          return new Image(((Image) o).getPath(), true);
        }
        return o;
      }
    });

    updateSelectedPaths();

    updateItemsDisplay();
  }

  private void deselect(final Image image) {
    itemsObservable = itemsObservable.map(new Func1<Object, Object>() {
      @Override public Object call(Object o) {
        if (o.equals(image)) {
          return new Image(((Image) o).getPath(), false);
        }
        return o;
      }
    });

    updateSelectedPaths();

    updateItemsDisplay();
  }

  private void updateItemsDisplay() {
    subscription.add(itemsObservable.toList().subscribe(new Action1<List<Object>>() {
      @Override public void call(List<Object> objects) {
        final ImagePickerView view = getView();
        if (view != null) {
          view.showItems(objects);
        }
      }
    }));
  }

  private void updateSelectedPaths() {
    subscription.add(itemsObservable.filter(new Func1<Object, Boolean>() {
      @Override public Boolean call(Object o) {
        return o instanceof Image && ((Image) o).isSelected();
      }
    }).cast(Image.class).map(new Func1<Image, String>() {
      @Override public String call(Image image) {
        return image.getPath();
      }
    }).toList().subscribe(new Action1<List<String>>() {
      @Override public void call(List<String> strings) {
        selectedPaths = new ArrayList<>(strings);
        final ImagePickerView view = getView();
        if (view != null) {
          view.showCount(selectedPaths.size());
        }
      }
    }));
  }
}
