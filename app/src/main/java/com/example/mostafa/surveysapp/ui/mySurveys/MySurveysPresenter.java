package com.example.mostafa.surveysapp.ui.mySurveys;

import com.example.mostafa.surveysapp.base.BasePresenter;
import com.example.mostafa.surveysapp.data.models.Survey;

import java.util.List;

public interface MySurveysPresenter extends BasePresenter {
    void updateCurrentUserData();
    void getMySurveys();
    void searchOnSurvey(String keyword, List<Survey> mySurveys);
}
