package com.group16.mcc.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import com.group16.mcc.api.MccApi;
import com.group16.mcc.api.User;

import com.group16.mcc.app.R;


/**
 * Modified from http://javapapers.com/android/beautiful-android-login-screen-design-tutorial/
 */
public class LoginActivity extends Activity implements Callback<User> {
    private static final String TAG = "LoginActivity";

    private static final String BASE_URL = "http://10.0.2.2:3000/api/"; // 10.0.2.2 is host loopback address
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private EditText usernameTextView;
    private EditText passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameTextView = (EditText) findViewById(R.id.username);
        passwordTextView = (EditText) findViewById(R.id.password);

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

        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordTextView.setError(getString(R.string.field_required));
            focusView = passwordTextView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameTextView.setError(getString(R.string.field_required));
            focusView = usernameTextView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            focusView.requestFocus();
        } else {
            setProgressBarIndeterminateVisibility(true);
            MccApi api = retrofit.create(MccApi.class);
            Call<User> call = api.loginUser(username, password);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Response<User> response, Retrofit retrofit) {
        setProgressBarIndeterminateVisibility(false);
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
        Log.e(TAG, "Error when trying to log in", t);
    }
}
