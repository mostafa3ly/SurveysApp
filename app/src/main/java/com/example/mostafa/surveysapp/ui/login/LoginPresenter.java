package com.example.mostafa.surveysapp.ui.login;

import com.example.mostafa.surveysapp.base.BasePresenter;

public interface LoginPresenter extends BasePresenter{
    void checkIfLoggedIn();
    void onLoginUIResult(int resultCode, boolean isConnected);
}