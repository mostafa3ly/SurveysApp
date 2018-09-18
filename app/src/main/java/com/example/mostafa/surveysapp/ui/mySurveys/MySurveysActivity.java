package com.example.mostafa.surveysapp.ui.mySurveys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mostafa.surveysapp.R;
import com.example.mostafa.surveysapp.ResultsActivity;
import com.example.mostafa.surveysapp.base.BaseActivity;
import com.example.mostafa.surveysapp.data.models.Survey;
import com.example.mostafa.surveysapp.ui.adapters.MySurveysAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MySurveysActivity extends BaseActivity implements MySurveysAdapter.OnClickListener, MySurveysView {


    @BindView(R.id.profile_pic)
    CircleImageView profileImage;
    @BindView(R.id.username)
    TextView usernameTextView;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.my_surveys_list)
    RecyclerView mySurveysRecyclerView;
    @BindView(R.id.message)
    TextView messageTextView;
    private MySurveysAdapter mySurveysAdapter;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private String mSearchWord;
    private List<Survey> mySurveys;
    private boolean isFetched = false;

    private MySurveysPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_surveys);
        setUnBinder(ButterKnife.bind(this));
        presenter = new MySurveysPresenterImpl(this);
        initViews();
    }

    @Override
    public void initViews() {
        mySurveysAdapter = new MySurveysAdapter(new ArrayList<Survey>(), this, this);
        mySurveysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySurveysRecyclerView.setAdapter(mySurveysAdapter);
        updateSearchView();
        presenter.updateCurrentUserData();
        presenter.getMySurveys();

    }

    @Override
    public void setCurrentUserData(String name, String imageUrl) {
        Picasso.get().load(imageUrl).into(profileImage);
        usernameTextView.setText(name);
    }

    @Override
    public void showEmptyView(boolean show) {

        if (show)
            messageTextView.setVisibility(View.VISIBLE);
        else
            messageTextView.setVisibility(View.GONE);
    }

    @Override
    public void onSurveysFetched(List<Survey> surveys) {
        if (!isFetched) {
            mySurveys = surveys;
            isFetched = true;
        }
        mySurveysAdapter.addAll(surveys);
    }

    private void updateSearchView() {
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchWord = query;
                closeKeyboard();
                presenter.searchOnSurvey(mSearchWord, mySurveys);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                presenter.getMySurveys();
                searchView.clearFocus();
                closeKeyboard();
                return true;
            }
        });
    }


    @Override
    public void onClick(String id) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(getString(R.string.id), id);
        startActivity(intent);
    }

    @Override
    public void showProgress(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}