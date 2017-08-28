package com.alex.lighthub.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.loaders.SearchLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private Animation alphaAppear, scaleExpand;
    private ProgressBar searchProgress;
    private TextView nothingFound;
    private EditText searchQuery;
    private ListView searchResults;
    private String searchResultsContainer;
    private static final String CONTAINER = "container";
    private static final int SEARCH_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        scaleExpand = AnimationUtils.loadAnimation(this, R.anim.scale_expand);

        searchProgress = (ProgressBar) findViewById(R.id.search_progress);
        searchProgress.setVisibility(View.GONE);

        nothingFound = (TextView) findViewById(R.id.nothing_found);
        nothingFound.setText(getString(R.string.nothing_found));
        nothingFound.setVisibility(View.GONE);

        searchQuery = (EditText) findViewById(R.id.search_query);
        searchQuery.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    search();
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        Button searchButton = (Button) findViewById(R.id.go);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        searchResults = (ListView) findViewById(R.id.search_results);

        if (searchResultsContainer != null) getData(searchResultsContainer);
        else search();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CONTAINER, searchResultsContainer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getString(CONTAINER) != null) {
            searchResultsContainer = savedInstanceState.getString(CONTAINER);
            getData(searchResultsContainer);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void search() {
        nothingFound.setVisibility(View.GONE);
        getLoaderManager().initLoader(SEARCH_LOADER_ID, Bundle.EMPTY, this).forceLoad();
    }

    private void getData(String response) {
        try {
            if (response.equals(getString(R.string.no_internet_connection))) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                JSONArray repoArray = new JSONArray(new JSONObject(response).getString("items"));
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

                if (repoList.isEmpty()) nothingFound.setVisibility(View.VISIBLE);

                searchResults.setAdapter(new SimpleAdapter(
                        SearchActivity.this,
                        repoList,
                        R.layout.list_item,
                        new String[]{"name", "description"},
                        new int[]{R.id.repo_name, R.id.repo_description}));
                searchResults.setAnimation(alphaAppear);

            }
        } catch (JSONException e) {
            searchResults.setAdapter(null);
            nothingFound.setVisibility(View.VISIBLE);
            nothingFound.setAnimation(alphaAppear);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        searchProgress.setVisibility(View.VISIBLE);
        searchProgress.setAnimation(scaleExpand);
        return new SearchLoader(this, searchQuery.getText().toString(), getString(R.string.git_search_url));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String response) {
        getData(response);
        searchResultsContainer = response;
        getLoaderManager().destroyLoader(loader.getId());
        searchProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        getLoaderManager().destroyLoader(loader.getId());
    }
}
