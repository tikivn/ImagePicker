package vn.tiki.imagepicker;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Giang Nguyen on 8/14/16.
 */
public class OnlyAdapter extends RecyclerView.Adapter<OnlyViewHolder> {
  private final TypeDeterminer typeDeterminer;
  private final LayoutSelector layoutSelector;
  private List<?> items;
  private OnItemClickListener onItemClickListener;
  private ExtraBinding extraBinding;

  private OnlyAdapter(@NonNull TypeDeterminer typeDeterminer,
                      @NonNull LayoutSelector layoutSelector) {
    this.typeDeterminer = typeDeterminer;
    this.layoutSelector = layoutSelector;
  }

  private void setOnItemClickListener(@NonNull OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  private void setExtraBinding(@NonNull ExtraBinding extraBinding) {
    this.extraBinding = extraBinding;
  }

  public void setItems(List<?> items) {
    this.items = items;
  }

  @Override
  public OnlyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final int layoutId = layoutSelector.layoutForType(viewType);
    final ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId, parent, false);
    return new OnlyViewHolder(binding, extraBinding);
  }

  @Override
  public int getItemViewType(int position) {
    final Object item = items.get(position);
    return typeDeterminer.typeOf(item);
  }

  @Override
  public void onBindViewHolder(OnlyViewHolder holder, int position) {
    final Object item = items.get(position);
    holder.bind(item);
    holder.setOnItemClickListener(onItemClickListener);
  }

  @Override
  public int getItemCount() {
    return items == null ? 0 : items.size();
  }

  public static class Builder {

    private LayoutSelector layoutSelector;
    private TypeDeterminer typeDeterminer;
    private OnItemClickListener onItemClickListener;
    private ExtraBinding extraBinding;

    public Builder typeDeterminer(TypeDeterminer typeDeterminer) {
      this.typeDeterminer = typeDeterminer;
      return this;
    }

    public Builder layoutSelector(LayoutSelector layoutSelector) {
      this.layoutSelector = layoutSelector;
      return this;
    }

    public Builder onItemClickListener(OnItemClickListener onItemClickListener) {
      this.onItemClickListener = onItemClickListener;
      return this;
    }

    public Builder customBinding(@NonNull ExtraBinding extraBinding) {
      this.extraBinding = extraBinding;
      return this;
    }

    public OnlyAdapter build() {
      if (typeDeterminer == null) {
        typeDeterminer = new TypeDeterminer() {
          @Override public int typeOf(Object item) {
            return 0;
          }
        };
      }
      if (layoutSelector == null) {
        throw new NullPointerException("Null layoutSelector");
      }
      final OnlyAdapter adapter = new OnlyAdapter(typeDeterminer, layoutSelector);
      if (onItemClickListener != null) {
        adapter.setOnItemClickListener(onItemClickListener);
      }
      if (extraBinding != null) {
        adapter.setExtraBinding(extraBinding);
      }
      return adapter;
    }

  }

}
