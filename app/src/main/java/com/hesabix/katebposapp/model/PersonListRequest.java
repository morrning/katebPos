package com.hesabix.katebposapp.model;

public class PersonListRequest {
    private int page;
    private int itemsPerPage;
    private String search;
    private String types;
    private String transactionFilters;
    private String sortBy;

    public PersonListRequest(int page, int itemsPerPage, String search, String types, String transactionFilters, String sortBy) {
        this.page = page;
        this.itemsPerPage = itemsPerPage;
        this.search = search;
        this.types = types;
        this.transactionFilters = transactionFilters;
        this.sortBy = sortBy;
    }

    // Getters
    public int getPage() { return page; }
    public int getItemsPerPage() { return itemsPerPage; }
    public String getSearch() { return search; }
    public String getTypes() { return types; }
    public String getTransactionFilters() { return transactionFilters; }
    public String getSortBy() { return sortBy; }
} 