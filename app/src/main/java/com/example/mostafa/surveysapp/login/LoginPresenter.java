package com.example.mostafa.surveysapp.login;

public interface LoginPresenter {
    void checkIfLoggedIn();
    void onLoginUIResult(int resultCode, boolean isConnected);
}