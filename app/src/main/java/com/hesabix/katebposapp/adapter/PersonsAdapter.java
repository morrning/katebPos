package com.hesabix.katebposapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hesabix.katebposapp.R;
import com.hesabix.katebposapp.model.Person;
import com.hesabix.katebposapp.model.PersonType;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class PersonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_PERSON = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Person> persons = new ArrayList<>();
    private List<Long> personIds = new ArrayList<>();
    private OnPersonClickListener listener;
    private boolean showLoading = false;

    public interface OnPersonClickListener {
        void onPersonClick(Person person);
        void onPersonLongClick(Person person);
    }

    public PersonsAdapter(OnPersonClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position < persons.size() ? VIEW_TYPE_PERSON : VIEW_TYPE_LOADING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PERSON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person, parent, false);
            return new PersonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PersonViewHolder) {
            PersonViewHolder personHolder = (PersonViewHolder) holder;
            Person person = persons.get(position);
            personHolder.bind(person);
        }
    }

    @Override
    public int getItemCount() {
        return persons.size() + (showLoading ? 1 : 0);
    }

    public void setPersons(List<Person> persons) {
        this.persons.clear();
        this.personIds.clear();
        addPersons(persons);
    }

    public void addPersons(List<Person> newPersons) {
        int startPosition = persons.size();
        int addedCount = 0;
        
        for (Person person : newPersons) {
            if (!personIds.contains(person.getId())) {
                persons.add(person);
                personIds.add(person.getId());
                addedCount++;
            }
        }
        
        if (addedCount > 0) {
            notifyItemRangeInserted(startPosition, addedCount);
        }
    }

    public void setLoading(boolean loading) {
        if (this.showLoading != loading) {
            this.showLoading = loading;
            if (loading) {
                notifyItemInserted(persons.size());
            } else {
                notifyItemRemoved(persons.size());
            }
        }
    }

    public void clear() {
        persons.clear();
        personIds.clear();
        notifyDataSetChanged();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPersonName;
        private TextView tvPersonCode;
        private TextView tvPersonMobile;
        private TextView tvPersonBalance;
        private TextView tvPersonTypes;
        private TextView tvPersonBalanceLabel;

        PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPersonName = itemView.findViewById(R.id.tv_person_name);
            tvPersonCode = itemView.findViewById(R.id.tv_person_code);
            tvPersonMobile = itemView.findViewById(R.id.tv_person_mobile);
            tvPersonBalance = itemView.findViewById(R.id.tv_person_balance);
            tvPersonTypes = itemView.findViewById(R.id.tv_person_types);
            tvPersonBalanceLabel = itemView.findViewById(R.id.tv_person_balance_label);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < persons.size()) {
                    listener.onPersonClick(persons.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && position < persons.size()) {
                    listener.onPersonLongClick(persons.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(Person person) {
            tvPersonName.setText(person.getNikename());
            tvPersonCode.setText(person.getCode());
            tvPersonMobile.setText(person.getMobile());

            // نمایش انواع شخص
            StringBuilder types = new StringBuilder();
            for (PersonType type : person.getTypes()) {
                if (type.isChecked()) {
                    if (types.length() > 0) {
                        types.append("، ");
                    }
                    types.append(type.getLabel());
                }
            }
            tvPersonTypes.setText(types.toString());

            // نمایش موجودی با رنگ متفاوت برای موجودی منفی
            double balance = person.getBalance();
            tvPersonBalance.setText(String.format("تراز: %,.0f ریال", balance));
            
            if (balance < 0) {
                tvPersonBalance.setTextColor(itemView.getContext().getResources().getColor(R.color.error));
                tvPersonBalanceLabel.setVisibility(View.VISIBLE);
            } else {
                tvPersonBalance.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
                tvPersonBalanceLabel.setVisibility(View.GONE);
            }
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
} 