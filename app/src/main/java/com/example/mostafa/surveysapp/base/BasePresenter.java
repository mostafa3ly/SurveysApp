package com.example.mostafa.surveysapp.base;

import com.example.mostafa.surveysapp.data.FireBaseDBHelper;

public interface BasePresenter {
    FireBaseDBHelper fireBaseDBHelper = FireBaseDBHelper.getInstance();
    void deAttachListener();
}
