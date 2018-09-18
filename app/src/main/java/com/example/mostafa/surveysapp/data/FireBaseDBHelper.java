package com.example.mostafa.surveysapp.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mostafa.surveysapp.data.models.Survey;
import com.example.mostafa.surveysapp.data.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseDBHelper {
    private static final String USERS = "users";
    private static final String RESULTS = "results";
    private static final String SURVEYS = "surveys";
    private static final String PIC_URL = "https://i.stack.imgur.com/dr5qp.jpg";
    private static FireBaseDBHelper INSTANCE;
    private static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    private FireBaseDBHelper() {
    }

    public static FireBaseDBHelper getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FireBaseDBHelper();
        return INSTANCE;
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void checkIfNewUser(final CheckUserCallback callback) {
        databaseReference = DATABASE_REFERENCE.child(USERS);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getCurrentUser().getUid())) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed();
            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);
    }

    public void getUnansweredSurveys(final SurveysCallback surveysCallback) {
        final String id = getCurrentUser().getUid();
        databaseReference = DATABASE_REFERENCE.child(SURVEYS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Survey> surveys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Survey survey = snapshot.getValue(Survey.class);
                    if(survey!=null) {
                        if (!snapshot.child(RESULTS).hasChild(id) && !survey.getOwnerId().equals(id))
                            surveys.add(survey);
                    }
                }
                surveysCallback.onSuccess(surveys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                surveysCallback.onFailed();
            }
        });
    }

    public void addNewUser() {
        FirebaseUser currentUser = getCurrentUser();
        User user = new User();
        user.setEmail(currentUser.getEmail());
        user.setName(currentUser.getDisplayName());
        user.setUid(currentUser.getUid());
        if (currentUser.getPhotoUrl() != null)
            user.setPhotoUrl(currentUser.getPhotoUrl().toString());
        else {
            user.setPhotoUrl(PIC_URL);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(PIC_URL))
                    .build();
            currentUser.updateProfile(profileUpdates);
        }
        databaseReference.child(user.getUid()).setValue(user);
    }

    public void getMySurveys(final SurveysCallback surveysCallback) {
        final String id = getCurrentUser().getUid();
        databaseReference = DATABASE_REFERENCE.child(SURVEYS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Survey> surveys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Survey survey = snapshot.getValue(Survey.class);
                    if(survey!=null) {
                        if (survey.getOwnerId().equals(id))
                            surveys.add(survey);
                    }
                }
                surveysCallback.onSuccess(surveys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                surveysCallback.onFailed();
            }
        });
    }


    public void removeListener() {
        if(eventListener !=null)
            databaseReference.removeEventListener(eventListener);
    }

    public interface CheckUserCallback {
        void onSuccess();

        void onFailed();
    }

    public interface SurveysCallback {
        void onSuccess(List<Survey> surveys);

        void onFailed();
    }

}
