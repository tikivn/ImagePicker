package vn.tiki.imagepicker.entity;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class Image {

  private final String path;
  private final int index;

  public Image(String path, int index) {
    this.path = path;
    this.index = index;
  }

  public String getPath() {
    return path;
  }

  public boolean isSelected() {
    return index > 0;
  }
}
