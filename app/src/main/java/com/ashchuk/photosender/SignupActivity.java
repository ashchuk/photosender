package com.ashchuk.photosender;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ashchuk.photosender.Loaders.RegisterAsyncTaskLoader;
import com.ashchuk.photosender.Models.User;

import butterknife.ButterKnife;
import butterknife.BindView;

public class SignupActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<User> {
    private static final String TAG = "SignupActivity";
    private static final int LOADER_ID = 0;

    @BindView(R.id.input_name)
    EditText _nameTextView;
    @BindView(R.id.input_email)
    EditText _emailTextView;
    @BindView(R.id.input_password)
    EditText _passwordTextView;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordTextView;
    @BindView(R.id.btn_signup)
    Button _signupButtonView;
    @BindView(R.id.link_login)
    TextView _loginLinkView;

    private ProgressDialog _progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButtonView.setEnabled(false);

        _progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _progressDialog.setIndeterminate(true);
        _progressDialog.setMessage("Creating Account...");
        _progressDialog.show();

        Bundle bundle = getBundle();
        startRegistrationTask(bundle);
    }

    @NonNull
    private Bundle getBundle() {
        String name = _nameTextView.getText().toString();
        String email = _emailTextView.getText().toString();
        String password = _passwordTextView.getText().toString();
        String reEnterPassword = _reEnterPasswordTextView.getText().toString();

        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        bundle.putString("password", password);
        bundle.putString("nickname", name);
        return bundle;
    }

    public void onSignupSuccess(User user) {
        _signupButtonView.setEnabled(true);
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButtonView.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameTextView.getText().toString();
        String email = _emailTextView.getText().toString();
        String password = _passwordTextView.getText().toString();
        String reEnterPassword = _reEnterPasswordTextView.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameTextView.setError("at least 3 characters");
            valid = false;
        } else {
            _nameTextView.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailTextView.setError("enter a valid email address");
            valid = false;
        } else {
            _emailTextView.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordTextView.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordTextView.setError(null);
        }

        if (reEnterPassword.isEmpty() ||
                reEnterPassword.length() < 4 ||
                reEnterPassword.length() > 10 ||
                !(reEnterPassword.equals(password))) {
            _reEnterPasswordTextView.setError("Password do not match");
            valid = false;
        } else {
            _reEnterPasswordTextView.setError(null);
        }

        return valid;
    }

    private void startRegistrationTask(Bundle bundle) {
        LoaderManager lm = getLoaderManager();
        lm.destroyLoader(LOADER_ID);
        lm.initLoader(LOADER_ID, bundle, this).forceLoad();
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle bundle) {
        return new RegisterAsyncTaskLoader(this, bundle);
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User result) {
        if (result != null)
            onSignupSuccess(result);
        else
            onSignupFailed();
        _progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }
}