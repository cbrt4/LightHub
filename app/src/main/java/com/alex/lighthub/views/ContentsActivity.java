package com.alex.lighthub.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.lighthub.R;
import com.alex.lighthub.application.MainApp;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.models.ContentsModel;
import com.alex.lighthub.presenters.ContentsPresenter;

import java.util.Map;

public class ContentsActivity extends AppCompatActivity implements Viewer<ContentsModel> {

    private ListView contentsList;
    private Animation alphaAppear, scaleExpand, scaleShrink;
    private ProgressBar loadingProgress;
    private String credentials;
    private ContentsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        alphaAppear = AnimationUtils.loadAnimation(this, R.anim.alpha);
        scaleExpand = AnimationUtils.loadAnimation(this, R.anim.scale_expand);
        scaleShrink = AnimationUtils.loadAnimation(this, R.anim.scale_shrink);

        loadingProgress = (ProgressBar) findViewById(R.id.contents_progress);
        loadingProgress.setVisibility(View.GONE);

        contentsList = (ListView) findViewById(R.id.contents_list);

        if (getIntent().getStringExtra("name") != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        }
        if (getIntent().getStringExtra("contents_url") != null) {
            getCredentials();
            presenter = MainApp.getContentsPresenter(this, getIntent().getStringExtra("contents_url"), credentials);
            presenter.attachView(this);
        } else {
            Toast.makeText(this, "No repo url in this intent", Toast.LENGTH_SHORT).show();
        }
        presenter.attachView(this);
    }

    @Override
    public void onBackPressed() {
        if (!presenter.back()) super.onBackPressed();
    }

    private void getCredentials() {
        if (getIntent().getStringExtra("credentials") != null)
            this.credentials = getIntent().getStringExtra("credentials");
        else
            this.credentials = getSharedPreferences(getString(R.string.get_access), MODE_PRIVATE)
                    .getString(getString(R.string.get_access), "");
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
    public void setView(ContentsModel contentsModel) {
        if (contentsModel.getError() != null) {
            Toast.makeText(this, contentsModel.getError(), Toast.LENGTH_SHORT).show();
            presenter.back();
        }
        else if (contentsModel.getCodeContent() != null) {
            Intent viewIntent = new Intent(this, CodeViewActivity.class);
            viewIntent.putExtra("name", contentsModel.getCodeContentName());
            viewIntent.putExtra("lines", contentsModel.getLines());
            viewIntent.putExtra("content", contentsModel.getCodeContent());
            startActivity(viewIntent);
            presenter.back();
        } else {
            for (Map<String, String> content : contentsModel.getContents()) {
                if (content.get("type").equals("file"))
                    content.put("type", String.valueOf(R.drawable.file_black_24dp));
                else content.put("type", String.valueOf(R.drawable.folder_black_24dp));
            }
            contentsList.setAdapter(new SimpleAdapter(
                    this,
                    contentsModel.getContents(),
                    R.layout.list_item_contents,
                    new String[]{"name", "url", "type"},
                    new int[]{R.id.content_name, R.id.content_url, R.id.type_icon}));

            contentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView contentsUrl = view.findViewById(R.id.content_url);
                    presenter.updateHistory(contentsUrl.getText().toString());
                    presenter.loadData();
                }
            });
            contentsList.setAnimation(alphaAppear);
        }
    }
}
