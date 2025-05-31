package com.hesabix.katebposapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusinessInfo {
    @SerializedName("id")
    private int id;

    @SerializedName("owner")
    private String owner;

    @SerializedName("name")
    private String name;

    @SerializedName("legal_name")
    private String legalName;

    @SerializedName("field")
    private String field;

    @SerializedName("address")
    private String address;

    @SerializedName("maliyatafzode")
    private String maliyatafzode;

    @SerializedName("arzmain")
    private Currency arzmain;

    @SerializedName("year")
    private Year year;

    @SerializedName("moneys")
    private List<Currency> moneys;

    public static class Currency {
        @SerializedName("id")
        private int id;

        @SerializedName("label")
        private String label;

        @SerializedName("name")
        private String name;

        @SerializedName("shortName")
        private String shortName;

        @SerializedName("symbol")
        private String symbol;

        // Getters
        public int getId() { return id; }
        public String getLabel() { return label; }
        public String getName() { return name; }
        public String getShortName() { return shortName; }
        public String getSymbol() { return symbol; }
    }

    public static class Year {
        @SerializedName("id")
        private int id;

        @SerializedName("label")
        private String label;

        @SerializedName("head")
        private boolean head;

        @SerializedName("start")
        private String start;

        @SerializedName("end")
        private String end;

        @SerializedName("now")
        private long now;

        @SerializedName("startShamsi")
        private String startShamsi;

        @SerializedName("endShamsi")
        private String endShamsi;

        // Getters
        public int getId() { return id; }
        public String getLabel() { return label; }
        public boolean isHead() { return head; }
        public String getStart() { return start; }
        public String getEnd() { return end; }
        public long getNow() { return now; }
        public String getStartShamsi() { return startShamsi; }
        public String getEndShamsi() { return endShamsi; }
    }

    // Getters
    public int getId() { return id; }
    public String getOwner() { return owner; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getField() { return field; }
    public String getAddress() { return address; }
    public String getMaliyatafzode() { return maliyatafzode; }
    public Currency getArzmain() { return arzmain; }
    public Year getYear() { return year; }
    public List<Currency> getMoneys() { return moneys; }
} 