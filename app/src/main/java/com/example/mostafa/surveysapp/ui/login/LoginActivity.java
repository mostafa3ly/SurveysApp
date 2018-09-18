package com.example.mostafa.surveysapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mostafa.surveysapp.R;
import com.example.mostafa.surveysapp.base.BaseActivity;
import com.example.mostafa.surveysapp.ui.home.HomeActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements LoginView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.refresh)
    ImageButton refresh;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.message)
    TextView message;

    private LoginPresenter presenter;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUnBinder(ButterKnife.bind(this));
        presenter = new LoginPresenterImpl(this);
        initViews();

    }

    @Override
    public void initViews() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.checkIfLoggedIn();
            }
        });
        presenter.checkIfLoggedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            presenter.onLoginUIResult(resultCode, isNetworkAvailable());
        }
    }


    @Override
    public void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
            message.setText(getString(R.string.loading));
        } else {
            progressBar.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
            message.setText(getString(R.string.check_network_connection));
        }
    }

    @Override
    public void openMainActivity() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void openLoginUI(boolean open) {
        if(open) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.logo)
                            .setAvailableProviders(
                                    providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.deAttachListener();
    }
}