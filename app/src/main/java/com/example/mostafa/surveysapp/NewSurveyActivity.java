package com.example.mostafa.surveysapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mostafa.surveysapp.data.models.Question;
import com.example.mostafa.surveysapp.data.models.Survey;
import com.example.mostafa.surveysapp.ui.adapters.QuestionsAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSurveyActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 88;

    @BindView (R.id.questions_list) RecyclerView questionsRecyclerView;
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
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishSurvey();
            }
        });
        newQuestionMenu.setClosedOnTouchOutside(true);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(NewSurveyActivity.this).create();
                    alertDialog.setTitle(getString(R.string.warning));
                    alertDialog.setMessage(getString(R.string.deleting_question_message));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mQuestionsAdapter.remove(viewHolder.getAdapterPosition());
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mQuestionsAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(questionsRecyclerView);
    }


    private void closeKeyboard()
    {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void openKeyboard(){
        titleField.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(titleField, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void publishSurvey()
    {
        if (titleField.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.add_survey_title), Toast.LENGTH_SHORT).show();
            openKeyboard();
            return;
        }
        else if (mQuestionsAdapter.getQuestions().size()==0)
        {
            Toast.makeText(this, getString(R.string.no_questions_added), Toast.LENGTH_SHORT).show();
            titleField.clearFocus();
            closeKeyboard();
            return;
        }

        titleField.clearFocus();
        closeKeyboard();

        Survey survey = new Survey();
        survey.setOwnerPic(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        survey.setQuestions(mQuestionsAdapter.getQuestions());
        survey.setOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        survey.setTitle(titleField.getText().toString());
        DatabaseReference surveysReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.surveys));
        String id  = surveysReference.push().getKey();
        survey.setId(id);
        publishButton.setEnabled(false);
        surveysReference.child(id).setValue(survey);
        Toast.makeText(NewSurveyActivity.this, getString(R.string.survey_published_successfully), Toast.LENGTH_SHORT).show();
        finish();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.questions),mQuestionsAdapter.getQuestions());
        outState.putString(getString(R.string.survey_title),titleField.getText().toString());
        outState.putParcelable(getString(R.string.position),questionsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Question> questions = savedInstanceState.getParcelableArrayList(getString(R.string.questions));
        mQuestionsAdapter.add(questions);
        titleField.setText(savedInstanceState.getString(getString(R.string.survey_title)));
        questionsRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.position)));
    }

    @Override
    public void onClick(View v) {
        int type ;
        if(v.getId()==R.id.essay) type = 1;
        else if (v.getId()==R.id.single_choice) type = 2;
        else type = 3;
        newQuestionMenu.close(true);
        titleField.clearFocus();
        closeKeyboard();
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