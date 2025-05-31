package com.hesabix.katebposapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.Commodity;

import java.util.ArrayList;
import java.util.List;

public class CommoditiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;

    private final Context context;
    private final List<Commodity> commodities;
    private boolean isLoading;
    private final OnCommodityClickListener listener;

    public interface OnCommodityClickListener {
        void onCommodityClick(Commodity commodity);
        void onCommodityLongClick(Commodity commodity);
    }

    public CommoditiesAdapter(Context context, OnCommodityClickListener listener) {
        this.context = context;
        this.commodities = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_commodity, parent, false);
            return new CommodityViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommodityViewHolder) {
            Commodity commodity = commodities.get(position);
            CommodityViewHolder viewHolder = (CommodityViewHolder) holder;
            
            viewHolder.tvCode.setText(commodity.getCode());
            viewHolder.tvName.setText(commodity.getName());
            viewHolder.tvUnit.setText(commodity.getUnit());
            viewHolder.tvPrice.setText(String.format("%,d ریال", (long)commodity.getPriceSell()));
            viewHolder.tvCount.setText(String.format("%,d", (long)commodity.getCount()));

            holder.itemView.setOnClickListener(v -> listener.onCommodityClick(commodity));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onCommodityLongClick(commodity);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return commodities.size() + (isLoading ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position < commodities.size() ? TYPE_ITEM : TYPE_LOADING;
    }

    public void setCommodities(List<Commodity> commodities) {
        this.commodities.clear();
        this.commodities.addAll(commodities);
        notifyDataSetChanged();
    }

    public void addCommodities(List<Commodity> commodities) {
        int startPosition = this.commodities.size();
        this.commodities.addAll(commodities);
        notifyItemRangeInserted(startPosition, commodities.size());
    }

    public void setLoading(boolean loading) {
        if (this.isLoading != loading) {
            this.isLoading = loading;
            if (loading) {
                notifyItemInserted(commodities.size());
            } else {
                notifyItemRemoved(commodities.size());
            }
        }
    }

    public void clear() {
        commodities.clear();
        notifyDataSetChanged();
    }

    private static class CommodityViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvUnit, tvPrice, tvCount;

        CommodityViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_code);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
} 