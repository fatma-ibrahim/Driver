package com.example.mapbox2.network;

import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.responses.AttendenceResponse;
import com.example.mapbox2.responses.ChangeLocationResponse;
import com.example.mapbox2.responses.CountChildsResponse;
import com.example.mapbox2.responses.EndTripResponse;
import com.example.mapbox2.responses.LoginResponse;
import com.example.mapbox2.responses.NavigateBackResponse;
import com.example.mapbox2.responses.RegisterResponse;
import com.example.mapbox2.responses.SchoolArrivedResponse;
import com.example.mapbox2.responses.ShowUserResponse;
import com.example.mapbox2.responses.StartTripResponse;
import com.example.mapbox2.responses.UpdateResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/driver/register")
    Single<RegisterResponse> createUser(@Field("name") String driverName,
                                        @Field("email") String email,
                                        @Field("password") String password,
                                        @Field("password_confirmation") String passwordConfirmation,
                                        @Field("licenseNumber") String licenseNumber,
                                        @Field("mobileNumber") String mobileNumber,
                                        @Field("code") String code);

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/driver/login")
    Single<LoginResponse> loginUser(@Field("email") String email,
                                    @Field("password") String password);

    @Headers("Accept: application/json")
    @GET("api/driver/show")
    Single<ShowUserResponse> showUser(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @GET("api/driver/count_childs")
    Single<CountChildsResponse> count_childs(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("api/driver/update")
    Single<UpdateResponse> updateUser(@Header("Authorization") String token
                                        , @Field("name") String driverName
                                        , @Field("email") String email
                                        , @Field("licenseNumber") String licenseNumber
                                        , @Field("mobileNumber") String mobileNumber
                                        , @Field("image_path") String image_path
    );


    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/trip/start")
    Single<StartTripResponse> startTrip(@Field("lit") double latitude, @Field("lng") double longitude, @Header("Authorization") String token);

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/trip/backHome")
    Single<NavigateBackResponse> navigateBack(@Field("lit") double latitude, @Field("lng") double longitude, @Header("Authorization") String token);



    @Headers("Accept: application/json")
    @FormUrlEncoded
    @PUT("api/changeLocation")
    Single<ChangeLocationResponse> sendLocationToParent(@Field("lit") double latitude, @Field("lng") double longitude, @Field("trip_id") int tripID, @Field("parent_id") StringBuilder parent_id, @Header("Authorization") String token);



    @Headers("Accept: application/json")
    @POST("api/trip/delivered")
    Single<SchoolArrivedResponse> arrivedSchool(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @POST("api/trip/end")
    Single<EndTripResponse> tripEnded(@Header("Authorization") String token);

    @Headers("Accept: application/json")
    @GET("api/trip/attendence")
    Single<AttendenceResponse> getAttendence(@Header("Authorization") String token);

}
