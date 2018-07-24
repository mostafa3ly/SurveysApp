package com.example.mostafa.surveysapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafa.surveysapp.models.Survey;
import com.example.mostafa.surveysapp.widget.SurveyWidgetProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MySurveysActivity extends AppCompatActivity implements MySurveysAdapter.OnClickListener,MySurveysAdapter.OnPinPost{


    @BindView(R.id.profile_pic)CircleImageView profileImage;
    @BindView(R.id.username)TextView usernameTextView;
    @BindView(R.id.search)SearchView searchView;
    @BindView(R.id.my_surveys_list)RecyclerView mySurveysRecyclerView;
    @BindView(R.id.message)TextView messageTextView;
    private MySurveysAdapter mySurveysAdapter;
    private ArrayList<Survey> mMySurveys;
    private boolean isSearching ;
    private String mSearchWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_surveys);
        ButterKnife.bind(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(currentUser.getPhotoUrl()).into(profileImage);
        usernameTextView.setText(currentUser.getDisplayName());
        mMySurveys = getIntent().getParcelableArrayListExtra(getString(R.string.my_surveys));
        if(mMySurveys.size()==0) {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(getString(R.string.you_have_no_surveys));
        }
        mySurveysAdapter = new MySurveysAdapter(mMySurveys,this,this,this);
        mySurveysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySurveysRecyclerView.setAdapter(mySurveysAdapter);
        updateSearchView();
    }

    private void updateSearchView() {
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = true;
                mSearchWord = query;
                messageTextView.setVisibility(View.GONE);
                closeKeyboard();
                searchOnSurvey();
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
                isSearching = false;
                mySurveysAdapter.addAll(mMySurveys);
                messageTextView.setVisibility(View.GONE);
                searchView.clearFocus();
                closeKeyboard();
                return true;
            }
        });
    }

    private void closeKeyboard()
    {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.is_searching),isSearching);
        outState.putString(getString(R.string.search_word),mSearchWord);
        outState.putString(getString(R.string.input_text),searchView.getQuery().toString());
        outState.putParcelable(getString(R.string.position),mySurveysRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isSearching = savedInstanceState.getBoolean(getString(R.string.is_searching));
        mSearchWord = savedInstanceState.getString(getString(R.string.search_word));
        searchView.setQuery(savedInstanceState.getString(getString(R.string.input_text)),false);
        if(isSearching)searchOnSurvey();
        mySurveysRecyclerView.getLayoutManager().onRestoreInstanceState(
                savedInstanceState.getParcelable(getString(R.string.position)));
    }

    private void searchOnSurvey() {

        SurveySearchTask surveySearchTask = new SurveySearchTask(mMySurveys, new OnSearchFinished() {
            @Override
            public void onFinished(List<Survey> surveys) {
                if(surveys!=null) {
                    mySurveysAdapter.addAll(surveys);
                    if(surveys.size()==0)
                    messageTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        surveySearchTask.execute(mSearchWord);
    }

    @Override
    public void onClick(String id) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra(getString(R.string.id),id);
        startActivity(intent);
    }

    @Override
    public void onPinned(String id, String title) {
        Toast.makeText(this, getString(R.string.survey_pinned), Toast.LENGTH_SHORT).show();
        final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.survey),Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.id),id);
        editor.putString(getString(R.string.title),title);
        editor.putBoolean(getString(R.string.pin),true);
        editor.commit();
        DatabaseReference resultsReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.surveys)).child(id).child(getString(R.string.results));
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editor.putString(getString(R.string.results),dataSnapshot.getChildrenCount()+"");
                editor.commit();
                Intent intent = new Intent(MySurveysActivity.this, SurveyWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), SurveyWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        resultsReference.addValueEventListener(valueEventListener);

    }

    private static class SurveySearchTask extends AsyncTask<String, Void, ArrayList<Survey>> {

        private List<Survey> surveys;
        private OnSearchFinished onSearchFinished;

        SurveySearchTask(List<Survey> surveys,OnSearchFinished onSearchFinished) {
            this.surveys = surveys;
            this.onSearchFinished = onSearchFinished;
        }

        @Override
        protected ArrayList<Survey> doInBackground(String... strings) {
            ArrayList<Survey> resultSurveys = new ArrayList<>();
            for (Survey survey : surveys)
            {
                if(survey.getTitle().contains(strings[0]))
                    resultSurveys.add(survey);
            }
            return resultSurveys;
        }
        @Override
        protected void onPostExecute(ArrayList<Survey> surveys) {
            onSearchFinished.onFinished(surveys);
        }
    }

    private interface OnSearchFinished{
        void onFinished(List<Survey> surveys);
    }
}