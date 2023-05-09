package com.example.mapbox2.onBoarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mapbox2.Dashboard;
import com.example.mapbox2.LogIn;
import com.example.mapbox2.R;
import com.example.mapbox2.SignUp;
import com.example.mapbox2.adapters.OnBoardingAdapter;
import com.example.mapbox2.databinding.ActivityOnBoardingBinding;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {
    private ActivityOnBoardingBinding onBoardingBinding;
    private OnBoardingAdapter onBoardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBoardingBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding);

        // setting OnBoarding Items.
        setOnBoardingItems();
        setupOnBoardingIndicator();
        setCurrentOnBoardingIndicator(0);

        onBoardingBinding.ViewPager.setAdapter(onBoardingAdapter);

        // setting the current location of page.
        onBoardingBinding.ViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicator(position);
            }
        });

        onBoardingBinding.buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBoardingBinding.ViewPager.getCurrentItem() + 1 <
                        onBoardingAdapter.getItemCount()) {
                    onBoardingBinding.ViewPager.setCurrentItem(onBoardingBinding.ViewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(OnBoardingActivity.this, LogIn.class));
                    finish();
                }
            }
        });


    }


    // setting up the on boarding (title,description,image).
    private void setOnBoardingItems() {
        List<OnBoardingItem> onBoardingItems = new ArrayList<>();
        OnBoardingItem item = new OnBoardingItem();
        item.setTitle("We will be everywhere");
        item.setDescription("You can determine your location and the school bus will come to take them from the door of your home.");
        item.setImage(R.drawable.onboarding1);

        OnBoardingItem item2 = new OnBoardingItem();
        item2.setTitle("Rest assured at any time");
        item2.setDescription("You can check on your son from home, work, or anywhere through the app.");
        item2.setImage(R.drawable.onboarding2);

        OnBoardingItem item3 = new OnBoardingItem();
        item3.setTitle("Your child is in safe hands");
        item3.setDescription("With School Bus Tracking, you don't need to be worried about your son.");
        item3.setImage(R.drawable.onboarding3);

        onBoardingItems.add(item);
        onBoardingItems.add(item2);
        onBoardingItems.add(item3);

        onBoardingAdapter = new OnBoardingAdapter(onBoardingItems);
    }

    // setting up the views of indicators.
    private void setupOnBoardingIndicator() {
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            onBoardingBinding.layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    // setting up the views of indicators according to the index.
    private void setCurrentOnBoardingIndicator(int index) {
        int childCount = onBoardingBinding.layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) onBoardingBinding.layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if (index == onBoardingAdapter.getItemCount() - 1) {
            onBoardingBinding.buttonOnboardingAction.setText("Start");
        } else {
            onBoardingBinding.buttonOnboardingAction.setText("Next");
        }
    }
}