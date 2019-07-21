package com.egyeso.talkme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.egyeso.talkme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Button LoginButton , PhoneLoginButton;
    TextInputLayout UserEmail , UserPassword;
    TextView NeedNewAccountLink , ForgetPasswordLink;
    ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        InitializeFields();
    }

    private void InitializeFields() {
        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(this);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        PhoneLoginButton.setOnClickListener(this);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeedNewAccountLink = findViewById(R.id.need_new_account_account_link);
        NeedNewAccountLink.setOnClickListener(this);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        ForgetPasswordLink.setOnClickListener(this);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                LoginUser();
                break;
            case R.id.phone_login_button:

                break;
            case R.id.need_new_account_account_link:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.forget_password_link:
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                break;
        }
    }

    private void LoginUser() {
        String email = Objects.requireNonNull(UserEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(UserPassword.getEditText()).getText().toString().trim();
        if (TextUtils.isEmpty(email))
            Toast.makeText(this, "Please enter email..", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(password))
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                        Toast.makeText(LoginActivity.this, "Logged in Successfully..", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }else {
                        Toast.makeText(LoginActivity.this, "Error : " + task.getResult().toString() , Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
}
