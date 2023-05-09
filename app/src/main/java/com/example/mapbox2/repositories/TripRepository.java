package com.example.mapbox2.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.network.ApiClient;
import com.example.mapbox2.network.ApiService;
import com.example.mapbox2.responses.AttendenceResponse;
import com.example.mapbox2.responses.ChangeLocationResponse;
import com.example.mapbox2.responses.CountChildsResponse;
import com.example.mapbox2.responses.EndTripResponse;
import com.example.mapbox2.responses.NavigateBackResponse;
import com.example.mapbox2.responses.SchoolArrivedResponse;
import com.example.mapbox2.responses.StartTripResponse;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class TripRepository {
    private static final String TAG = "TripRepository";
    private ApiService apiService;

    public TripRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<StartTripResponse> startTrip(double latitude, double longitude, String token) {
        MutableLiveData<StartTripResponse> mutableLiveData = new MutableLiveData<>();
        Single<StartTripResponse> single = apiService.startTrip(latitude,longitude,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<StartTripResponse> observer = new SingleObserver<StartTripResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-startTrip");
            }

            @Override
            public void onSuccess(StartTripResponse startTripResponse) {
                Log.d(TAG, "( onSuccess )-startTrip"+startTripResponse.getData().getFathers().size());
                mutableLiveData.setValue(startTripResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorData(responseBody);
                    StartTripResponse startTripResponse = new StartTripResponse();
                    startTripResponse.setMessage(error);
                    Log.d(TAG,error);
                    mutableLiveData.setValue(startTripResponse);
                }else{
                    String body =e.getMessage();
                    StartTripResponse startTripResponse = new StartTripResponse();
                    startTripResponse.setMessage(body);
                    Log.d(TAG,body+"not http error");
                    mutableLiveData.setValue(startTripResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }

    public LiveData<NavigateBackResponse> navigateBack(double latitude, double longitude, String token) {
        MutableLiveData<NavigateBackResponse> mutableLiveData = new MutableLiveData<>();
        Single<NavigateBackResponse> single = apiService.navigateBack(latitude,longitude,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<NavigateBackResponse> observer = new SingleObserver<NavigateBackResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-startTrip");
            }

            @Override
            public void onSuccess(NavigateBackResponse navigateBackResponse) {
                Log.d(TAG, "( onSuccess )-startTrip"+navigateBackResponse.getData().getFathers().size());
                mutableLiveData.setValue(navigateBackResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorData(responseBody);
                    NavigateBackResponse navigateBackResponse = new NavigateBackResponse();
                    navigateBackResponse.setMessage(error);
                    Log.d(TAG,error);
                    mutableLiveData.setValue(navigateBackResponse);
                }else{
                    String body =e.getMessage();
                    NavigateBackResponse navigateBackResponse = new NavigateBackResponse();
                    navigateBackResponse.setMessage(body);
                    Log.d(TAG,body+"not http error");
                    mutableLiveData.setValue(navigateBackResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }


    public LiveData<ChangeLocationResponse> sendLocationToParent(double latitude, double longitude, int tripID, StringBuilder parentId, String token) {
        MutableLiveData<ChangeLocationResponse> mutableLiveData = new MutableLiveData<>();
        Single<ChangeLocationResponse> single = apiService.sendLocationToParent(latitude,longitude,tripID,parentId,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<ChangeLocationResponse> observer = new SingleObserver<ChangeLocationResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-sendLocationToParent");
            }

            @Override
            public void onSuccess(ChangeLocationResponse changeLocationResponse) {
                Log.d(TAG, "( onSuccess )-sendLocationToParent");
                mutableLiveData.setValue(changeLocationResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorMessage(responseBody);
                    ChangeLocationResponse changeLocationResponse = new ChangeLocationResponse();
                    changeLocationResponse.setMessage(error);
                    mutableLiveData.setValue(changeLocationResponse);
                }else{
                    String body =e.getMessage();
                    ChangeLocationResponse changeLocationResponse = new ChangeLocationResponse();
                    changeLocationResponse.setMessage(body);
                    Log.d(TAG,body);
                    mutableLiveData.setValue(changeLocationResponse);
                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }



    public LiveData<SchoolArrivedResponse> SchoolArrived(String token) {
        MutableLiveData<SchoolArrivedResponse> mutableLiveData = new MutableLiveData<>();
        Single<SchoolArrivedResponse> single = apiService.arrivedSchool(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<SchoolArrivedResponse> observer = new SingleObserver<SchoolArrivedResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-SchoolArrived");
            }

            @Override
            public void onSuccess(SchoolArrivedResponse schoolArrivedResponse) {
                Log.d(TAG, "( onSuccess )-SchoolArrived");
                mutableLiveData.setValue(schoolArrivedResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorMessage(responseBody);
                    Log.d(TAG,error);

                    SchoolArrivedResponse schoolArrivedResponse = new SchoolArrivedResponse();
                    schoolArrivedResponse.setMessage(error);
                    mutableLiveData.setValue(schoolArrivedResponse);
                }else{
                    String body =e.getMessage();
                    SchoolArrivedResponse schoolArrivedResponse = new SchoolArrivedResponse();
                    schoolArrivedResponse.setMessage(body);
                    Log.d(TAG,body);
                    mutableLiveData.setValue(schoolArrivedResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }

    public LiveData<AttendenceResponse> getAttendance(String token) {
        MutableLiveData<AttendenceResponse> mutableLiveData = new MutableLiveData<>();
        Single<AttendenceResponse> single = apiService.getAttendence(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<AttendenceResponse> observer = new SingleObserver<AttendenceResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-SchoolArrived");
            }

            @Override
            public void onSuccess(AttendenceResponse attendenceResponse) {
                Log.d(TAG, "( onSuccess )-SchoolArrived");
                mutableLiveData.setValue(attendenceResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorData(responseBody);
                    Log.d(TAG,error);

                    AttendenceResponse attendenceResponse = new AttendenceResponse();
                    attendenceResponse.setMessage(error);
                    mutableLiveData.setValue(attendenceResponse);
                }else{
                    String body =e.getMessage();
                    AttendenceResponse attendenceResponse = new AttendenceResponse();
                    attendenceResponse.setMessage(body);
                    Log.d(TAG,body);
                    mutableLiveData.setValue(attendenceResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }

    public LiveData<EndTripResponse> EndTrip(String token) {
        MutableLiveData<EndTripResponse> mutableLiveData = new MutableLiveData<>();
        Single<EndTripResponse> single = apiService.tripEnded(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<EndTripResponse> observer = new SingleObserver<EndTripResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-SchoolArrived");
            }

            @Override
            public void onSuccess(EndTripResponse endTripResponse) {
                Log.d(TAG, "( onSuccess )-SchoolArrived");
                mutableLiveData.setValue(endTripResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorData(responseBody);
                    Log.d(TAG,error);

                    EndTripResponse endTripResponse = new EndTripResponse();
                    endTripResponse.setMessage(error);
                    mutableLiveData.setValue(endTripResponse);
                }else{
                    String body =e.getMessage();
                    EndTripResponse endTripResponse = new EndTripResponse();
                    endTripResponse.setMessage(body);
                    Log.d(TAG,body);
                    mutableLiveData.setValue(endTripResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }

    public LiveData<CountChildsResponse> count_childs(String token) {
        MutableLiveData<CountChildsResponse> mutableLiveData = new MutableLiveData<>();
        Single<CountChildsResponse> single = apiService.count_childs(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        SingleObserver<CountChildsResponse> observer = new SingleObserver<CountChildsResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe-SchoolArrived");
            }

            @Override
            public void onSuccess(CountChildsResponse countChildsResponse) {
                Log.d(TAG, "( onSuccess )-SchoolArrived");
                mutableLiveData.setValue(countChildsResponse);
            }


            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException)e).response().errorBody();
                    String error = getErrorMessage(responseBody);
                    Log.d(TAG,error);

                    CountChildsResponse countChildsResponse = new CountChildsResponse();
                    countChildsResponse.setMessage(error);
                    mutableLiveData.setValue(countChildsResponse);
                }else{
                    String body =e.getMessage();
                    CountChildsResponse countChildsResponse = new CountChildsResponse();
                    countChildsResponse.setMessage(body);
                    Log.d(TAG,body);
                    mutableLiveData.setValue(countChildsResponse);

                }
            }
        };
        single.subscribe(observer);
        return mutableLiveData;
    }


    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String getErrorData(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("data");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
