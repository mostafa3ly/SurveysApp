package com.example.mostafa.surveysapp.ui.login;

import com.example.mostafa.surveysapp.data.FireBaseDBHelper;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView view;


    LoginPresenterImpl(LoginView view) {
        this.view = view;
    }

    @Override
    public void checkIfLoggedIn() {
        view.showProgress(true);
        if (fireBaseDBHelper.getCurrentUser() != null) {
            view.openMainActivity();
        } else {
            view.openLoginUI(true);
        }
    }

    @Override
    public void onLoginUIResult(int resultCode, boolean isConnected) {
        if(resultCode == RESULT_OK)
        {
            view.openMainActivity();
            fireBaseDBHelper.checkIfNewUser(new FireBaseDBHelper.CheckUserCallback() {
                @Override
                public void onSuccess() {
                    fireBaseDBHelper.addNewUser();
                }

                @Override
                public void onFailed() {
                    view.showProgress(false);
                }
            });
        }
        else if(resultCode == RESULT_CANCELED){
            if (!isConnected)
                view.showProgress(false);
            else view.openLoginUI(false);
        }
    }

    @Override
    public void deAttachListener() {
        fireBaseDBHelper.removeListener();
    }
}
