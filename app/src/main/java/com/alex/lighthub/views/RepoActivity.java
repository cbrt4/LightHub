package com.alex.lighthub.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.alex.lighthub.R;

public class RepoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo);

        if (getIntent().getStringExtra("name") != null)
            Toast.makeText(this, getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No repo name in this intent", Toast.LENGTH_SHORT).show();
    }
}
