package com.hesabix.katebposapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.SellDoc;

import java.util.ArrayList;
import java.util.List;

public class SellDocsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;

    private final Context context;
    private final List<SellDoc> sellDocs;
    private boolean isLoading;
    private final OnSellDocClickListener listener;

    public interface OnSellDocClickListener {
        void onSellDocClick(SellDoc sellDoc);
        void onSellDocLongClick(SellDoc sellDoc);
    }

    public SellDocsAdapter(Context context, OnSellDocClickListener listener) {
        this.context = context;
        this.sellDocs = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sell_doc, parent, false);
            return new SellDocViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SellDocViewHolder) {
            SellDoc sellDoc = sellDocs.get(position);
            SellDocViewHolder viewHolder = (SellDocViewHolder) holder;

            viewHolder.tvCode.setText(sellDoc.getCode());
            viewHolder.tvDate.setText(sellDoc.getDate());
            viewHolder.tvAmount.setText(String.format("%,d ریال", Long.parseLong(sellDoc.getAmount())));
            
            if (sellDoc.getPerson() != null) {
                viewHolder.tvPerson.setText(sellDoc.getPerson().getNikename());
            } else {
                viewHolder.tvPerson.setText("-");
            }

            if (sellDoc.getLabel() != null) {
                viewHolder.tvLabel.setText(sellDoc.getLabel().getLabel());
                viewHolder.tvLabel.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvLabel.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> listener.onSellDocClick(sellDoc));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onSellDocLongClick(sellDoc);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return sellDocs.size() + (isLoading ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return position == sellDocs.size() ? TYPE_LOADING : TYPE_ITEM;
    }

    public void setSellDocs(List<SellDoc> sellDocs) {
        this.sellDocs.clear();
        this.sellDocs.addAll(sellDocs);
        notifyDataSetChanged();
    }

    public void addSellDocs(List<SellDoc> sellDocs) {
        int startPosition = this.sellDocs.size();
        this.sellDocs.addAll(sellDocs);
        notifyItemRangeInserted(startPosition, sellDocs.size());
    }

    public void setLoading(boolean loading) {
        if (this.isLoading != loading) {
            this.isLoading = loading;
            if (loading) {
                notifyItemInserted(sellDocs.size());
            } else {
                notifyItemRemoved(sellDocs.size());
            }
        }
    }

    public void clear() {
        sellDocs.clear();
        notifyDataSetChanged();
    }

    private static class SellDocViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode;
        TextView tvDate;
        TextView tvAmount;
        TextView tvPerson;
        TextView tvLabel;

        SellDocViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tv_code);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPerson = itemView.findViewById(R.id.tv_person);
            tvLabel = itemView.findViewById(R.id.tv_label);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
} 