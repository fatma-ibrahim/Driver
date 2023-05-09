package com.example.mapbox2.viewmodels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapbox2.repositories.AuthRepository;
import com.example.mapbox2.responses.LoginResponse;
import com.example.mapbox2.responses.RegisterResponse;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public LiveData<RegisterResponse> createUser(String name, String email, String password, String passwordConfirmation, String licenseNumber, String mobileNumber, String code) {
        return authRepository.createUser(name, email, password, passwordConfirmation, licenseNumber, mobileNumber, code);
    }

    public LiveData<LoginResponse> loginUser(String email, String password) {
        return authRepository.loginUser(email, password);
    }

}
