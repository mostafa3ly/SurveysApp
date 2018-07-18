package com.example.mostafa.surveysapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mostafa.surveysapp.models.Question;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSurveyActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 88;

    @BindView (R.id.questions_list)
    RecyclerView questionsRecyclerView;
    @BindView(R.id.essay)FloatingActionButton essayButton;
    @BindView(R.id.single_choice)FloatingActionButton singleChoiceButton;
    @BindView(R.id.add_question)FloatingActionMenu newQuestionMenu;
    @BindView(R.id.multi_choice)FloatingActionButton multiChoiceButton;
    @BindView(R.id.survey_title)EditText titleField;
    @BindView(R.id.publish)Button publishButton;

    private QuestionsAdapter mQuestionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);
        ButterKnife.bind(this);
        mQuestionsAdapter = new QuestionsAdapter(new ArrayList<Question>(),this);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(mQuestionsAdapter);
        essayButton.setOnClickListener(this);
        singleChoiceButton.setOnClickListener(this);
        multiChoiceButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int type;
        if(v.getId()==R.id.essay)
            type = 1;
        else if (v.getId()==R.id.single_choice)
            type = 2;
        else
            type = 3;
        newQuestionMenu.close(true);
        addNewQuestion(type);
    }

    private void addNewQuestion(int type) {
        Intent intent = new Intent(this,NewQuestionActivity.class);
        intent.putExtra(getString(R.string.type),type);
        startActivityForResult(intent,REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                mQuestionsAdapter.add((Question)data.getParcelableExtra(getString(R.string.question)));
            }

        }
    }
}
