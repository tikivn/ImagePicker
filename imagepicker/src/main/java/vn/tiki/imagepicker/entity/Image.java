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

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Image image = (Image) o;

    return getPath() != null ? getPath().equals(image.getPath()) : image.getPath() == null;
  }

  @Override public int hashCode() {
    return getPath() != null ? getPath().hashCode() : 0;
  }
}
