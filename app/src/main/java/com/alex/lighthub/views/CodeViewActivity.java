package com.alex.lighthub.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;

public class CodeViewActivity extends AppCompatActivity {

    private TextView lines, fileContent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_view);

        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            lines = (TextView) findViewById(R.id.lines);
            fileContent = (TextView) findViewById(R.id.file_content);

            if (getIntent().getStringExtra("content") != null) {
                if (toolbar != null) toolbar.setTitle(getIntent().getStringExtra("name"));
                lines.setText(getIntent().getStringExtra("lines"));
                fileContent.setText(getIntent().getStringExtra("content"));
            } else {
                if (toolbar != null) toolbar.setTitle(savedInstanceState.getString("name"));
                lines.setText(getIntent().getStringExtra("lines"));
                fileContent.setText(savedInstanceState.getString("content"));
            }
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            Toast.makeText(this,
                    e.toString() + "\n" + stackTrace,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            if (toolbar.getTitle() != null)
                savedInstanceState.putString("name", toolbar.getTitle().toString());
            savedInstanceState.putString("lines", lines.getText().toString());
            savedInstanceState.putString("content", fileContent.getText().toString());
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            Toast.makeText(this,
                    e.toString() + "\n" + stackTrace,
                    Toast.LENGTH_LONG).show();
        }
    }
}
