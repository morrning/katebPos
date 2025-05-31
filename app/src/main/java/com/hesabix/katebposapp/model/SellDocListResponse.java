package com.hesabix.katebposapp.model;

import java.util.List;

public class SellDocListResponse {
    private List<SellDoc> items;
    private int total;
    private int page;
    private int perPage;

    public List<SellDoc> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }
} 