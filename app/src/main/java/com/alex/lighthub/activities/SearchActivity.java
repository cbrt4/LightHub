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
import android.widget.LinearLayout;
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
    private LinearLayout pageNavigator;
    private TextView pageCounter;
    private Button buttonFirst, buttonPrevious, buttonNext, buttonLast;
    private HashMap<String, String> searchParameters;
    private static final String STARS = "stars", FORKS = "forks", UPDATED = "updated";
    private static final String ASCENDING = "asc", DESCENDING = "desc";
    private static int TOTAL_COUNT, PER_PAGE, CURRENT_PAGE, LAST_PAGE, FIRST_PAGE = 1;
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
                    searchParameters = new HashMap<>();
                    searchParameters.put(SearchLoader.PAGE, "1");
                    searchParameters.put(SearchLoader.PER_PAGE, "100");
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
                searchParameters = new HashMap<>();
                searchParameters.put(SearchLoader.PAGE, "1");
                searchParameters.put(SearchLoader.PER_PAGE, "100");
                search();
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        searchResults = (ListView) findViewById(R.id.search_results);

        pageNavigator = (LinearLayout) findViewById(R.id.page_navigator);
        pageNavigator.setVisibility(View.GONE);

        pageCounter = (TextView) findViewById(R.id.page_counter);

        buttonFirst = (Button) findViewById(R.id.button_first);
        buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchParameters.put(SearchLoader.PAGE, String.valueOf(FIRST_PAGE));
                search();
            }
        });

        buttonPrevious = (Button) findViewById(R.id.button_previous);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_PAGE = Integer.parseInt(searchParameters.get(SearchLoader.PAGE));
                searchParameters.put(SearchLoader.PAGE, String.valueOf(CURRENT_PAGE - 1));
                search();
            }
        });

        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_PAGE = Integer.parseInt(searchParameters.get(SearchLoader.PAGE));
                searchParameters.put(SearchLoader.PAGE, String.valueOf(CURRENT_PAGE + 1));
                search();
            }
        });

        buttonLast = (Button) findViewById(R.id.button_last);
        buttonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchParameters.put(SearchLoader.PAGE, String.valueOf(LAST_PAGE));
                search();
            }
        });

        searchParameters = new HashMap<>();
        searchParameters.put(SearchLoader.PAGE, String.valueOf(FIRST_PAGE));
        searchParameters.put(SearchLoader.PER_PAGE, "100");
        searchParameters.put(SearchLoader.SORT, "");
        searchParameters.put(SearchLoader.ORDER, "");

        try {
            search();
        } catch (Exception e) {
            Toast.makeText(this, e.toString() + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sort_stars_asc:
                searchParameters.put(SearchLoader.SORT, STARS);
                searchParameters.put(SearchLoader.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_stars_desc:
                searchParameters.put(SearchLoader.SORT, STARS);
                searchParameters.put(SearchLoader.ORDER, DESCENDING);
                search();
                return true;

            case R.id.sort_forks_asc:
                searchParameters.put(SearchLoader.SORT, FORKS);
                searchParameters.put(SearchLoader.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_forks_desc:
                searchParameters.put(SearchLoader.SORT, FORKS);
                searchParameters.put(SearchLoader.ORDER, DESCENDING);
                search();
                return true;

            case R.id.sort_updated_asc:
                searchParameters.put(SearchLoader.SORT, UPDATED);
                searchParameters.put(SearchLoader.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_updated_desc:
                searchParameters.put(SearchLoader.SORT, UPDATED);
                searchParameters.put(SearchLoader.ORDER, DESCENDING);
                search();
                return true;

            case R.id.exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void search() {
        nothingFound.setVisibility(View.GONE);
        getLoaderManager().initLoader(SEARCH_LOADER_ID, Bundle.EMPTY, this);
    }

    private void getData(String response) {
        try {
            if (response.equals(getString(R.string.no_internet_connection))) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            } else {
                TOTAL_COUNT = Integer.parseInt(new JSONObject(response).getString("total_count"));
                CURRENT_PAGE = Integer.parseInt(searchParameters.get(SearchLoader.PAGE));
                PER_PAGE = Integer.parseInt(searchParameters.get(SearchLoader.PER_PAGE));
                LAST_PAGE = TOTAL_COUNT % PER_PAGE == 0 ? TOTAL_COUNT / PER_PAGE : TOTAL_COUNT / PER_PAGE + 1;

                if (TOTAL_COUNT > PER_PAGE) {
                    pageNavigator.setVisibility(View.VISIBLE);
                    pageNavigator.setAnimation(alphaAppear);
                } else pageNavigator.setVisibility(View.GONE);

                if (CURRENT_PAGE > FIRST_PAGE) {
                    buttonFirst.setClickable(true);
                    buttonPrevious.setClickable(true);
                } else {
                    buttonFirst.setClickable(false);
                    buttonPrevious.setClickable(false);
                }

                if (CURRENT_PAGE < LAST_PAGE) {
                    buttonNext.setClickable(true);
                    buttonLast.setClickable(true);
                } else {
                    buttonNext.setClickable(false);
                    buttonLast.setClickable(false);
                }

                pageCounter.setText(CURRENT_PAGE + "/" + LAST_PAGE);

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
            Toast.makeText(this, e.toString() + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        searchProgress.setVisibility(View.VISIBLE);
        searchProgress.setAnimation(scaleExpand);
        return new SearchLoader(this,
                searchQuery.getText().toString(),
                getString(R.string.git_search_url),
                searchParameters);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String response) {
        if (response != null) getData(response);
        getLoaderManager().destroyLoader(loader.getId());
        searchProgress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        getLoaderManager().destroyLoader(loader.getId());
    }
}
