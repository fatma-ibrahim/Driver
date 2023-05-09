package com.example.mapbox2.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapbox2.network.ApiClient;
import com.example.mapbox2.network.ApiService;
import com.example.mapbox2.responses.LoginResponse;
import com.example.mapbox2.responses.RegisterResponse;
import com.example.mapbox2.responses.ShowUserResponse;
import com.example.mapbox2.responses.UpdateResponse;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class UserRepository {
    private static final String TAG = "userRepository";
    private ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<ShowUserResponse> getUserInfo(String token)
    {
        MutableLiveData<ShowUserResponse> responseMutableLiveData = new MutableLiveData<>();
        Single<ShowUserResponse> single = apiService.showUser(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<ShowUserResponse> singleObserver = new SingleObserver<ShowUserResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe");
            }

            @Override
            public void onSuccess(ShowUserResponse showUserResponse) {
                Log.d(TAG,"onSuccess");
                responseMutableLiveData.setValue(showUserResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorMessage(responseBody);
                    ShowUserResponse showUserResponse = new ShowUserResponse();
                    showUserResponse.setMessage(error);
                    responseMutableLiveData.setValue(showUserResponse);
                }else{
                    String body =e.getMessage();
                    ShowUserResponse showUserResponse = new ShowUserResponse();
                    showUserResponse.setMessage(body);
                    Log.d(TAG,body);
                    responseMutableLiveData.setValue(showUserResponse);

                }
            }
        };
        single.subscribe(singleObserver);
        return responseMutableLiveData;
    }
    public LiveData<UpdateResponse> updateUser(String token,String name,String email,String licenseNumber,String mobileNumber,String image_path)
    {
        MutableLiveData<UpdateResponse> responseMutableLiveData = new MutableLiveData<>();
        Single<UpdateResponse> single = apiService.updateUser(token,name,email,licenseNumber,mobileNumber,image_path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<UpdateResponse> singleObserver = new SingleObserver<UpdateResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubscribe");
            }

            @Override
            public void onSuccess(UpdateResponse updateResponse) {
                Log.d(TAG,"onSuccess");
                responseMutableLiveData.setValue(updateResponse);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorMessage(responseBody);
                    UpdateResponse updateResponse = new UpdateResponse();
                    updateResponse.setMessage(error);
                    responseMutableLiveData.setValue(updateResponse);
                }else{
                    String body =e.getMessage();
                    UpdateResponse updateResponse = new UpdateResponse();
                    updateResponse.setMessage(body);
                    Log.d(TAG,body);
                    responseMutableLiveData.setValue(updateResponse);

                }
            }
        };
        single.subscribe(singleObserver);
        return responseMutableLiveData;
    }
    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }


}
