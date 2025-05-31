package com.hesabix.katebposapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.Commodity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommoditySearchAdapter extends ArrayAdapter<Commodity> implements Filterable {
    private List<Commodity> originalList;
    private List<Commodity> filteredList;
    private LayoutInflater inflater;

    public CommoditySearchAdapter(@NonNull Context context) {
        super(context, 0);
        this.originalList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Commodity> newData) {
        this.originalList.clear();
        this.originalList.addAll(newData);
        this.filteredList.clear();
        this.filteredList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public Commodity getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_commodity_search, parent, false);
        }

        Commodity commodity = getItem(position);
        if (commodity != null) {
            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            TextView codeTextView = convertView.findViewById(R.id.codeTextView);
            TextView priceTextView = convertView.findViewById(R.id.priceTextView);

            nameTextView.setText(commodity.getName());
            codeTextView.setText(commodity.getCode());
            priceTextView.setText(String.format(Locale.getDefault(), "%,.0f ریال", commodity.getPriceSell()));
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Commodity> filteredItems = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredItems.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Commodity commodity : originalList) {
                        if (commodity.getName().toLowerCase().contains(filterPattern) ||
                            commodity.getCode().toLowerCase().contains(filterPattern)) {
                            filteredItems.add(commodity);
                        }
                    }
                }

                results.values = filteredItems;
                results.count = filteredItems.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<Commodity>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
} 