package com.example.mostafa.surveysapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mostafa.surveysapp.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.refresh)ImageButton refresh;


    private FirebaseAuth mAuth;
    private DatabaseReference mUsersReference;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mAuth = FirebaseAuth.getInstance();
        mUsersReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.users));
        checkLogin();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    private void checkLogin()
    {
        refresh.setVisibility(View.GONE);
        if(mAuth.getCurrentUser()==null)
        {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.drawable.logo)
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    providers)
                            .build(),
                    RC_SIGN_IN);
        }
        else {
            goToMain();
        }
    }

    private void addUser (final FirebaseUser firebaseUser)
    {
        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setUid(firebaseUser.getUid());
        if (firebaseUser.getPhotoUrl()!=null)
            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
        else {
            user.setPhotoUrl(getString(R.string.pic_url));
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(getString(R.string.pic_url)))
                    .build();
            firebaseUser.updateProfile(profileUpdates);
        }
        mUsersReference.child(user.getUid()).setValue(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (resultCode == RESULT_OK) {
                mUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(currentUser.getUid()))
                        {
                            addUser(currentUser);
                        }
                      goToMain();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else if (resultCode == RESULT_CANCELED) {
                if (!checkNetwork())
                {
                    Toast.makeText(this, getString(R.string.check_network_connection), Toast.LENGTH_SHORT).show();
                    refresh.setVisibility(View.VISIBLE);
                }
                else finish();
            }
        }
    }


    private boolean checkNetwork ()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void goToMain() {
        startActivity(new Intent(LoginActivity.this,SurveysListActivity.class));
        finish();
    }
}