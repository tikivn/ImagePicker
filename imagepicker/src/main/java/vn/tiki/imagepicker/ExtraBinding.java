package vn.tiki.imagepicker;

import android.databinding.ViewDataBinding;

/**
 * Created by Giang Nguyen on 9/29/16.
 */

public interface ExtraBinding {

  /**
   * Callback which will be called when Adapter.onBindViewHolder() was called.
   *
   * @param binding  Item's binding
   * @param item     item's data
   * @param position item's position
   */
  void onBind(ViewDataBinding binding, Object item, int position);
}
