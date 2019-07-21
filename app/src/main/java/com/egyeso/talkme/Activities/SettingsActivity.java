package com.egyeso.talkme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.egyeso.talkme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Button UpdateAccountSettings;
    EditText userName , userStatus;
    CircleImageView userProfileImage;
    String currentUserID;
    FirebaseAuth mAuth;
    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        Initializefields();
        userName.setVisibility(View.INVISIBLE);
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){
                    String retrieveUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    String retrieveStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    String retrieveProfileImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrieveUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                    String retrieveStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                }else {
                    userName.setVisibility(View.INVISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please update your profile information...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Initializefields() {
        UpdateAccountSettings = findViewById(R.id.update_settings);
        UpdateAccountSettings.setOnClickListener(this);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.profile_image);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update_settings:
                UpdateSettings();
                break;
        }
    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();
        if (TextUtils.isEmpty(setUserName))
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(setStatus))
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setStatus);

            RootRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(SettingsActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
