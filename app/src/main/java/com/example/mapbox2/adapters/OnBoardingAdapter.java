package com.example.mapbox2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapbox2.R;
import com.example.mapbox2.databinding.OnboardingItemBinding;
import com.example.mapbox2.onBoarding.OnBoardingItem;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {
    private List<OnBoardingItem> onBoardingItemList;
    private LayoutInflater layoutInflater;

    public OnBoardingAdapter(List<OnBoardingItem> onBoardingItemList) {
        this.onBoardingItemList = onBoardingItemList;
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        return new OnBoardingViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.onboarding_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.onboardingItemBinding.textTitle.setText(onBoardingItemList.get(position).getTitle());
        holder.onboardingItemBinding.textDescription.setText(onBoardingItemList.get(position).getDescription());
        holder.onboardingItemBinding.imageOnboarding.setImageResource(onBoardingItemList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return onBoardingItemList.size();
    }

    public class OnBoardingViewHolder extends RecyclerView.ViewHolder {
        private OnboardingItemBinding onboardingItemBinding;
        public OnBoardingViewHolder(@NonNull OnboardingItemBinding onboardingItemBinding) {
            super(onboardingItemBinding.getRoot());
            this.onboardingItemBinding = onboardingItemBinding;
        }
    }
}
