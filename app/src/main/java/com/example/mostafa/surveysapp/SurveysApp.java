package com.example.mostafa.surveysapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class SurveysApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}