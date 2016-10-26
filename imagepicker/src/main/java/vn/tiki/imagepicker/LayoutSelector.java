package vn.tiki.imagepicker;

import android.support.annotation.LayoutRes;

/**
 * Created by Giang Nguyen on 9/24/16.
 */
public interface LayoutSelector {

  /**
   * Select layout for item
   *
   * @param type type of item
   * @return {@link LayoutRes} id of layout
   */
  @LayoutRes int layoutForType(int type);
}
