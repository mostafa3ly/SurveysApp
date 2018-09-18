package com.example.mostafa.surveysapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.mostafa.surveysapp.ui.mySurveys.MySurveysActivity;
import com.example.mostafa.surveysapp.NewSurveyActivity;
import com.example.mostafa.surveysapp.R;
import com.example.mostafa.surveysapp.SurveyActivity;
import com.example.mostafa.surveysapp.ui.adapters.SurveysAdapter;
import com.example.mostafa.surveysapp.base.BaseActivity;
import com.example.mostafa.surveysapp.data.models.Survey;
import com.example.mostafa.surveysapp.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements HomeView,SurveysAdapter.OnClickListener {


    @BindView(R.id.surveys_list)
    RecyclerView surveysRecyclerView;
    @BindView(R.id.empty_surveys)
    LinearLayout emptyView;
    @BindView(R.id.progress)
    ProgressBar progressBar;

    private SurveysAdapter mSurveysAdapter;

    private HomePresenter presenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys_list);
        ButterKnife.bind(this);
        presenter = new HomePresenterImpl(this);
        initViews();
    }

    @Override
    public void initViews() {
        mSurveysAdapter = new SurveysAdapter(new ArrayList<Survey>(), this, this);
        surveysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        surveysRecyclerView.setAdapter(mSurveysAdapter);
        presenter.getUnansweredSurveys();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getUnansweredSurveys();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.deAttachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sign_out:
                presenter.signOut(this);
                return true;
            case R.id.new_survey:
                startActivity(new Intent(this, NewSurveyActivity.class));
                return true;
            case R.id.my_surveys:
                startActivity(new Intent(this, MySurveysActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Survey survey) {
        Intent intent = new Intent(this, SurveyActivity.class);
        intent.putExtra(getString(R.string.survey), survey);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.position), surveysRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onSurveysFetched(List<Survey> surveys) {
        mSurveysAdapter.addAll(surveys);
    }

    @Override
    public void onSignedOut() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }


}