package com.example.mapbox2;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mapbox2.adapters.AttendanceAdapter;
import com.example.mapbox2.adapters.ChildAdapter;
import com.example.mapbox2.databinding.ActivityAttendanceBinding;
import com.example.mapbox2.models.FatherAttendence;
import com.example.mapbox2.responses.AttendenceResponse;
import com.example.mapbox2.storage.SharedPreferencesManager;
import com.example.mapbox2.viewmodels.AuthViewModel;
import com.example.mapbox2.viewmodels.TripViewModel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import stream.customalert.CustomAlertDialogue;

public class AttendanceActivity extends AppCompatActivity implements AttendanceAdapter.FatherClick {
    private static final String TAG = "AttendanceActivity";
    private ActivityAttendanceBinding attendanceBinding;
    private List<FatherAttendence> fatherAttendences;
    private AttendanceAdapter attendanceAdapter;
    private TripViewModel tripViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceBinding = DataBindingUtil.setContentView(this, R.layout.activity_attendance);
        tripViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(TripViewModel.class);

        /** Recycler View **/
        fatherAttendences = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(fatherAttendences, this);
        attendanceBinding.recyclerView.setAdapter(attendanceAdapter);

        getAttendance();
    }

    private void getAttendance() {
        attendanceBinding.progress.setVisibility(View.VISIBLE);
        tripViewModel.getAttendance("Bearer " + SharedPreferencesManager.getInstance(this).getToken()).observe(this, new Observer<AttendenceResponse>() {
            @Override
            public void onChanged(AttendenceResponse attendenceResponse) {
                attendanceBinding.progress.setVisibility(View.INVISIBLE);
                if (attendenceResponse != null && attendenceResponse.isSuccess()) {
                    Toast.makeText(AttendanceActivity.this, attendenceResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    LayoutAnimationController animationController =
                            AnimationUtils.loadLayoutAnimation(AttendanceActivity.this, R.anim.layout_animation);
                    attendanceBinding.recyclerView.setLayoutAnimation(animationController);
                    fatherAttendences.addAll(attendenceResponse.getData());
                    attendanceAdapter.notifyDataSetChanged();
                    Log.d(TAG, attendenceResponse.getMessage());
                } else if (attendenceResponse != null && !attendenceResponse.isSuccess()) {
                    Log.d(TAG, attendenceResponse.getMessage());
                    showDialog("Sorry",attendenceResponse.getMessage());
                }
            }
        });
    }

    @Override
    public void onFatherClickListener(FatherAttendence fatherAttendence) {
        Intent intent = new Intent(AttendanceActivity.this,AttendanceDetails.class);
        intent.putExtra("children",(Serializable) fatherAttendence.getChildren());
        intent.putExtra("fatherName", fatherAttendence.getName());
        startActivity(intent);
    }

    private void showDialog(String title, String message) {
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(AttendanceActivity.this)
                .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                .setTitle(title)
                .setMessage(message)
                .setNegativeText("OK")
                .setNegativeColor(R.color.purple_200)
                .setNegativeTypeface(Typeface.DEFAULT_BOLD)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setDecorView(getWindow().getDecorView())
                .build();
        alert.show();
    }

}

