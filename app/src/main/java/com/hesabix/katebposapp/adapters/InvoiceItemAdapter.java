package com.hesabix.katebposapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.InvoiceItem;

import java.util.List;
import java.util.Locale;

public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.ViewHolder> {
    private List<InvoiceItem> items;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(InvoiceItem item, int position);
        void onDeleteClick(InvoiceItem item, int position);
    }

    public InvoiceItemAdapter(Context context, List<InvoiceItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<InvoiceItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView quantityTextView;
        private TextView priceTextView;
        private TextView totalTextView;
        private ImageButton editButton;
        private ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            totalTextView = itemView.findViewById(R.id.totalTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(InvoiceItem item, int position) {
            nameTextView.setText(item.getCommodity().getName());
            quantityTextView.setText(String.format(Locale.getDefault(), "%.0f %s", 
                item.getQuantity(), item.getCommodity().getUnit()));
            priceTextView.setText(String.format(Locale.getDefault(), "%,.0f ریال", item.getPrice()));
            totalTextView.setText(String.format(Locale.getDefault(), "%,.0f ریال", item.getTotal()));

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item, position);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item, position);
                }
            });
        }
    }
} 