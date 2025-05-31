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
import com.hesabix.katebposapp.model.Person;

import java.util.ArrayList;
import java.util.List;

public class CustomerSearchAdapter extends ArrayAdapter<Person> implements Filterable {
    private List<Person> originalList;
    private List<Person> filteredList;
    private LayoutInflater inflater;

    public CustomerSearchAdapter(@NonNull Context context) {
        super(context, 0);
        this.originalList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Person> newData) {
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
    public Person getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_customer_search, parent, false);
        }

        Person person = getItem(position);
        if (person != null) {
            TextView nameTextView = convertView.findViewById(R.id.nameTextView);
            TextView codeTextView = convertView.findViewById(R.id.codeTextView);
            TextView mobileTextView = convertView.findViewById(R.id.mobileTextView);

            nameTextView.setText(person.getNikename());
            codeTextView.setText(person.getCode());
            mobileTextView.setText(person.getMobile());
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
                List<Person> filteredItems = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredItems.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Person person : originalList) {
                        if (person.getNikename().toLowerCase().contains(filterPattern) ||
                            person.getCode().toLowerCase().contains(filterPattern) ||
                            person.getMobile().toLowerCase().contains(filterPattern)) {
                            filteredItems.add(person);
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
                    filteredList.addAll((List<Person>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
} 