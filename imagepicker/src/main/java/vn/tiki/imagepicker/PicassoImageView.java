package vn.tiki.imagepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

/**
 * Created by Giang Nguyen on 12/5/16.
 */

public class PicassoImageView extends ImageView {
  private static final String TAG = "PicassoImageView";

  public PicassoImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setImagePath(String filePath) {
    final int width = this.getMeasuredWidth();
    final int height = this.getMeasuredHeight();
    final RequestCreator requestCreator = Picasso.with(this.getContext())
        .load(new File(filePath));

    if (this.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
      requestCreator.resize(width, height).centerCrop();
    } else if (this.getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
      requestCreator.resize(width, height).centerInside();
    } else {
      requestCreator.fit();
    }
    requestCreator.into(this);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Picasso.with(getContext())
        .cancelRequest(this);
  }
}
