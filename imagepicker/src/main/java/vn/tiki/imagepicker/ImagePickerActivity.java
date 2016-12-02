package vn.tiki.imagepicker;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import vn.tiki.imagepicker.entity.Image;
import vn.tiki.noadapter.LayoutSelector;
import vn.tiki.noadapter.OnItemClickListener;
import vn.tiki.noadapter.OnlyAdapter;
import vn.tiki.noadapter.TypeDeterminer;

/**
 * Created by Giang Nguyen on 12/2/16.
 */
public class ImagePickerActivity extends AppCompatActivity
    implements EasyPermissions.PermissionCallbacks, ImagePickerView {

  private static final String TAG = "ImagePickerActivity";
  private static final int RC_WRITE_EXTERNAL_STORAGE = 1;
  private static final int RC_SETTINGS_SCREEN = 2;
  private RecyclerView rvImages;
  private OnlyAdapter adapter;
  private List<?> items;
  private ImagePickerPresenter presenter;
  private View vLoading;
  private View vEmpty;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LocalImageLoader imageLoader = new LocalImageLoader();
    presenter = new ImagePickerPresenter(imageLoader);

    setContentView(R.layout.activity_image_picker);

    setupActionBar();

    rvImages = (RecyclerView) findViewById(R.id.rvImages);
    vLoading = this.findViewById(R.id.vLoading);
    vEmpty = this.findViewById(R.id.vEmpty);

    setupLayoutManager(4);

    setupAdapter();
    setTitle("(0/10)");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupActionBar() {
    final ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SETTINGS_SCREEN) {
      // Do something after user returned from app settings screen, like showing a Toast.
      Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // Forward results to EasyPermissions
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    // This will display a dialog directing them to enable the permission in app settings.
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      final Snackbar snackbar = Snackbar.make(rvImages, R.string.msg_no_write_external_permission,
          Snackbar.LENGTH_INDEFINITE);
      snackbar.setAction(R.string.ok, new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
          final Uri uri = Uri.fromParts("package", getPackageName(), null);
          intent.setData(uri);
          startActivityForResult(intent, RC_SETTINGS_SCREEN);
        }
      });
      snackbar.show();
    }
  }

  @Override public void onPermissionsGranted(int requestCode, List<String> list) {
    if (requestCode == RC_WRITE_EXTERNAL_STORAGE) {
      loadImages();
    }
  }

  @AfterPermissionGranted(RC_WRITE_EXTERNAL_STORAGE)
  void loadImages() {
    presenter.loadImages(this);
  }

  @Override protected void onStart() {
    super.onStart();
    presenter.attachView(this);
    if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      loadImages();
    } else {
      EasyPermissions.requestPermissions(this, getString(R.string.msg_no_write_external_permission),
          RC_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
  }

  @Override protected void onStop() {
    presenter.detachView();
    super.onStop();
  }

  @Override public void showItems(@NonNull List<?> items) {
    final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemDiffCallback(
        items,
        this.items));
    adapter.setItems(items);
    diffResult.dispatchUpdatesTo(adapter);
    this.items = items;
  }

  @Override public void showLoading() {
    vLoading.setVisibility(View.VISIBLE);
    rvImages.setVisibility(View.GONE);
    vEmpty.setVisibility(View.GONE);
  }

  @Override public void hideLoading() {
    vLoading.setVisibility(View.GONE);
    rvImages.setVisibility(View.VISIBLE);
  }

  @Override public void showEmpty() {
    vLoading.setVisibility(View.GONE);
    vEmpty.setVisibility(View.VISIBLE);
  }

  @Override public void showCount(int count) {
    setTitle(String.format(Locale.getDefault(), "%d/10", count));
  }

  private void setupLayoutManager(int spanCount) {
    final GridLayoutManager layoutManager = new GridLayoutManager(
        this,
        spanCount,
        LinearLayoutManager.VERTICAL,
        false);
    rvImages.setLayoutManager(layoutManager);
    layoutManager.setSpanSizeLookup(new GridLayoutManager.DefaultSpanSizeLookup());
    rvImages.setHasFixedSize(true);
  }

  private void setupAdapter() {
    adapter = new OnlyAdapter.Builder()
        .typeDeterminer(new TypeDeterminer() {
          @Override public int typeOf(Object item) {
            if (item instanceof Image) {
              if (((Image) item).isSelected()) {
                return 1;
              }
              return 2;
            }
            return 0;
          }
        })
        .layoutSelector(new LayoutSelector() {
          @Override public int layoutForType(int type) {
            switch (type) {
              case 2:
                return R.layout.item_image;
              case 1:
                return R.layout.item_image_selected;
              default:
                return R.layout.item_camera_capture;
            }
          }
        })
        .onItemClickListener(new OnItemClickListener() {
          @Override public void onItemClick(View view, Object item, int position) {
            if (item instanceof Image) {
              presenter.toggleSelect(item);
            }
          }
        })
        .build();

    rvImages.setAdapter(adapter);
  }

  static class ItemDiffCallback extends DiffUtil.Callback {

    private final List<?> newItems;
    private final List<?> oldItems;

    ItemDiffCallback(List<?> newItems,
        List<?> oldItems) {
      this.newItems = newItems;
      this.oldItems = oldItems;
    }

    @Override public int getOldListSize() {
      return oldItems == null ? 0 : oldItems.size();
    }

    @Override public int getNewListSize() {
      return newItems == null ? 0 : newItems.size();
    }

    @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      final Object oldItem = oldItems.get(oldItemPosition);
      final Object newItem = this.newItems.get(newItemPosition);
      if (oldItem instanceof Image) {
        return newItem instanceof Image
            && ((Image) oldItem).isSelected() == ((Image) newItem).isSelected();
      }
      return !(newItem instanceof Image);
    }
  }
}
