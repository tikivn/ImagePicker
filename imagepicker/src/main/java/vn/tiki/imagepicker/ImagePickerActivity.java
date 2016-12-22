package vn.tiki.imagepicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import vn.tiki.imagepicker.entity.Image;
import vn.tiki.imagepicker.entity.PickerItem;
import vn.tiki.imagepicker.viewholder.CaptureViewHolder;
import vn.tiki.imagepicker.viewholder.ImageViewHolder;
import vn.tiki.noadapter.AbsViewHolder;
import vn.tiki.noadapter.OnItemClickListener;
import vn.tiki.noadapter.OnlyAdapter;
import vn.tiki.noadapter.TypeDeterminer;
import vn.tiki.noadapter.ViewHolderSelector;

/**
 * Created by Giang Nguyen on 12/2/16.
 */
public class ImagePickerActivity extends AppCompatActivity
    implements EasyPermissions.PermissionCallbacks, ImagePickerView {

  private static final int RC_WRITE_EXTERNAL_STORAGE = 1;
  private static final int RC_SETTINGS_SCREEN = 2;
  private static final int RC_CAMERA = 3;
  private static final int RC_CAPTURE = 4;
  private RecyclerView rvImages;
  private OnlyAdapter adapter;
  private ImagePickerPresenter presenter;
  private View vLoading;
  private View vEmpty;
  private View vRootView;
  private String currentImagePath;
  private int max;
  private Snackbar exceedNotification;

  public static Intent start(Context context, int max, ArrayList<String> selectedPaths) {
    Intent starter = new Intent(context, ImagePickerActivity.class);
    starter.putExtra("max", max);
    if (selectedPaths != null && !selectedPaths.isEmpty()) {
      starter.putExtra("selectedPaths", selectedPaths);
    }
    return starter;
  }

  @NonNull private Intent cameraIntent() {
    return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LocalImageLoader imageLoader = new LocalImageLoader();
    final boolean pickerSupported = cameraIntent().resolveActivity(getPackageManager()) != null;
    final Intent intent = getIntent();
    max = intent.getIntExtra("max", 5);
    final ArrayList<String> selectedPaths = intent.getStringArrayListExtra("selectedPaths");
    presenter = new ImagePickerPresenter(
        imageLoader,
        pickerSupported,
        max,
        selectedPaths);

    setContentView(R.layout.activity_picker_image_picker);

    setupActionBar();

    rvImages = (RecyclerView) findViewById(R.id.rvImages);
    vLoading = this.findViewById(R.id.vLoading);
    vEmpty = this.findViewById(R.id.vEmpty);
    vRootView = this.findViewById(R.id.vRootView);

    setupLayoutManager(4);

    setupAdapter();

    setupExceedNotification();
  }

  private void setupExceedNotification() {
    final String message = getString(R.string.msg_exceeded_format, max);
    exceedNotification = Snackbar.make(vRootView, message, Snackbar.LENGTH_SHORT);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.picker_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_done) {
      final ArrayList<String> selectedImagePaths = presenter.getSelectedImagePaths();
      setResultData(selectedImagePaths);
      return true;
    } else if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setResultData(ArrayList<String> imagePaths) {
    if (!imagePaths.isEmpty()) {
      final Intent data = new Intent();
      data.putStringArrayListExtra("imagePaths", imagePaths);
      setResult(RESULT_OK, data);
    }
    finish();
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
    switch (requestCode) {
      case RC_SETTINGS_SCREEN:
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          // Do something after user returned from app settings screen, like showing a Toast.
          Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT)
              .show();
        }
        break;
      case RC_CAPTURE:
        if (resultCode == RESULT_OK && currentImagePath != null) {
          final Uri imageUri = Uri.parse(currentImagePath);
          if (imageUri != null) {
            final String path = imageUri.getPath();
            MediaScannerConnection.scanFile(this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                  @Override
                  public void onScanCompleted(String path, Uri uri) {
                    runOnUiThread(new Runnable() {
                      @Override public void run() {
                        loadImages();
                      }
                    });
                  }
                });
          }
        }
        break;
      default:
        break;
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
    if (requestCode == RC_WRITE_EXTERNAL_STORAGE) {
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
    switch (requestCode) {
      case RC_WRITE_EXTERNAL_STORAGE:
        loadImages();
        break;
      case RC_CAMERA:
        captureImage();
        break;
      default:
        break;
    }
  }

  @AfterPermissionGranted(RC_CAMERA)
  void captureImage() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (intent.resolveActivity(getPackageManager()) != null) {
      try {
        final File imageFile = Util.createImageFile("Camera");
        currentImagePath = imageFile.getAbsolutePath();
        final String authority = getPackageName() + ".file_provider";
        final Uri uri = FileProvider.getUriForFile(this, authority, imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, RC_CAPTURE);
      } catch (IOException e) {
        Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
      }
    } else {
      Toast.makeText(this, getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
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
      showEmpty();
      EasyPermissions.requestPermissions(this, getString(R.string.msg_no_write_external_permission),
          RC_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
  }

  @Override protected void onStop() {
    presenter.detachView();
    super.onStop();
  }

  /**
   * Request for camera permission
   */
  private void captureImageWithPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
      if (rc == PackageManager.PERMISSION_GRANTED) {
        captureImage();
      } else {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.msg_no_camera_permission),
            RC_CAMERA,
            Manifest.permission.CAMERA);
      }
    } else {
      captureImage();
    }
  }

  @Override public void showItems(@NonNull List<?> items) {
    vLoading.setVisibility(View.GONE);
    rvImages.setVisibility(View.VISIBLE);
    adapter.setItems(items);
  }

  @Override public void showLoading() {
    vLoading.setVisibility(View.VISIBLE);
    rvImages.setVisibility(View.GONE);
    vEmpty.setVisibility(View.GONE);
  }

  @Override public void showEmpty() {
    vLoading.setVisibility(View.GONE);
    vEmpty.setVisibility(View.VISIBLE);
  }

  @Override public void showCount(int count) {
    setTitle(getString(R.string.selection_format, count, max));
  }

  @Override public void showExceededNotification() {
    if (exceedNotification.isShown()) {
      return;
    }
    exceedNotification.show();
  }

  @Override public void showError() {
    Toast.makeText(this, R.string.error_can_not_load_images, Toast.LENGTH_SHORT).show();
    finish();
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
        .viewHolderSelector(new ViewHolderSelector() {
          @Override public AbsViewHolder viewHolderForType(ViewGroup parent, int type) {
            switch (type) {
              case 1:
                return ImageViewHolder.create(parent, true);
              case 2:
                return ImageViewHolder.create(parent, false);
              default:
                return CaptureViewHolder.create(parent);
            }
          }
        })
        .onItemClickListener(new OnItemClickListener() {
          @Override public void onItemClick(View view, Object item, int position) {
            if (item instanceof Image) {
              presenter.toggleSelect(item);
            } else if (item instanceof PickerItem) {
              if (presenter.isMaximum()) {
                showExceededNotification();
              } else {
                captureImageWithPermission();
              }
            }
          }
        })
        .diffCallback(new ImageItemDiffCallback())
        .build();

    rvImages.setAdapter(adapter);
  }
}
