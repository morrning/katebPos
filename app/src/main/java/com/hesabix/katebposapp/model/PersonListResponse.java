package com.hesabix.katebposapp.model;

import java.util.List;

public class PersonListResponse {
    private List<Person> items;
    private int total;
    private int unfilteredTotal;

    public List<Person> getItems() { return items; }
    public void setItems(List<Person> items) { this.items = items; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getUnfilteredTotal() { return unfilteredTotal; }
    public void setUnfilteredTotal(int unfilteredTotal) { this.unfilteredTotal = unfilteredTotal; }
} 