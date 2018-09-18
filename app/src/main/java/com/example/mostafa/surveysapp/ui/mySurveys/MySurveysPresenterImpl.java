package com.example.mostafa.surveysapp.ui.mySurveys;

import android.util.Log;

import com.example.mostafa.surveysapp.data.FireBaseDBHelper;
import com.example.mostafa.surveysapp.data.models.Survey;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MySurveysPresenterImpl implements MySurveysPresenter {

    private MySurveysView view;

    public MySurveysPresenterImpl(MySurveysView view) {
        this.view = view;
    }

    @Override
    public void updateCurrentUserData() {
        FirebaseUser currentUser = fireBaseDBHelper.getCurrentUser();
        view.setCurrentUserData(currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
    }

    @Override
    public void getMySurveys() {
        Log.i("sss", "shon");
        fireBaseDBHelper.getMySurveys(getSurveysCallback());
    }

    @Override
    public void searchOnSurvey(String keyword, List<Survey> mySurveys) {
        SurveySearchTask surveySearchTask = new SurveySearchTask(mySurveys, getSurveysCallback());
        surveySearchTask.execute(keyword);
    }

    @Override
    public void deAttachListener() {
        fireBaseDBHelper.removeListener();
    }

    private FireBaseDBHelper.SurveysCallback getSurveysCallback() {
        return new FireBaseDBHelper.SurveysCallback() {
            @Override
            public void onSuccess(List<Survey> surveys) {
                view.showProgress(false);
                view.showEmptyView(false);
                if (surveys.size() == 0)
                    view.showEmptyView(true);
                view.onSurveysFetched(surveys);
            }

            @Override
            public void onFailed() {
                view.showProgress(false);
            }
        };
    }
}
