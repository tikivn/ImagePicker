package vn.tiki.imagepicker;

import android.support.v7.util.DiffUtil;
import java.util.List;
import vn.tiki.imagepicker.entity.Image;

/**
 * Created by Giang Nguyen on 12/15/16.
 */

class DiffUtilCallback extends DiffUtil.Callback {

  private List<?> items;
  private List<?> newItems;

  void setItems(List<?> items) {
    this.items = items;
  }

  void setNewItems(List<?> newItems) {
    this.newItems = newItems;
  }

  @Override public int getOldListSize() {
    return items == null ? 0 : items.size();
  }

  @Override public int getNewListSize() {
    return newItems == null ? 0 : newItems.size();
  }

  @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    final Object oldItem = items.get(oldItemPosition);
    final Object newItem = newItems.get(newItemPosition);
    return areItemsTheSame(oldItem, newItem);
  }

  @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    final Object oldItem = items.get(oldItemPosition);
    final Object newItem = newItems.get(newItemPosition);
    return areContentsTheSame(oldItem, newItem);
  }

  public boolean areItemsTheSame(Object oldItem, Object newItem) {
    if (oldItem instanceof Image) {
      if (newItem instanceof Image) {
        return ((Image) oldItem).getPath().equals(((Image) newItem).getPath());
      }
    }
    return oldItem.equals(newItem);
  }

  public boolean areContentsTheSame(Object oldItem, Object newItem) {
    return oldItem.equals(newItem);
  }
}
