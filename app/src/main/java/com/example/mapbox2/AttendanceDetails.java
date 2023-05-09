package com.example.mapbox2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.example.mapbox2.adapters.ChildAdapter;
import com.example.mapbox2.databinding.ActivityAttendanceDetailsBinding;
import com.example.mapbox2.models.ChildrenItem;
import com.example.mapbox2.responses.LoginResponse;

import java.util.List;

public class AttendanceDetails extends AppCompatActivity {
    private ActivityAttendanceDetailsBinding detailsBinding;
    private List<ChildrenItem> childrenItems;
    private ChildAdapter childAdapter;
    private String fatherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_attendance_details);
        doIntialization();

        detailsBinding.imageBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void doIntialization(){
        childrenItems = (List<ChildrenItem>) getIntent().getSerializableExtra("children");
        fatherName = getIntent().getStringExtra("fatherName");
        detailsBinding.title.setText("Children's Attendance Of "+fatherName);
        childAdapter = new ChildAdapter(childrenItems);
        detailsBinding.recyclerView.setAdapter(childAdapter);
        LayoutAnimationController animationController =
                AnimationUtils.loadLayoutAnimation(AttendanceDetails.this, R.anim.layout_animation);
        detailsBinding.recyclerView.setLayoutAnimation(animationController);
    }

}