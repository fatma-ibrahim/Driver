package com.example.mapbox2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapbox2.R;
import com.example.mapbox2.databinding.FatherItemBinding;
import com.example.mapbox2.models.FatherAttendence;


import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ChildViewHolder> {
    private List<FatherAttendence> childList;
    private LayoutInflater layoutInflater;
    private FatherClick fatherClick;

    public AttendanceAdapter(List<FatherAttendence> childList, FatherClick fatherClick) {
        this.childList = childList;
        this.fatherClick = fatherClick;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        return new ChildViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.father_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        holder.bind(childList.get(position));
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        private FatherItemBinding fatherItemBinding;
        public ChildViewHolder(@NonNull FatherItemBinding fatherItemBinding) {
            super(fatherItemBinding.getRoot());
            this.fatherItemBinding = fatherItemBinding;
            fatherItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FatherAttendence fatherAttendence = childList.get(getAdapterPosition());
                    fatherClick.onFatherClickListener(fatherAttendence);
                }
            });

        }
        void bind(FatherAttendence fatherAttendence){
            fatherItemBinding.setFather(fatherAttendence);
        }
    }
    public interface FatherClick{
        void onFatherClickListener(FatherAttendence fatherAttendence);
    }
}
