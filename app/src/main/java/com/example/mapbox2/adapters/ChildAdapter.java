package com.example.mapbox2.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapbox2.R;
import com.example.mapbox2.databinding.ChildItemViewBinding;
import com.example.mapbox2.models.ChildrenItem;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {
    private List<ChildrenItem> childList;
    private LayoutInflater layoutInflater;

    public ChildAdapter(List<ChildrenItem> childList) {
        this.childList = childList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        return new ChildViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.child_item_view,parent,false));
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
        private ChildItemViewBinding childItemViewBinding;
        public ChildViewHolder(@NonNull ChildItemViewBinding childItemViewBinding) {
            super(childItemViewBinding.getRoot());
            this.childItemViewBinding = childItemViewBinding;
        }
        void bind(ChildrenItem child){
            childItemViewBinding.setChild(child);
        }
    }

}
