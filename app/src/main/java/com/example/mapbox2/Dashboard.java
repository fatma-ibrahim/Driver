package com.example.mapbox2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mapbox2.databinding.ActivityDashboardBinding;
import com.example.mapbox2.responses.CountChildsResponse;
import com.example.mapbox2.services.LocationService;
import com.example.mapbox2.storage.SharedPreferencesManager;
import com.example.mapbox2.viewmodels.TripViewModel;
import com.pusher.pushnotifications.PushNotifications;

public class Dashboard extends AppCompatActivity {
    private ActivityDashboardBinding dashboardBinding;
    private TripViewModel tripViewModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        tripViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(TripViewModel.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Getting Trip Info");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        tripViewModel.count_childs("Bearer " + SharedPreferencesManager.getInstance(this).getToken()).observe(this, new Observer<CountChildsResponse>() {
            @Override
            public void onChanged(CountChildsResponse countChildsResponse) {
                progressDialog.dismiss();
                if(countChildsResponse!=null && countChildsResponse.isSuccess()){
                    dashboardBinding.totalChilds.setText(String.valueOf(countChildsResponse.getData().getTotalChildern()));
                    dashboardBinding.attendanceChilds.setText(String.valueOf(countChildsResponse.getData().getChildern()));
                    dashboardBinding.schoolName.setText(countChildsResponse.getData().getSchoolName());
                }else {
                    progressDialog.dismiss();
                }
            }
        });

        dashboardBinding.StartNavigate.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this, MainActivity.class));
        });

        dashboardBinding.NavigateBack.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this, NavigateBackActivity.class));
        });
        dashboardBinding.myProfile.setOnClickListener(v -> {
            startActivity(new Intent(Dashboard.this, Profile.class));
        });

        dashboardBinding.studentsAttendance.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this,AttendanceActivity.class));
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        if(!SharedPreferencesManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(Dashboard.this,LogIn.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(String.valueOf(103));
            startService(intent);
            Toast.makeText(this, "Location Service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }
}