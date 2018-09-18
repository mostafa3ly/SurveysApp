package com.example.mostafa.surveysapp.ui.mySurveys;

import android.os.AsyncTask;

import com.example.mostafa.surveysapp.data.models.Survey;
import com.example.mostafa.surveysapp.data.FireBaseDBHelper.SurveysCallback;

import java.util.ArrayList;
import java.util.List;

public class SurveySearchTask extends AsyncTask<String, Void, ArrayList<Survey>> {

    private List<Survey> surveys;
    private SurveysCallback surveysCallback;


    public SurveySearchTask(List<Survey> surveys, SurveysCallback surveysCallback) {
        this.surveys = surveys;
        this.surveysCallback = surveysCallback;
    }


    @Override
    protected ArrayList<Survey> doInBackground(String... strings) {
        ArrayList<Survey> resultSurveys = new ArrayList<>();
        for (Survey survey : surveys) {
            if (survey.getTitle().contains(strings[0]))
                resultSurveys.add(survey);
        }
        return resultSurveys;
    }

    @Override
    protected void onPostExecute(ArrayList<Survey> surveys) {
        surveysCallback.onSuccess(surveys);
    }
}
