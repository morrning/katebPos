package com.hesabix.katebposapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.retrofit.Business;
import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {
    private List<Business> businessList;
    private final OnBusinessClickListener listener;

    public interface OnBusinessClickListener {
        void onBusinessClick(Business business);
    }

    public BusinessAdapter(List<Business> businessList, OnBusinessClickListener listener) {
        this.businessList = businessList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        Business business = businessList.get(position);
        holder.nameTextView.setText(business.getName());
        holder.ownerTextView.setText(business.getOwner());
        holder.itemView.setOnClickListener(v -> listener.onBusinessClick(business));
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public void updateList(List<Business> newList) {
        businessList = newList;
        notifyDataSetChanged();
    }

    static class BusinessViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ownerTextView;

        BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.business_name);
            ownerTextView = itemView.findViewById(R.id.business_owner);
        }
    }
}