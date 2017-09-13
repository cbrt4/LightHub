package com.alex.lighthub.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alex.lighthub.R;
import com.alex.lighthub.views.LoginActivity;
import com.alex.lighthub.views.MainActivity;

public class Starter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String credentials = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE)
                    .getString(getString(R.string.get_access), "");

        if (credentials.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
