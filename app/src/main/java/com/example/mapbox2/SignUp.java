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

import com.example.mapbox2.databinding.ActivitySignUpBinding;
import com.example.mapbox2.responses.RegisterResponse;
import com.example.mapbox2.viewmodels.AuthViewModel;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding signUpBinding;
    private AuthViewModel authViewModel;
    ProgressDialog progressDialog;
    private String name;
    private String email;
    private String password;
    private String passwordConfirmation;
    private String licenseNumber;
    private String mobileNumber;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(AuthViewModel.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registration is preparing");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        /**If User Click On Login Button**/
        signUpBinding.btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUp.this, LogIn.class));
            finish();
        });

        /**If User Click On Get Started Button**/
        signUpBinding.buttonGetStarted.setOnClickListener(view -> {
            name = signUpBinding.textFirstName.getText().toString() + "" + signUpBinding.textLastName.getText().toString();
            email = signUpBinding.textEmail.getText().toString();
            mobileNumber = signUpBinding.textNumber.getText().toString();
            licenseNumber = signUpBinding.textlicenseNumber.getText().toString();
            password = signUpBinding.textPassword.getText().toString();
            passwordConfirmation = signUpBinding.textPasswordConfirm.getText().toString();
            code = signUpBinding.textSchoolCode.getText().toString();
            // Validation
            if (signUpBinding.textFirstName.getText().toString().isEmpty() || signUpBinding.textFirstName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your First Name", Toast.LENGTH_SHORT).show();
                return;
            } else if (signUpBinding.textLastName.getText().toString().isEmpty() || signUpBinding.textLastName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Last Name", Toast.LENGTH_SHORT).show();
                return;
            } else if (email.isEmpty() || email.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Email", Toast.LENGTH_SHORT).show();
                return;
            } else if (mobileNumber.isEmpty() ||mobileNumber.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Number", Toast.LENGTH_SHORT).show();
                return;
            } else if (mobileNumber.contains(".")) {
                Toast.makeText(this, "Your Number Contain Un Numerical Values", Toast.LENGTH_SHORT).show();
                return;
            } else if (licenseNumber.isEmpty() || licenseNumber.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your License Number", Toast.LENGTH_SHORT).show();
                return;
            } else if (licenseNumber.contains(".")) {
                Toast.makeText(this, "Your license Number Contain Un Numerical Values", Toast.LENGTH_SHORT).show();
                return;
            } else if (code.isEmpty() || code.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your School Code", Toast.LENGTH_SHORT).show();
                return;
            } else if (code.contains(".")) {
                Toast.makeText(this, "Your School Code Contain Un Numerical Values", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.isEmpty() || password.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Password", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordConfirmation.isEmpty() || passwordConfirmation.trim().isEmpty()) {
                Toast.makeText(this, "Please Fill Your Password Confirmation", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordConfirmation)) {
                Toast.makeText(this, "Your Password And Password Confirmation Doesn't match", Toast.LENGTH_SHORT).show();
                return;
            }
            // send details to server
            progressDialog.show();
            authViewModel.createUser(name, email, password, passwordConfirmation, licenseNumber, mobileNumber, code).observe(SignUp.this, new Observer<RegisterResponse>() {
                @Override
                public void onChanged(RegisterResponse registerResponse) {
                    progressDialog.dismiss();
                    Log.d("hossam", registerResponse.getMessage());
                    Toast.makeText(SignUp.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    if(registerResponse.getData().getCode() != null){
                        registerResponse.getData().getCode().forEach(code -> Toast.makeText(SignUp.this, code.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.getData().getName() != null){
                        registerResponse.getData().getName().forEach(name -> Toast.makeText(SignUp.this, name.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.getData().getEmail() != null){
                        registerResponse.getData().getEmail().forEach(email -> Toast.makeText(SignUp.this, email.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.getData().getMobileNumber() != null){
                        registerResponse.getData().getMobileNumber().forEach(mobileNumber -> Toast.makeText(SignUp.this, mobileNumber.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.getData().getLicenseNumber() != null){
                        registerResponse.getData().getLicenseNumber().forEach(licenseNumber -> Toast.makeText(SignUp.this, licenseNumber.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.getData().getPassword() != null){
                        registerResponse.getData().getPassword().forEach(password -> Toast.makeText(SignUp.this, password.toString(), Toast.LENGTH_SHORT).show());
                    }
                    if(registerResponse.isSuccess()!=false){
                        startActivity(new Intent(SignUp.this,LogIn.class));
                    }
                }
            });
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}