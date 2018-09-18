package com.example.mostafa.surveysapp.ui.home;

import com.example.mostafa.surveysapp.base.BaseView;
import com.example.mostafa.surveysapp.data.models.Survey;

import java.util.List;

public interface HomeView extends BaseView {
    void onSurveysFetched(List<Survey> surveys);
    void onSignedOut();
    void showEmptyView();
}
