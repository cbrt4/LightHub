package com.alex.lighthub.views;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.presenters.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements Viewer<String> {

    private EditText loginView, passwordView;
    private CheckBox stayLoggedIn;
    private ProgressBar loginProgress;
    private String login, password;
    private static long BACK_PRESSED;
    private static LoginPresenter presenter;

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

        stayLoggedIn = (CheckBox) findViewById(R.id.stay_logged_in);
        stayLoggedIn.setChecked(false);

        loginProgress = (ProgressBar) findViewById(R.id.login_progress);
        loginProgress.setVisibility(View.GONE);

        Button signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (presenter == null) presenter =
                new LoginPresenter(this, getString(R.string.git_main_url), authenticate());
        presenter.attachView(this);
        presenter.refreshView();
    }

    @Override
    public void onBackPressed() {
        if (BACK_PRESSED + 2000 > System.currentTimeMillis()) {
            presenter = null;
            finish();
        } else Toast.makeText(this, "Press back again to return", Toast.LENGTH_SHORT).show();
        BACK_PRESSED = System.currentTimeMillis();
    }

    private void attemptLogin() {

        login = loginView.getText().toString();
        password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        } else if (TextUtils.isEmpty(login)) {
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
            presenter = new LoginPresenter(this, getString(R.string.git_main_url), authenticate());
            presenter.loadData();
        }
        presenter = null;
    }

    private String authenticate() {
        String authenticator = "Basic " + Base64.encodeToString((login + ":" + password).getBytes(),
                Base64.NO_WRAP);
        if (stayLoggedIn.isChecked()) {
            SharedPreferences.Editor editor =
                    getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE).edit();
            editor.putString(getString(R.string.get_access), authenticator).apply();
        }
        return authenticator;
    }

    private boolean isLoginValid(String login) {
        return !login.contains("^");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void showProgress() {
        loginProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        loginProgress.setVisibility(View.GONE);
    }

    @Override
    public void setView(String response) {
        if (response.equals("200")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("credentials", authenticate());
            startActivity(intent);
            presenter = null;
            finish();
        } else if (response.equals(getString(R.string.no_internet_connection)))
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        else if (response.equals("401"))
            Toast.makeText(this, getString(R.string.unauthorized), Toast.LENGTH_SHORT).show();
    }
}