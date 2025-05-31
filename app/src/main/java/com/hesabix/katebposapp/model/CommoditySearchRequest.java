package com.hesabix.katebposapp.model;

public class CommoditySearchRequest {
    private int page;
    private int itemsPerPage;
    private String search;
    private String sortBy;

    public CommoditySearchRequest(int page, int itemsPerPage, String search, String sortBy) {
        this.page = page;
        this.itemsPerPage = itemsPerPage;
        this.search = search;
        this.sortBy = sortBy;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getItemsPerPage() { return itemsPerPage; }
    public void setItemsPerPage(int itemsPerPage) { this.itemsPerPage = itemsPerPage; }
    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
} 