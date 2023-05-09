package com.example.mapbox2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.mapbox2.repositories.UserRepository;
import com.example.mapbox2.responses.ShowUserResponse;
import com.example.mapbox2.responses.UpdateResponse;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<ShowUserResponse> getUserInfo(String token){
        return userRepository.getUserInfo(token);
    }

    public LiveData<UpdateResponse> updateUser(String token, String name, String email, String licenseNumber, String mobileNumber,String image_path){
        return userRepository.updateUser(token,name,email,licenseNumber,mobileNumber,image_path);
    }

}
