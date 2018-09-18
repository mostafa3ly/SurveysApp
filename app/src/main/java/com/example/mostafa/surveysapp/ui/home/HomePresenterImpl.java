package com.example.mostafa.surveysapp.ui.home;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.mostafa.surveysapp.data.FireBaseDBHelper;
import com.example.mostafa.surveysapp.data.models.Survey;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class HomePresenterImpl implements HomePresenter {

    private HomeView view;

    public HomePresenterImpl(HomeView view) {
        this.view = view;
    }

    @Override
    public void signOut(Context context) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                view.onSignedOut();
            }
        });
    }

    @Override
    public void getUnansweredSurveys() {
        fireBaseDBHelper.getUnansweredSurveys(new FireBaseDBHelper.SurveysCallback() {
            @Override
            public void onSuccess(List<Survey> surveys) {
                view.showProgress(false);
                if (surveys.size() == 0)
                    view.showEmptyView();
                else
                    view.onSurveysFetched(surveys);
            }

            @Override
            public void onFailed() {
                view.showProgress(false);
            }
        });
    }

    @Override
    public void deAttachListener() {
        fireBaseDBHelper.removeListener();
    }
}
