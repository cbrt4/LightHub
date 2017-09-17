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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.models.MainModel;
import com.alex.lighthub.presenters.MainPresenter;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Viewer<MainModel> {

    private Animation alphaAppear, scaleExpand, scaleShrink;
    private ImageView avatar;
    private TextView name, login, location;
    private ListView repos;
    private ProgressBar loadingProgress;
    private static long BACK_PRESSED;
    private String credentials;
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

        if (presenter == null) {
            getCredentials();
            presenter = new MainPresenter(this, getString(R.string.git_main_url), credentials);
        }
        presenter.attachView(this);
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
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.putExtra("credentials", credentials);
                startActivity(searchIntent);
                return true;

            case R.id.refresh:
                presenter.loadData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (BACK_PRESSED + 2000 > System.currentTimeMillis()) {
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
    public void setView(MainModel mainModel) {
        if (mainModel.getError() != null) {
            Toast.makeText(this, mainModel.getError(), Toast.LENGTH_SHORT).show();
        } else {
            name.setText(mainModel.getName() != null ?
                    mainModel.getName() : "");
            name.setAnimation(alphaAppear);

            login.setText(mainModel.getLogin() != null ?
                    mainModel.getLogin() : "");
            login.setAnimation(alphaAppear);

            location.setText(mainModel.getLocation() != null ?
                    mainModel.getLocation() : "");
            location.setAnimation(alphaAppear);

            repos.setAdapter(new SimpleAdapter(
                    this,
                    mainModel.getRepos() != null ? mainModel.getRepos() : new ArrayList<Map<String, ?>>(),
                    R.layout.list_item_repo,
                    new String[]{"name", "description", "contents_url"},
                    new int[]{R.id.repo_name, R.id.repo_description, R.id.contents_url}));
            repos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView contentsUrl = view.findViewById(R.id.contents_url);
                    TextView repoName = view.findViewById(R.id.repo_name);
                    Intent contentsIntent = new Intent(MainActivity.this, ContentsActivity.class);
                    contentsIntent.putExtra("contents_url", contentsUrl.getText().toString());
                    contentsIntent.putExtra("name", repoName.getText().toString());
                    contentsIntent.putExtra("credentials", credentials);
                    startActivity(contentsIntent);
                }
            });
            repos.setAnimation(alphaAppear);

            if (mainModel.getAvatar() != null) {
                avatar.setImageBitmap(mainModel.getAvatar());
                avatar.setAnimation(alphaAppear);
            }
        }
    }
}