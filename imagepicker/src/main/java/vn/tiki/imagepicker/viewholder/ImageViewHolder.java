package vn.tiki.imagepicker.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;
import vn.tiki.imagepicker.R;
import vn.tiki.imagepicker.Util;
import vn.tiki.imagepicker.entity.Image;
import vn.tiki.noadapter2.AbsViewHolder;

/**
 * Created by Giang Nguyen on 12/21/16.
 */
public class ImageViewHolder extends AbsViewHolder {

  private final ImageView imageView;

  private ImageViewHolder(View itemView) {
    super(itemView);
    itemView.setOnClickListener(this);
    imageView = ((ImageView) itemView.findViewById(R.id.image));
  }

  public static ImageViewHolder create(ViewGroup parent, boolean selected) {
    final int layoutId;
    if (selected) {
      layoutId = R.layout.item_image_list_selected;
    } else {
      layoutId = R.layout.item_image_list;
    }
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(layoutId, parent, false);
    return new ImageViewHolder(view);
  }

  @Override public void bind(Object item) {
    super.bind(item);
    final String filePath = ((Image) item).getPath();
    final int width = Util.getScreenWidth(itemView.getContext()) / 5;

    final RequestCreator requestCreator = Picasso.with(itemView.getContext())
        .load(new File(filePath));

    if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
      requestCreator.resize(width, width).centerCrop();
    } else if (imageView.getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
      requestCreator.resize(width, width).centerInside();
    } else {
      requestCreator.fit();
    }
    requestCreator.into(imageView);
  }

  @Override public void unbind() {
    super.unbind();
    Picasso.with(itemView.getContext())
        .cancelRequest(imageView);
  }
}
