package com.ashchuk.photosender;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashchuk.photosender.Loaders.GetPhotoAsyncTaskLoader;
import com.ashchuk.photosender.Loaders.GetUserAsyncTaskLoader;
import com.ashchuk.photosender.Models.Photo;
import com.ashchuk.photosender.Models.User;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Object> {

    private static final int PHOTO_LOADER_ID = 0;
    private static final int USER_LOADER_ID = 1;

    private Boolean photoDownloaded;
    private Boolean userDownloaded;

    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.userProfileImage)
    CircleImageView userProfileImage;
    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.photo)
    ImageView photoView;

    @BindView(R.id.contentView)
    LinearLayout contentView;
    @BindView(R.id.progressIndicator)
    AVLoadingIndicatorView progressIndicator;

    private String photoUuid;
    private String userUuid;

    private User user;
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        photoUuid = getIntent().getStringExtra("photoUuid");
        userUuid = getIntent().getStringExtra("userUuid");

        startDownload();
    }

    private void showProgress() {
        contentView.setVisibility(View.GONE);
        progressIndicator.smoothToShow();
    }

    private void hideProgress() {
        contentView.setVisibility(View.VISIBLE);
        progressIndicator.smoothToHide();
    }

    private void startDownload() {
        showProgress();

        photoDownloaded = false;
        userDownloaded = false;

        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(PHOTO_LOADER_ID);
        lm.destroyLoader(USER_LOADER_ID);
        lm.initLoader(PHOTO_LOADER_ID, null, this).forceLoad();
        lm.initLoader(USER_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle bundle) {
        if (id == PHOTO_LOADER_ID)
            return new GetPhotoAsyncTaskLoader(this, photoUuid);
        if (id == USER_LOADER_ID)
            return new GetUserAsyncTaskLoader(this, userUuid);

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object obj) {
        if (obj == null) {
            LoaderManager lm = getLoaderManager();
            lm.destroyLoader(loader.getId());
            lm.initLoader(loader.getId(), null, this).forceLoad();
            return;
        }

        if (loader.getId() == PHOTO_LOADER_ID) {
            photo = (Photo) obj;

            comment.setText(photo.getDescription());
            date.setText(photo.getDate().toString());
            location.setText(Float.toString(photo.getLatitude()) + ", " + Float.toString(photo.getLongitude()));
            Bitmap image = BitmapFactory.decodeByteArray(Base64.decode(photo.getPhoto(), 0), 0, Base64.decode(photo.getPhoto(), 0).length);
            photoView.setImageBitmap(image);

            photoDownloaded = true;
        }
        if (loader.getId() == USER_LOADER_ID) {
            user = (User) obj;

            username.setText(user.getName());
            Bitmap image = BitmapFactory.decodeByteArray(Base64.decode(user.getAvatar(), 0), 0, Base64.decode(user.getAvatar(), 0).length);
            userProfileImage.setImageBitmap(image);

            userDownloaded = true;
        }

        if (photoDownloaded && userDownloaded)
            hideProgress();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
