package com.alex.lighthub.views;

import android.content.Context;
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
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.presenters.SearchPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements Viewer<String>, View.OnClickListener {

    private Animation alphaAppear, scaleExpand, scaleShrink;
    private ProgressBar searchProgress;
    private TextView nothingFound;
    private EditText searchQuery;
    private ListView searchResults;
    private LinearLayout pageNavigator;
    private TextView pageCounter;
    private Button buttonFirst, buttonPrevious, buttonNext, buttonLast;
    private static HashMap<String, String> searchParameters;
    private static final String STARS = "stars", FORKS = "forks", UPDATED = "updated";
    private static final String ASCENDING = "asc", DESCENDING = "desc";
    private static int CURRENT_PAGE, LAST_PAGE, FIRST_PAGE = 1;
    private static long BACK_PRESSED;
    private static SearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        scaleExpand = AnimationUtils.loadAnimation(this, R.anim.scale_expand);
        scaleShrink = AnimationUtils.loadAnimation(this, R.anim.scale_shrink);

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
                    searchParameters.put(SearchPresenter.PAGE, "1");
                    searchParameters.put(SearchPresenter.PER_PAGE, "100");
                    search();
                    InputMethodManager inputManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        searchResults = (ListView) findViewById(R.id.search_results);
        pageNavigator = (LinearLayout) findViewById(R.id.page_navigator);
        pageNavigator.setVisibility(View.GONE);
        pageCounter = (TextView) findViewById(R.id.page_counter);

        Button searchButton = (Button) findViewById(R.id.go);
        searchButton.setOnClickListener(this);
        buttonFirst = (Button) findViewById(R.id.button_first);
        buttonFirst.setOnClickListener(this);
        buttonPrevious = (Button) findViewById(R.id.button_previous);
        buttonPrevious.setOnClickListener(this);
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(this);
        buttonLast = (Button) findViewById(R.id.button_last);
        buttonLast.setOnClickListener(this);

        searchParameters = new HashMap<>();
        searchParameters.put(SearchPresenter.PAGE, String.valueOf(FIRST_PAGE));
        searchParameters.put(SearchPresenter.PER_PAGE, "100");
        searchParameters.put(SearchPresenter.SORT, "");
        searchParameters.put(SearchPresenter.ORDER, "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (presenter == null) presenter =
                new SearchPresenter(this, getString(R.string.git_search_url), "");
        presenter.attachView(this);
        presenter.refreshView();
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
                searchParameters.put(SearchPresenter.SORT, STARS);
                searchParameters.put(SearchPresenter.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_stars_desc:
                searchParameters.put(SearchPresenter.SORT, STARS);
                searchParameters.put(SearchPresenter.ORDER, DESCENDING);
                search();
                return true;

            case R.id.sort_forks_asc:
                searchParameters.put(SearchPresenter.SORT, FORKS);
                searchParameters.put(SearchPresenter.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_forks_desc:
                searchParameters.put(SearchPresenter.SORT, FORKS);
                searchParameters.put(SearchPresenter.ORDER, DESCENDING);
                search();
                return true;

            case R.id.sort_updated_asc:
                searchParameters.put(SearchPresenter.SORT, UPDATED);
                searchParameters.put(SearchPresenter.ORDER, ASCENDING);
                search();
                return true;

            case R.id.sort_updated_desc:
                searchParameters.put(SearchPresenter.SORT, UPDATED);
                searchParameters.put(SearchPresenter.ORDER, DESCENDING);
                search();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {

        CURRENT_PAGE = Integer.parseInt(searchParameters.get(SearchPresenter.PAGE));
        switch (view.getId()) {

            case R.id.button_first:
                searchParameters.put(SearchPresenter.PAGE, String.valueOf(FIRST_PAGE));
                break;

            case R.id.button_previous:
                searchParameters.put(SearchPresenter.PAGE, String.valueOf(CURRENT_PAGE - 1));
                break;

            case R.id.button_next:
                searchParameters.put(SearchPresenter.PAGE, String.valueOf(CURRENT_PAGE + 1));
                break;

            case R.id.button_last:
                searchParameters.put(SearchPresenter.PAGE, String.valueOf(LAST_PAGE));
                break;

            case R.id.go:
                searchParameters = new HashMap<>();
                searchParameters.put(SearchPresenter.PAGE, String.valueOf(FIRST_PAGE));
                searchParameters.put(SearchPresenter.PER_PAGE, "100");
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
        search();
    }

    @Override
    public void onBackPressed() {
        if (BACK_PRESSED + 2000 > System.currentTimeMillis()) {
            presenter = null;
            finish();
        } else Toast.makeText(this, "Press back again to return", Toast.LENGTH_SHORT).show();
        BACK_PRESSED = System.currentTimeMillis();
    }

    private void search() {
        presenter.setParameters(searchQuery.getText().toString(), searchParameters);
        presenter.loadData();
    }

    @Override
    public void showProgress() {
        searchProgress.setVisibility(View.VISIBLE);
        searchProgress.setAnimation(scaleExpand);
    }

    @Override
    public void hideProgress() {
        searchProgress.setAnimation(scaleShrink);
        searchProgress.setVisibility(View.GONE);
    }

    @Override
    public void setView(String response) {
        nothingFound.setVisibility(View.GONE);
        try {
            if (response.equals(getString(R.string.no_internet_connection))) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            } else if (response.equals("")) {
                searchResults.setAdapter(null);
                nothingFound.setVisibility(View.VISIBLE);
            } else {
                int TOTAL_COUNT = Integer.parseInt(new JSONObject(response).getString("total_count"));
                int PER_PAGE = Integer.parseInt(searchParameters.get(SearchPresenter.PER_PAGE));
                CURRENT_PAGE = Integer.parseInt(searchParameters.get(SearchPresenter.PAGE));
                LAST_PAGE = TOTAL_COUNT % PER_PAGE == 0 ?
                        TOTAL_COUNT / PER_PAGE : TOTAL_COUNT / PER_PAGE + 1;

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

                pageCounter.setText(" " + CURRENT_PAGE + "/" + LAST_PAGE + " ");

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
                        this,
                        repoList,
                        R.layout.list_item,
                        new String[]{"name", "description"},
                        new int[]{R.id.repo_name, R.id.repo_description}));
                searchResults.setAnimation(alphaAppear);
            }
        } catch (JSONException e) {
            Toast.makeText(this, e.toString() + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            Toast.makeText(this, e.toString() + "\n" + stackTrace, Toast.LENGTH_LONG).show();
        }
    }
}