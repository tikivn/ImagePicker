package vn.tiki.imagepicker.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import vn.tiki.imagepicker.R;
import vn.tiki.noadapter.AbsViewHolder;

/**
 * Created by Giang Nguyen on 12/21/16.
 */

public class CaptureViewHolder extends AbsViewHolder {
  private CaptureViewHolder(View itemView) {
    super(itemView);
    itemView.setOnClickListener(this);
  }

  public static CaptureViewHolder create(ViewGroup parent) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.item_camera_capture, parent, false);
    return new CaptureViewHolder(view);
  }
}
