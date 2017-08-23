package com.ashchuk.photosender;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashchuk.photosender.Loaders.UploadPhotoAsyncTaskLoader;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakePhotoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Boolean> {

    private final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int LOADER_ID = 0;
    private final String FILE_SUFFIX = "png";

    private Bitmap image;
    private Uri photoURI;
    private Location lastKnownLocation = null;

    @BindView(R.id.progressIndicator)
    AVLoadingIndicatorView progressIndicator;
    @BindView(R.id.contentView)
    LinearLayout contentView;

    @BindView(R.id.sendfab)
    FloatingActionButton sendfab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.comment)
    TextView commentView;
    @BindView(R.id.location)
    TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ButterKnife.bind(this);

        showProgress();
        initLocationListener();

        setSupportActionBar(toolbar);

        sendfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Upload photo...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (image == null)
                    return;

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);

                Bundle bundle = new Bundle();
                bundle.putByteArray("image", stream.toByteArray());
                bundle.putString("comment", !commentView.getText().toString().isEmpty() ? commentView.getText().toString() : "");
                bundle.putString("latitude", (lastKnownLocation != null) ? Double.toString(lastKnownLocation.getLatitude()) : "");
                bundle.putString("longitude", (lastKnownLocation != null) ? Double.toString(lastKnownLocation.getLongitude()) : "");

                startUploadPhotoTask(bundle);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropImageActivity();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != 0) {
                finish();
                return;
            }
        }
        dispatchTakePictureIntent();
        hideProgress();
    }

    private void showProgress() {
        contentView.setVisibility(View.GONE);
        progressIndicator.smoothToShow();
    }

    private void hideProgress() {
        contentView.setVisibility(View.VISIBLE);
        progressIndicator.smoothToHide();
    }

    private void initLocationListener() {
        LocationManager locationManager;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
                locationView.setText("Location applied!");
                locationView.setTextColor(Color.parseColor("#1da526"));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_ACCESS_FINE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void startCropImageActivity() {
        CropImage.activity(photoURI)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                FILE_SUFFIX,    /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(TakePhotoActivity.this, "Error occurred while creating the File", Toast.LENGTH_LONG).show();
            }
            if (photoFile != null) {
                try {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.ashchuk.photosender",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } catch (Exception ex) {
                    Toast.makeText(TakePhotoActivity.this, "Error occurred while creating the File", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_CANCELED) {
                finish();
                return;
            }
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                imageView.setImageBitmap(image);
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    imageView.setImageBitmap(image);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    throw result.getError();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(TakePhotoActivity.this, "Error during making photo. Try again", Toast.LENGTH_LONG).show();
        }
    }

    private void onFail() {
        hideProgress();
        Toast.makeText(TakePhotoActivity.this, "Error during sending photo. Try again", Toast.LENGTH_LONG).show();
    }

    private void onSuccess() {
        hideProgress();
        finish();
    }

    private void startUploadPhotoTask(Bundle bundle) {
        showProgress();
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(LOADER_ID);
        lm.initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle bundle) {
        return new UploadPhotoAsyncTaskLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean result) {
        if (result) {
            onSuccess();
        } else {
            onFail();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}
