package com.group16.mcc.app;

import java.net.SocketTimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import com.group16.mcc.Util;
import com.group16.mcc.api.MccApi;
import com.group16.mcc.api.User;


/**
 * Modified from http://javapapers.com/android/beautiful-android-login-screen-design-tutorial/
 */
public class LoginActivity extends Activity implements Callback<User> {
    private static final String TAG = "LoginActivity";

    private static final MccApi api = Util.getApi();

    private EditText usernameTextView;
    private EditText passwordTextView;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameTextView = (EditText) findViewById(R.id.username);
        passwordTextView = (EditText) findViewById(R.id.password);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress);

        Button loginButton = (Button) findViewById(R.id.log_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });
    }

    /**
     * Validate Login form and authenticate.
     */
    public void initLogin() {
        usernameTextView.setError(null);
        passwordTextView.setError(null);

        View focusView = null;

        String password = passwordTextView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextView.setError(getString(R.string.field_required));
            focusView = passwordTextView;
        }

        String username = usernameTextView.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameTextView.setError(getString(R.string.field_required));
            focusView = usernameTextView;
        }

        if (focusView != null) {
            focusView.requestFocus();
        } else {
            loginProgress.setVisibility(View.VISIBLE);
            Call<User> call = api.loginUser(username, password);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Response<User> response, Retrofit retrofit) {
        loginProgress.setVisibility(View.INVISIBLE);
        if (response.isSuccess()) {
            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
            mainActivity.putExtra("token", response.body().token); //Optional parameters
            LoginActivity.this.startActivity(mainActivity);
        } else if (response.code() == 400) {
            passwordTextView.setError(getString(R.string.incorrect_password));
            passwordTextView.requestFocus();
        } else {
            Log.e(TAG, "Error when trying to log in");
        }
    }

    @Override
    public void onFailure(Throwable t) {
        loginProgress.setVisibility(View.INVISIBLE);
        Log.e(TAG, "Error when trying to log in", t);
        if (t instanceof SocketTimeoutException) {
            Toast.makeText(this, "Can't connect to backend", Toast.LENGTH_LONG).show();
        }
    }
}
