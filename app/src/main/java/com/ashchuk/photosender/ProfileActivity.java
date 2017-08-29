package com.ashchuk.photosender;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashchuk.photosender.Models.User;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.userProfileImage)
    ImageView _userProfileImageView;
    @BindView(R.id.toolbar)
    Toolbar _toolbarView;
    @BindView(R.id.fab)
    FloatingActionButton _fabView;
    @BindView(R.id.email)
    TextView _emailView;
    @BindView(R.id.registrationDate)
    TextView _registrationDateView;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(_toolbarView);

        _fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Nothing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        user = (User) getIntent().getSerializableExtra("user");
        setTitle(user.getName());

        Bitmap image = BitmapFactory.decodeByteArray(Base64.decode(user.getAvatar(), 0), 0, Base64.decode(user.getAvatar(), 0).length);
        _userProfileImageView.setImageBitmap(image);
        _emailView.setText(user.getEmail());
        _registrationDateView.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(user.getRegistrationDate()));
    }
}
