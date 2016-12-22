package vn.tiki.imagepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

/**
 * Created by Giang Nguyen on 12/22/16.
 */

public class PicassoImageView extends ImageView {
  public PicassoImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setFilePath(String filePath) {
    final int width = Util.getScreenWidth(this.getContext()) / 5;

    final RequestCreator requestCreator = Picasso.with(this.getContext())
        .load(new File(filePath));

    if (this.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
      requestCreator.resize(width, width).centerCrop();
    } else if (this.getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
      requestCreator.resize(width, width).centerInside();
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
