package com.alex.lighthub.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.presenters.MainPresenter;
import com.alex.lighthub.responses.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Viewer<Response>, View.OnClickListener {

    private Animation alphaAppear, scaleExpand, scaleShrink;
    private ImageView avatar;
    private TextView name, login, location;
    private ListView repos;
    private ProgressBar loadingProgress;
    private LinearLayout unauthorizedLayout;
    private String credentials;
    private static long BACK_PRESSED;
    private static MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        scaleExpand = AnimationUtils.loadAnimation(this, R.anim.scale_expand);
        scaleShrink = AnimationUtils.loadAnimation(this, R.anim.scale_shrink);

        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        login = (TextView) findViewById(R.id.login);
        location = (TextView) findViewById(R.id.location);
        repos = (ListView) findViewById(R.id.repos);

        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        loadingProgress.setVisibility(View.GONE);

        unauthorizedLayout = (LinearLayout) findViewById(R.id.unauthorized_layout);
        unauthorizedLayout.setVisibility(View.GONE);

        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        Button searchButton = (Button) findViewById(R.id.button_search);
        searchButton.setOnClickListener(this);

        if (presenter == null) {
            getCredentials();
            presenter = new MainPresenter(this, getString(R.string.git_main_url), credentials);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        presenter.attachView(this);
        presenter.refreshView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;

            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;

            case R.id.refresh:
                presenter.loadData();
                return true;

            case R.id.exit:
                presenter = null;
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                startActivity(new Intent(this, LoginActivity.class));
                presenter = null;
                break;

            case R.id.button_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (BACK_PRESSED + 2000 > System.currentTimeMillis()) {
            presenter = null;
            finish();
        } else Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        BACK_PRESSED = System.currentTimeMillis();
    }

    private void getCredentials() {
        if (getIntent().getStringExtra("credentials") != null)
            this.credentials = getIntent().getStringExtra("credentials");
        else
            this.credentials = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE)
                .getString(getString(R.string.get_access), "");
    }

    private void logout() {
        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.get_access), null).apply();
    }

    @Override
    public void showProgress() {
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.setAnimation(scaleExpand);
    }

    @Override
    public void hideProgress() {
        loadingProgress.setAnimation(scaleShrink);
        loadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void setView(Response response) {
        unauthorizedLayout.setVisibility(View.GONE);
        try {
            if (response.getError().equals(getString(R.string.unauthorized))) {
                unauthorizedLayout.setVisibility(View.VISIBLE);
            } else if (response.getError().equals(getString(R.string.no_internet_connection))) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                JSONObject responseJSON = new JSONObject(response.getInfo());

                name.setText(responseJSON.getString("name") != null ?
                        responseJSON.getString("name") : "");
                name.setAnimation(alphaAppear);

                login.setText(responseJSON.getString("login"));
                login.setAnimation(alphaAppear);

                location.setText(responseJSON.getString("location") != null ?
                        responseJSON.getString("location") : "");
                location.setAnimation(alphaAppear);

                JSONArray repoArray = new JSONArray(response.getRepos());
                List<HashMap<String, String>> repoList = new ArrayList<>();
                JSONObject repository;
                String name;
                String description;
                HashMap<String, String> repo;
                for (int i = 0; i < repoArray.length(); i++) {
                    repository = repoArray.getJSONObject(i);
                    name = repository.getString("name");
                    description = repository.getString("description");
                    repo = new HashMap<>();
                    repo.put("name", name);
                    repo.put("description", description != null ? description : "");
                    repoList.add(repo);
                }

                repos.setAdapter(new SimpleAdapter(
                        this,
                        repoList,
                        R.layout.list_item,
                        new String[]{"name", "description"},
                        new int[]{R.id.repo_name, R.id.repo_description}));
                repos.setAnimation(alphaAppear);

                if (response.getAvatar() != null) {
                    avatar.setImageBitmap(response.getAvatar());
                    avatar.setAnimation(alphaAppear);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this, e.toString() + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            Toast.makeText(this, e.toString() + "\n\n" + stackTrace, Toast.LENGTH_LONG).show();
        }
    }
}