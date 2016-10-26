package vn.tiki.imagepicker;

/**
 * Created by Giang Nguyen on 9/24/16.
 */

public interface TypeDeterminer {

  /**
   * Determine type of each item which will be used to select layout
   * @param item the item
   * @return unique id for object
   */
  int typeOf(Object item);
}
