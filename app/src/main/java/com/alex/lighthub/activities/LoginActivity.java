package com.alex.lighthub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alex.lighthub.R;

public class LoginActivity extends AppCompatActivity {

    private EditText loginView, passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginView = (EditText) findViewById(R.id.name);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.name || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        String login = loginView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(login)) {
            loginView.setError(getString(R.string.error_field_required));
            focusView = loginView;
            cancel = true;
        } else if (!isLoginValid(login)) {
            loginView.setError(getString(R.string.error_invalid_email));
            focusView = loginView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            authenticate(login, password);
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void authenticate(String login, String password) {
        String authenticator = "Basic " + Base64.encodeToString((login + ":" + password).getBytes(),
                Base64.NO_WRAP);
        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.get_access), authenticator).apply();
    }

    private boolean isLoginValid(String login) {
        return !login.contains("^");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}