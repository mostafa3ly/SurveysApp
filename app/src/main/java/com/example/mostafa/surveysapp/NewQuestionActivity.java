package com.example.mostafa.surveysapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mostafa.surveysapp.models.Question;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewQuestionActivity extends AppCompatActivity implements View.OnClickListener
{

    @BindView(R.id.question)EditText questionField;
    @BindView(R.id.options_list)RecyclerView optionsRecyclerView;
    @BindView(R.id.add_option)Button addOptionsButton;
    @BindView(R.id.remove_option)Button removeOptionButton;
    @BindView(R.id.submit)Button submitButton;
    private OptionsAdapter mOptionsAdapter;
    int mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        ButterKnife.bind(this);
        mType =  getIntent().getIntExtra(getString(R.string.type),0);
        if(mType==1)
        {
            addOptionsButton.setVisibility(View.GONE);
            optionsRecyclerView.setVisibility(View.INVISIBLE);
            mOptionsAdapter = new OptionsAdapter(new ArrayList<String>());
            removeOptionButton.setVisibility(View.GONE);
        }
        else
        {
            List<String> mandatoryOptions = new ArrayList<>();
            mandatoryOptions.add("");
            mandatoryOptions.add("");
            mOptionsAdapter = new OptionsAdapter(mandatoryOptions);
        }
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        optionsRecyclerView.setAdapter(mOptionsAdapter);

        submitButton.setOnClickListener(this);
        addOptionsButton.setOnClickListener(this);
        removeOptionButton.setOnClickListener(this);
        if(mOptionsAdapter.getOptions().size()==2)
            removeOptionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submit)
        {
            addQuestion();
        }
        else if(v.getId()==R.id.remove_option)
        {
            mOptionsAdapter.removeOption();
            if(mOptionsAdapter.getOptions().size()==2)
                removeOptionButton.setVisibility(View.INVISIBLE);
        }
        else{
            mOptionsAdapter.add("");
            removeOptionButton.setVisibility(View.VISIBLE);
        }
    }


    private void addQuestion()
    {
        if(questionField.getText().toString().isEmpty() || mOptionsAdapter.getOptions().contains(""))
        {
            Toast.makeText(this,getString(R.string.fill_blanks), Toast.LENGTH_SHORT).show();
        }
        else {
            Question question = new Question();
            question.setOptions(mOptionsAdapter.getOptions());
            question.setQuestion(questionField.getText().toString());
            question.setType(mType);
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.question),question);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }
}