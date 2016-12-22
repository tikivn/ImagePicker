package vn.tiki.imagepicker;

import vn.tiki.imagepicker.entity.Image;
import vn.tiki.noadapter.DiffCallback;

/**
 * Created by Giang Nguyen on 12/21/16.
 */

class ImageItemDiffCallback implements DiffCallback {

  @Override public boolean areItemsTheSame(Object oldItem, Object newItem) {
    if (oldItem instanceof Image) {
      if (newItem instanceof Image) {
        return ((Image) oldItem).getPath().equals(((Image) newItem).getPath());
      }
    }
    return oldItem.equals(newItem);
  }

  @Override public boolean areContentsTheSame(Object oldItem, Object newItem) {
    return oldItem.equals(newItem);
  }
}
