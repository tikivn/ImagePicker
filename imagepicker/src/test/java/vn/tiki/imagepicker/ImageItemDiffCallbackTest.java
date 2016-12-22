package vn.tiki.imagepicker;

import org.junit.Test;
import vn.tiki.imagepicker.entity.Image;
import vn.tiki.imagepicker.entity.PickerItem;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Giang Nguyen on 12/21/16.
 */
public class ImageItemDiffCallbackTest {

  private final ImageItemDiffCallback diffCallback = new ImageItemDiffCallback();

  @Test
  public void shouldPickersAreTheSame() throws Exception {
    assertTrue(diffCallback.areItemsTheSame(
        new PickerItem(),
        new PickerItem()));
  }

  @Test
  public void shouldPickersAreTheSameContent() throws Exception {
    assertTrue(diffCallback.areContentsTheSame(
        new PickerItem(),
        new PickerItem()));
  }

  @Test
  public void shouldImageAndPickerAreNotSame() throws Exception {
    assertFalse(diffCallback.areContentsTheSame(
        new PickerItem(),
        new Image("file://image1", false)));
  }

  @Test
  public void shouldImagesAreTheSame() throws Exception {
    assertTrue(diffCallback.areItemsTheSame(
        new Image("file://image1", false),
        new Image("file://image1", false)));
  }

  @Test
  public void shouldImagesAreTheSameIgnoreSelected() throws Exception {
    assertTrue(diffCallback.areItemsTheSame(
        new Image("file://image1", false),
        new Image("file://image1", true)));
  }

  @Test
  public void shouldImagesAreNotTheSame() throws Exception {
    assertFalse(diffCallback.areItemsTheSame(
        new Image("file://image1", false),
        new Image("file://image2", false)));
  }

  @Test
  public void shouldImageContentsAreSame() throws Exception {
    assertTrue(diffCallback.areContentsTheSame(
        new Image("file://image1", false),
        new Image("file://image1", false)));
  }

  @Test
  public void shouldImageContentsNotAreSame() throws Exception {
    assertFalse(diffCallback.areContentsTheSame(
        new Image("file://image1", false),
        new Image("file://image1", true)));
  }
}