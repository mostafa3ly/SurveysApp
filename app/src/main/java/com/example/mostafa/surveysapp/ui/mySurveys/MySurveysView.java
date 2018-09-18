package com.example.mostafa.surveysapp.ui.mySurveys;

import com.example.mostafa.surveysapp.base.BaseView;
import com.example.mostafa.surveysapp.data.models.Survey;

import java.util.List;

public interface MySurveysView extends BaseView {

    void setCurrentUserData(String name, String imageUrl);
    void showEmptyView(boolean show);
    void onSurveysFetched(List<Survey> surveys);

}
