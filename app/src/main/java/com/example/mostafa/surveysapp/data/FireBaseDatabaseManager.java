package com.example.mostafa.surveysapp.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.mostafa.surveysapp.data.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseDatabaseManager {
    private static final String USERS = "users";
    private static final String PIC_URL = "https://i.stack.imgur.com/dr5qp.jpg";
    private static FireBaseDatabaseManager INSTANCE ;
    private static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference USERS_REFERENCE = DATABASE_REFERENCE.child(USERS);

    private FireBaseDatabaseManager() {}

    public static FireBaseDatabaseManager getInstance() {
        if(INSTANCE==null)
            INSTANCE = new FireBaseDatabaseManager();
        return INSTANCE;
    }

    public FirebaseUser getCurrentUser ()
    {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void checkIfNewUser( final FireBaseManagerCallback callback)
    {

        USERS_REFERENCE.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getCurrentUser().getUid()))
                {
                    callback.onSuccess();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailed();
            }
        });
    }

    public void addNewUser()
    {
        User user = new User();
        user.setEmail(getCurrentUser().getEmail());
        user.setName(getCurrentUser().getDisplayName());
        user.setUid(getCurrentUser().getUid());
        if (getCurrentUser().getPhotoUrl() != null)
            user.setPhotoUrl(getCurrentUser().getPhotoUrl().toString());
        else {
            user.setPhotoUrl(PIC_URL);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(PIC_URL))
                    .build();
            getCurrentUser().updateProfile(profileUpdates);
        }
        USERS_REFERENCE.child(user.getUid()).setValue(user);
    }


    public interface FireBaseManagerCallback{
        void onSuccess();
        void onFailed();
    }

}
