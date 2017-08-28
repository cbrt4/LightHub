package com.alex.lighthub.activities;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.loaders.MainLoader;
import com.alex.lighthub.responses.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Response> {

    private Animation alphaAppear, scaleExpand;
    private ImageView avatar;
    private TextView name, login, location, noInternetConnection;
    private ListView repos;
    private ProgressBar loadingProgress;
    private String credentials;
    private SharedPreferences sharedPrefs;
    private Response responseContainer;
    private static final String CONTAINER = "container";
    private static long BACK_PRESSED;
    private static final int MAIN_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        scaleExpand = AnimationUtils.loadAnimation(this, R.anim.scale_expand);

        avatar = (ImageView) findViewById(R.id.avatar);
        name = (TextView) findViewById(R.id.name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });
        login = (TextView) findViewById(R.id.login);
        location = (TextView) findViewById(R.id.location);
        repos = (ListView) findViewById(R.id.repos);

        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        loadingProgress.setVisibility(View.GONE);

        noInternetConnection = (TextView) findViewById(R.id.no_internet_connection);
        noInternetConnection.setVisibility(View.GONE);
        noInternetConnection.setText("No internet Connection.\nTap to try again.");
        noInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });

        if (responseContainer != null) getData(responseContainer);
        else load();
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
                startActivity(new Intent(this, MainActivity.class));
                return true;

            case R.id.exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(CONTAINER, responseContainer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getParcelable(CONTAINER) != null) {
            responseContainer = savedInstanceState.getParcelable(CONTAINER);
            getData(responseContainer);
        }
    }

    @Override
    public void onBackPressed() {
        if (BACK_PRESSED + 2000 > System.currentTimeMillis()) finish();
        else
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        BACK_PRESSED = System.currentTimeMillis();
    }

    private void load() {
        getCredentials();
        getLoaderManager().initLoader(MAIN_LOADER_ID, Bundle.EMPTY, this);
    }

    private void getData(Response response) {
        try {
            if (response.getError().equals(getString(R.string.unauthorized)))
                startActivity(new Intent(this, LoginActivity.class));
            else if (response.getError().equals(getString(R.string.no_internet_connection))) {
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
                        MainActivity.this,
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
        }
    }

    private void getCredentials() {
        sharedPrefs = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE);
        credentials = sharedPrefs.getString(getString(R.string.get_access), "");
    }

    private void logout() {
        sharedPrefs = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.get_access), null).apply();
        load();
    }

    @Override
    public Loader<Response> onCreateLoader(int i, Bundle bundle) {
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.setAnimation(scaleExpand);
        return new MainLoader(this, credentials);
    }

    @Override
    public void onLoadFinished(Loader<Response> loader, Response response) {
        getData(response);
        getLoaderManager().destroyLoader(loader.getId());
        loadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Response> loader) {
        getLoaderManager().destroyLoader(loader.getId());
    }
}