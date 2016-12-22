package vn.tiki.imagepicker.entity;

/**
 * Created by Giang Nguyen on 12/2/16.
 */

public class Image {

  private final String path;
  private final boolean selected;

  public Image(String path, boolean selected) {
    this.path = path;
    this.selected = selected;
  }

  public String getPath() {
    return path;
  }

  public boolean isSelected() {
    return selected;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Image image = (Image) o;

    if (isSelected() != image.isSelected()) return false;
    return getPath() != null ? getPath().equals(image.getPath()) : image.getPath() == null;
  }

  @Override public int hashCode() {
    int result = getPath() != null ? getPath().hashCode() : 0;
    result = 31 * result + (isSelected() ? 1 : 0);
    return result;
  }

  @Override public String toString() {
    return "Image{" +
        "path='" + path + '\'' +
        ", selected=" + selected +
        '}';
  }
}
