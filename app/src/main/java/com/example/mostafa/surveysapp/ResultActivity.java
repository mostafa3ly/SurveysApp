package com.example.mostafa.surveysapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.mostafa.surveysapp.data.models.Question;
import com.example.mostafa.surveysapp.data.models.Result;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResultActivity extends AppCompatActivity {


    @BindView(R.id.profile_pic)CircleImageView profileImage;
    @BindView(R.id.username)TextView usernameTextView;
    @BindView(R.id.questions_list)RecyclerView questionsRecyclerView;

    private Result mResult;
    private QuestionsAdapter mQuestionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        mResult = getIntent().getParcelableExtra(getString(R.string.result));
        Picasso.get().load(mResult.getOwnerPic()).into(profileImage);
        usernameTextView.setText(mResult.getOwnerName());
        mQuestionsAdapter = new QuestionsAdapter(new ArrayList<Question>(),this);
        mQuestionsAdapter.addAll(mResult.getQuestions());
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(mQuestionsAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.position),questionsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questionsRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.position)));
    }
}
