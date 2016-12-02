package vn.tiki.imagepicker;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public interface ImagePickerView {

  void showItems(@NonNull List<?> items);

  void showLoading();

  void hideLoading();

  void showEmpty();

  void showCount(int count);
}
