package com.example.mapbox2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mapbox2.databinding.ActivityLogInBinding;
import com.example.mapbox2.responses.LoginResponse;
import com.example.mapbox2.storage.SharedPreferencesManager;
import com.example.mapbox2.viewmodels.AuthViewModel;

public class LogIn extends AppCompatActivity {
    private ActivityLogInBinding logInBinding;
    private AuthViewModel authViewModel;
    private String email;
    private String password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);
        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(AuthViewModel.class);
        email = logInBinding.textEmail.getText().toString();
        password = logInBinding.textPassword.getText().toString();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login is preparing");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        /** When User Click On Get Started **/
        logInBinding.getStarted.setOnClickListener(view -> {
            startActivity(new Intent(LogIn.this, SignUp.class));
            finish();
        });

        /** When User Click On Log In **/
        logInBinding.buttonLogin.setOnClickListener(view -> {
            email = logInBinding.textEmail.getText().toString();
            password = logInBinding.textPassword.getText().toString();
            if (logInBinding.textEmail.getText().toString().isEmpty() || logInBinding.textEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Email", Toast.LENGTH_SHORT).show();
                return;
            } else if (logInBinding.textPassword.getText().toString().isEmpty() || logInBinding.textPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Password", Toast.LENGTH_SHORT).show();
                return;
            } else if (logInBinding.textPassword.getText().toString().contains(".")) {
                Toast.makeText(this, "Your Password Contains Un Numerical Values", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.show();
            authViewModel.loginUser(email, password).observe(this, new Observer<LoginResponse>() {
                @Override
                public void onChanged(LoginResponse loginResponse) {
                    progressDialog.dismiss();
                    Log.d("hossamsad", loginResponse.getMessage());
                    if (loginResponse.isSuccess() == false) {
                        Toast.makeText(LogIn.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LogIn.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        SharedPreferencesManager.getInstance(LogIn.this)
                        .saveUser(loginResponse.getData().getDriver());
                        SharedPreferencesManager.getInstance(LogIn.this)
                        .saveToken(loginResponse.getData().getToken());
                        startActivity(new Intent(LogIn.this,Dashboard.class));
                    }
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(SharedPreferencesManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(LogIn.this,Dashboard.class));
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}