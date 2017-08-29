package com.ashchuk.photosender;

import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import android.app.LoaderManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.ashchuk.photosender.GLES.GlRenderer;
import com.ashchuk.photosender.GLES.SphereGLView;
import com.ashchuk.photosender.Loaders.GetPhotosAsyncTaskLoader;
import com.ashchuk.photosender.Loaders.LogoutAsyncTaskLoader;
import com.ashchuk.photosender.Models.Photo;
import com.ashchuk.photosender.Models.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlanetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Object> {

    private static final int LOGOUT_LOADER_ID = 0;
    private static final int SYNC_LOADER_ID = 1;
    private static final int HEADER_VIEW_INDEX = 0;
    private Boolean isBusy = false;

    static class HeaderView {
        @BindView(R.id.userProfileImage)
        CircleImageView userProfileImageView;
        @BindView(R.id.username)
        TextView usernameView;
        @BindView(R.id.email)
        TextView emailView;
    }

    @BindView(R.id.refreshfab)
    FloatingActionButton _refreshFABView;
    @BindView(R.id.photofab)
    FloatingActionButton _photoFABView;
    @BindView(R.id.toolbar)
    Toolbar _toolbarView;
    @BindView(R.id.mGLView)
    SphereGLView _OpenGLView;
    @BindView(R.id.nav_view)
    NavigationView _navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout _drawerLayoutView;

    private GlRenderer mGLRenderer;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet);

        HeaderView header = new HeaderView();
        ButterKnife.bind(this);
        ButterKnife.bind(header, _navigationView.getHeaderView(HEADER_VIEW_INDEX));

        mGLRenderer = new GlRenderer(this);
        _OpenGLView.InitView(mGLRenderer);

        _refreshFABView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloadAnimation();
                startGetPhotoTask();
            }
        });

        _photoFABView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToTakePhoto();
            }
        });

        _navigationView.getHeaderView(HEADER_VIEW_INDEX)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redirectToProfile(currentUser);
                    }
                });

        setSupportActionBar(_toolbarView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, _drawerLayoutView, _toolbarView, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        _drawerLayoutView.setDrawerListener(toggle);
        toggle.syncState();

        currentUser = (User) getIntent().getSerializableExtra("user");

        header.usernameView.setText(currentUser.getName());
        header.emailView.setText(currentUser.getEmail());
        Bitmap image = BitmapFactory.decodeByteArray(
                Base64.decode(currentUser.getAvatar(), 0),
                0, Base64.decode(currentUser.getAvatar(), 0).length);
        header.userProfileImageView.setImageBitmap(image);

        _navigationView.setNavigationItemSelectedListener(this);
    }

    private void redirectToTakePhoto() {
        Intent intent = new Intent(this, TakePhotoActivity.class);
        startActivity(intent);
    }

    public void redirectToProfile(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void startDownloadAnimation() {
        Animation animation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.rotate_clockwise);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isBusy)
                    _refreshFABView.startAnimation(animation);
                else
                    _refreshFABView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        _refreshFABView.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        if (_drawerLayoutView.isDrawerOpen(GravityCompat.START)) {
            _drawerLayoutView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.planet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sendmenu) {
            redirectToTakePhoto();
        } else if (id == R.id.profilemenu) {
            redirectToProfile(currentUser);
        } else if (id == R.id.syncmenu) {
            startGetPhotoTask();
        } else if (id == R.id.logout) {
            startLogoutTask();
        }

        _drawerLayoutView.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SphereGLView.PhotoEvent event) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("photo", event.photo);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRenderer.DeleteProgramms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _OpenGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _OpenGLView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void addPhotosToRenderer(ArrayList<Photo> photos) {
        isBusy = true;
        if (photos == null)
            Toast.makeText(getBaseContext(), "Load failed", Toast.LENGTH_LONG).show();
        else
            mGLRenderer.AddFigure(photos);
        isBusy = false;
    }

    private void startGetPhotoTask() {
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(SYNC_LOADER_ID);
        lm.initLoader(SYNC_LOADER_ID, null, this).forceLoad();
    }

    private void startLogoutTask() {
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(LOGOUT_LOADER_ID);
        lm.initLoader(LOGOUT_LOADER_ID, null, this).forceLoad();
    }

    private void restartLoader(int loaderId) {
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(loaderId);
        lm.initLoader(loaderId, null, this).forceLoad();
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle bundle) {
        if (id == SYNC_LOADER_ID)
            return new GetPhotosAsyncTaskLoader(this);
        if (id == LOGOUT_LOADER_ID)
            return new LogoutAsyncTaskLoader(this, currentUser.getId());
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (data == null) {
            restartLoader(loader.getId());
            return;
        }

        if (loader.getId() == LOGOUT_LOADER_ID) {
            Boolean result = (Boolean) data;
            if (result) logout();
            else restartLoader(loader.getId());
        }

        if (loader.getId() == SYNC_LOADER_ID) {
            ArrayList<Photo> photos = (ArrayList<Photo>) data;
            addPhotosToRenderer(photos);
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
