package com.example.mapbox2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.repositories.TripRepository;
import com.example.mapbox2.responses.AttendenceResponse;
import com.example.mapbox2.responses.ChangeLocationResponse;
import com.example.mapbox2.responses.CountChildsResponse;
import com.example.mapbox2.responses.EndTripResponse;
import com.example.mapbox2.responses.NavigateBackResponse;
import com.example.mapbox2.responses.SchoolArrivedResponse;
import com.example.mapbox2.responses.StartTripResponse;

import java.util.List;

public class TripViewModel extends ViewModel {
    private TripRepository tripRepository;

    public TripViewModel() {
        tripRepository = new TripRepository();
    }

    public LiveData<StartTripResponse> startTrip(double latitude, double longitude, String token) {
        return tripRepository.startTrip(latitude, longitude, token);
    }

    public LiveData<NavigateBackResponse> navigateBack(double latitude, double longitude, String token) {
        return tripRepository.navigateBack(latitude, longitude, token);
    }

    public LiveData<ChangeLocationResponse> sendLocationToParent(double latitude, double longitude, int tripID, StringBuilder parentId, String token) {
        return tripRepository.sendLocationToParent(latitude, longitude, tripID,parentId, token);
    }

    public LiveData<SchoolArrivedResponse> SchoolArrived(String token) {
        return tripRepository.SchoolArrived(token);
    }

    public LiveData<AttendenceResponse> getAttendance(String token) {
        return tripRepository.getAttendance(token);
    }

    public LiveData<CountChildsResponse> count_childs(String token) {
        return tripRepository.count_childs(token);
    }

        public LiveData<EndTripResponse> EndTrip(String token) {
        return tripRepository.EndTrip(token);
    }
}
