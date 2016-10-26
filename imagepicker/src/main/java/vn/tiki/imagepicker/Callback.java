package vn.tiki.imagepicker;

public interface Callback {
    void onSuccess(String imagePath);

    void onError(Throwable throwable);
  }