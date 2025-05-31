package com.hesabix.katebposapp.model;

import java.util.List;

public class CommodityListResponse {
    private List<Commodity> results;
    private Pagination pagination;

    public List<Commodity> getResults() {
        return results;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public static class Pagination {
        private int current_page;
        private int per_page;
        private int total_items;
        private int total_pages;

        public int getCurrentPage() {
            return current_page;
        }

        public int getPerPage() {
            return per_page;
        }

        public int getTotalItems() {
            return total_items;
        }

        public int getTotalPages() {
            return total_pages;
        }
    }
} 