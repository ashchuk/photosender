package com.ashchuk.photosender;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ashchuk.photosender.Loaders.LoginAsyncTaskLoader;
import com.ashchuk.photosender.Models.User;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<User> {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int LOADER_ID = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("pass", password);

        getLoaderManager().destroyLoader(LOADER_ID);
        Loader lm = getLoaderManager().initLoader(LOADER_ID, bundle, this);
        lm.forceLoad();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                User user = (User) data.getSerializableExtra("user");
                Intent intent = new Intent(this, PlanetActivity.class);
                intent.putExtra("user", user);
                finish();
                startActivity(intent);
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User user) {
        _loginButton.setEnabled(true);

        Intent intent = new Intent(this, PlanetActivity.class);
        intent.putExtra("user", user);
        finish();
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public Loader<User> onCreateLoader(int loaderId, Bundle bundle) {
        String email = bundle.getString("email");
        String pass = bundle.getString("pass");
        return new LoginAsyncTaskLoader(this, email, pass);
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User result) {
        if (result != null)
            onLoginSuccess(result);
        else
            onLoginFailed();
        progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }
}
