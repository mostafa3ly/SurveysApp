package com.example.mostafa.surveysapp.ui.home;

import android.content.Context;

import com.example.mostafa.surveysapp.base.BasePresenter;

public interface HomePresenter extends BasePresenter {
    void signOut(Context context);
    void getUnansweredSurveys();
}
