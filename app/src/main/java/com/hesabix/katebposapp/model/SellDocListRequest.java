package com.hesabix.katebposapp.model;

import java.util.List;

public class SellDocListRequest {
    private String type;
    private String search;
    private int page;
    private int perPage;
    private List<String> types;
    private String dateFilter;
    private List<String> sortBy;

    public SellDocListRequest(String type, String search, int page, int perPage, List<String> types, String dateFilter, List<String> sortBy) {
        this.type = type;
        this.search = search;
        this.page = page;
        this.perPage = perPage;
        this.types = types;
        this.dateFilter = dateFilter;
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "SellDocListRequest{" +
                "type='" + type + '\'' +
                ", search='" + search + '\'' +
                ", page=" + page +
                ", perPage=" + perPage +
                ", types=" + types +
                ", dateFilter='" + dateFilter + '\'' +
                ", sortBy=" + sortBy +
                '}';
    }
} 