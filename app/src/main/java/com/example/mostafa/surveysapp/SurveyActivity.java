package com.example.mostafa.surveysapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.surveysapp.models.Question;
import com.example.mostafa.surveysapp.models.Result;
import com.example.mostafa.surveysapp.models.Survey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.options_list)
    RecyclerView optionsRecyclerView;
    @BindView(R.id.submit)
    Button submitButton;
    @BindView(R.id.next)
    Button nextButton;
    @BindView(R.id.progress)
    TextView progress;
    @BindView(R.id.question)
    TextView questionTextView;
    @BindView(R.id.essay_answer)
    EditText answerField;


    private Survey mSurvey;
    private int mCurrentPosition = 0;
    private int mQuestionCount;
    private AnswersAdapter mAnswersAdapter;
    private ArrayList<Question> mAnsweredQuestions;
    private ArrayList<Integer> mSelectedPositions;
    private ArrayList<String> mSelectedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);
        mSurvey = getIntent().getParcelableExtra(getString(R.string.survey));
        getSupportActionBar().setTitle(mSurvey.getTitle());
        mQuestionCount = mSurvey.getQuestions().size();
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAnsweredQuestions = new ArrayList<>();

        nextButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.position), mCurrentPosition);
        outState.putIntegerArrayList(getString(R.string.positions), mAnswersAdapter.getSelectedPositions());
        outState.putStringArrayList(getString(R.string.answers), mAnswersAdapter.getSelectedAnswers());
        outState.putParcelableArrayList(getString(R.string.questions), mAnsweredQuestions);
        outState.putString(getString(R.string.answer),answerField.getText().toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPosition = savedInstanceState.getInt(getString(R.string.position));
        mSelectedPositions = savedInstanceState.getIntegerArrayList(getString(R.string.positions));
        mSelectedAnswers = savedInstanceState.getStringArrayList(getString(R.string.answers));
        mAnsweredQuestions = savedInstanceState.getParcelableArrayList(getString(R.string.questions));
        answerField.setText(savedInstanceState.getString(getString(R.string.answer)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        previewQuestion();
    }

    private void closeKeyboard()
    {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null) {
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void previewQuestion() {
        closeKeyboard();
        mSelectedAnswers = new ArrayList<>();
        mSelectedPositions = new ArrayList<>();
        Question question = mSurvey.getQuestions().get(mCurrentPosition);
        questionTextView.setText(question.getQuestion());
        if (mCurrentPosition + 1 == mQuestionCount) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
        progress.setText(getString(R.string.progress, mCurrentPosition + 1, mQuestionCount));
        if (question.getType() != 1)
            answerField.setVisibility(View.GONE);
        switch (question.getType()) {
            case 1:
                mAnswersAdapter = new AnswersAdapter(new ArrayList<String>(),question.getType(),mSelectedAnswers,mSelectedPositions);
            case 2:
            case 3:
                mAnswersAdapter = new AnswersAdapter(question.getAnswers(), question.getType(), mSelectedAnswers, mSelectedPositions);
                optionsRecyclerView.setAdapter(mAnswersAdapter);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (isValidAnswer()) {
            addAnsweredQuestion();
            switch (v.getId()) {
                case R.id.submit:
                    submitResult();
                    break;
                case R.id.next:
                    mCurrentPosition++;
                    answerField.getText().clear();
                    previewQuestion();
                    break;
            }
        }
        else Toast.makeText(this,getString(R.string.answer_the_question),Toast.LENGTH_SHORT).show();
    }

    private void submitResult() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Result result = new Result();
        result.setOwnerId(currentUser.getUid());
        result.setQuestions(mAnsweredQuestions);
        result.setOwnerName(currentUser.getDisplayName());
        result.setOwnerPic(currentUser.getPhotoUrl().toString());
        DatabaseReference resultReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.surveys))
                .child(mSurvey.getId()).child(getString(R.string.results))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        resultReference.setValue(result);
        Toast.makeText(SurveyActivity.this, getString(R.string.poll_submitted), Toast.LENGTH_SHORT).show();
        finish();
    }


    private void addAnsweredQuestion() {
        Question question = mSurvey.getQuestions().get(mCurrentPosition);
        Question answeredQuestion = new Question();
        answeredQuestion.setType(question.getType());
        answeredQuestion.setQuestion(question.getQuestion());
        if (question.getType() == 1) {
            answeredQuestion.setAnswers(new ArrayList<>(Collections.singletonList(answerField.getText().toString())));
        } else {
            answeredQuestion.setAnswers(mAnswersAdapter.getSelectedAnswers());
        }
        mAnsweredQuestions.add(answeredQuestion);
    }

    private boolean isValidAnswer() {
        Question question = mSurvey.getQuestions().get(mCurrentPosition);
        if (question.getType() == 1) {
            return !answerField.getText().toString().trim().isEmpty();
        } else {
            return mAnswersAdapter.getSelectedAnswers().size() != 0;
        }
    }
}