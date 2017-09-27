package com.alex.lighthub.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.application.MainApp;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.models.SearchModel;
import com.alex.lighthub.presenters.SearchPresenter;

import java.util.HashMap;

public class SearchActivity extends AppCompatActivity
        implements Viewer<SearchModel>, View.OnClickListener {

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
    private String credentials;
    private SearchPresenter presenter;

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

        getCredentials();
        presenter = MainApp.getSearchPresenter(this, getString(R.string.git_search_url), credentials);
        presenter.attachView(this);

        searchParameters = presenter.getSearchParameters();
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

            case R.id.reset_sorting:
                searchParameters.put(SearchPresenter.SORT, "");
                searchParameters.put(SearchPresenter.ORDER, "");
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
            finish();
        } else Toast.makeText(this, "Press back again to return", Toast.LENGTH_SHORT).show();
        BACK_PRESSED = System.currentTimeMillis();
    }

    private void getCredentials() {
        if (getIntent().getStringExtra("credentials") != null)
            this.credentials = getIntent().getStringExtra("credentials");
        else
            this.credentials = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE)
                    .getString(getString(R.string.get_access), "");
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
    public void setView(SearchModel searchModel) {
        nothingFound.setVisibility(View.GONE);
        if (searchModel.getError() != null) {
            Toast.makeText(this, searchModel.getError(), Toast.LENGTH_SHORT).show();
        } else if (searchModel.getResults() == null ||
                searchModel.getResults() != null && searchModel.getResults().isEmpty()) {
            searchResults.setAdapter(null);
            nothingFound.setVisibility(View.VISIBLE);
        } else {
            searchParameters = presenter.getSearchParameters();
            int TOTAL_COUNT = searchModel.getTotalCount();
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

            searchResults.setAdapter(new SimpleAdapter(
                    this,
                    searchModel.getResults(),
                    R.layout.list_item_repo,
                    new String[]{"name", "description", "contents_url"},
                    new int[]{R.id.repo_name, R.id.repo_description, R.id.contents_url}));
            searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView contentsUrl = view.findViewById(R.id.contents_url);
                    TextView repoName = view.findViewById(R.id.repo_name);
                    Intent contentsIntent = new Intent(SearchActivity.this, ContentsActivity.class);
                    contentsIntent.putExtra("contents_url", contentsUrl.getText().toString());
                    contentsIntent.putExtra("name", repoName.getText().toString());
                    contentsIntent.putExtra("credentials", credentials);
                    startActivity(contentsIntent);
                }
            });
            searchResults.setAnimation(alphaAppear);
        }
    }
}