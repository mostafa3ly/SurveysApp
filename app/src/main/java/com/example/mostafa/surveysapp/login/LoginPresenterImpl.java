package com.example.mostafa.surveysapp.login;

import com.example.mostafa.surveysapp.data.FireBaseDatabaseManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    private FireBaseDatabaseManager fireBaseDatabaseManager;


    LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        fireBaseDatabaseManager = FireBaseDatabaseManager.getInstance();
    }

    @Override
    public void checkIfLoggedIn() {
        loginView.showProgress(true);
        if (fireBaseDatabaseManager.getCurrentUser() != null) {
            loginView.openMainActivity();
        } else {
            loginView.openLoginUI(true);
        }
    }

    @Override
    public void onLoginUIResult(int resultCode, boolean isConnected) {
        if(resultCode == RESULT_OK)
        {
            loginView.openMainActivity();
            fireBaseDatabaseManager.checkIfNewUser( new FireBaseDatabaseManager.FireBaseManagerCallback() {
                @Override
                public void onSuccess() {
                    fireBaseDatabaseManager.addNewUser();
                }

                @Override
                public void onFailed() {
                    loginView.showProgress(false);
                }
            });
        }
        else if(resultCode == RESULT_CANCELED){
            if (!isConnected)
                loginView.showProgress(false);
            else loginView.openLoginUI(false);
        }
    }
}
