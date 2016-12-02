package vn.tiki.imagepicker.mvp;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class MvpPresenter<View> {
  private View view;

  public void attachView(View view) {
    this.view = view;
  }

  public void detachView() {
    this.view = null;
  }

  public View getView() {
    return view;
  }
}
