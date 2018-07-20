package com.example.mostafa.surveysapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.mostafa.surveysapp.models.Survey;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyActivity extends AppCompatActivity {


    @BindView(R.id.questions_list)RecyclerView questionsRecyclerView;
    @BindView(R.id.submit_result)Button submitButton;

    private Survey mSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);
        mSurvey = getIntent().getParcelableExtra(getString(R.string.survey));
    }
}
