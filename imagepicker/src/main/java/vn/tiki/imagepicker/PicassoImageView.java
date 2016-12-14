package vn.tiki.imagepicker;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

/**
 * Created by Giang Nguyen on 12/5/16.
 */

public class PicassoImageView extends ImageView {

  public PicassoImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @BindingAdapter("filePath")
  public static void bindFilePath(PicassoImageView imageView, String filePath) {
    imageView.setImagePath(filePath);
  }

  public void setImagePath(final String filePath) {

    final int width = Util.getScreenWidth(getContext()) / 5;
    final int height = width;

    final RequestCreator requestCreator = Picasso.with(getContext())
        .load(new File(filePath));

    if (getScaleType() == ImageView.ScaleType.CENTER_CROP) {
      requestCreator.resize(width, height).centerCrop();
    } else if (getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
      requestCreator.resize(width, height).centerInside();
    } else {
      requestCreator.fit();
    }
    requestCreator.into(PicassoImageView.this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Picasso.with(getContext())
        .cancelRequest(this);
  }
}
