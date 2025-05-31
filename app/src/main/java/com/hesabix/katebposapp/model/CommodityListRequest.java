package com.hesabix.katebposapp.model;

public class CommodityListRequest {
    private Filters filters;
    private Pagination pagination;
    private Sort sort;

    public CommodityListRequest(int page, int limit, String searchQuery) {
        this.filters = new Filters(searchQuery);
        this.pagination = new Pagination(page, limit);
        this.sort = new Sort("code", true);
    }

    public static class Filters {
        private Search search;

        public Filters(String searchValue) {
            this.search = new Search(searchValue);
        }

        public static class Search {
            private String value;

            public Search(String value) {
                this.value = value;
            }
        }
    }

    public static class Pagination {
        private int page;
        private int limit;

        public Pagination(int page, int limit) {
            this.page = page;
            this.limit = limit;
        }
    }

    public static class Sort {
        private String sortBy;
        private boolean sortDesc;

        public Sort(String sortBy, boolean sortDesc) {
            this.sortBy = sortBy;
            this.sortDesc = sortDesc;
        }
    }
} 