package com.group16.mcc.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token"); //if it's a string you stored.
        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText("Token: " + token);
    }
}
