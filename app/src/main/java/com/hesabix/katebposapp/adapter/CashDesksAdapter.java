package com.hesabix.katebposapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.CashDesk;

import java.util.ArrayList;
import java.util.List;

public class CashDesksAdapter extends RecyclerView.Adapter<CashDesksAdapter.ViewHolder> {
    private Context context;
    private List<CashDesk> cashDesks;
    private OnCashDeskClickListener listener;

    public interface OnCashDeskClickListener {
        void onCashDeskClick(CashDesk cashDesk);
        void onCashDeskLongClick(CashDesk cashDesk);
    }

    public CashDesksAdapter(Context context, OnCashDeskClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.cashDesks = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cash_desk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CashDesk cashDesk = cashDesks.get(position);
        holder.bind(cashDesk);
    }

    @Override
    public int getItemCount() {
        return cashDesks.size();
    }

    public void setCashDesks(List<CashDesk> cashDesks) {
        this.cashDesks = cashDesks;
        notifyDataSetChanged();
    }

    public void clear() {
        this.cashDesks.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvBalance;
        private TextView tvCurrency;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBalance = itemView.findViewById(R.id.tv_balance);
            tvCurrency = itemView.findViewById(R.id.tv_currency);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCashDeskClick(cashDesks.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCashDeskLongClick(cashDesks.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(CashDesk cashDesk) {
            tvName.setText(cashDesk.getName());
            tvBalance.setText(String.valueOf(cashDesk.getBalance()));
            if (cashDesk.getMoney() != null) {
                tvCurrency.setText(cashDesk.getMoney().getSymbol());
            }
        }
    }
} 