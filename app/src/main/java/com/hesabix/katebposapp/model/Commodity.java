package com.hesabix.katebposapp.model;

import java.util.List;

public class Commodity {
    private int id;
    private String name;
    private String unit;
    private UnitData unitData;
    private String des;
    private double priceBuy;
    private boolean speedAccess;
    private double priceSell;
    private String code;
    private String cat;
    private boolean khadamat;
    private boolean withoutTax;
    private boolean commodityCountCheck;
    private String minOrderCount;
    private String dayLoading;
    private String orderPoint;
    private int count;
    private List<Price> prices;

    public static class UnitData {
        private String name;
        private int floatNumber;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getFloatNumber() { return floatNumber; }
        public void setFloatNumber(int floatNumber) { this.floatNumber = floatNumber; }
    }

    public static class Price {
        private int id;
        private PriceList list;
        private String priceBuy;
        private String priceSell;

        public static class PriceList {
            private int id;
            private String label;

            public int getId() { return id; }
            public void setId(int id) { this.id = id; }
            public String getLabel() { return label; }
            public void setLabel(String label) { this.label = label; }
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public PriceList getList() { return list; }
        public void setList(PriceList list) { this.list = list; }
        public String getPriceBuy() { return priceBuy; }
        public void setPriceBuy(String priceBuy) { this.priceBuy = priceBuy; }
        public String getPriceSell() { return priceSell; }
        public void setPriceSell(String priceSell) { this.priceSell = priceSell; }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public UnitData getUnitData() { return unitData; }
    public void setUnitData(UnitData unitData) { this.unitData = unitData; }
    public String getDes() { return des; }
    public void setDes(String des) { this.des = des; }
    public double getPriceBuy() { return priceBuy; }
    public void setPriceBuy(double priceBuy) { this.priceBuy = priceBuy; }
    public boolean isSpeedAccess() { return speedAccess; }
    public void setSpeedAccess(boolean speedAccess) { this.speedAccess = speedAccess; }
    public double getPriceSell() { return priceSell; }
    public void setPriceSell(double priceSell) { this.priceSell = priceSell; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCat() { return cat; }
    public void setCat(String cat) { this.cat = cat; }
    public boolean isKhadamat() { return khadamat; }
    public void setKhadamat(boolean khadamat) { this.khadamat = khadamat; }
    public boolean isWithoutTax() { return withoutTax; }
    public void setWithoutTax(boolean withoutTax) { this.withoutTax = withoutTax; }
    public boolean isCommodityCountCheck() { return commodityCountCheck; }
    public void setCommodityCountCheck(boolean commodityCountCheck) { this.commodityCountCheck = commodityCountCheck; }
    public String getMinOrderCount() { return minOrderCount; }
    public void setMinOrderCount(String minOrderCount) { this.minOrderCount = minOrderCount; }
    public String getDayLoading() { return dayLoading; }
    public void setDayLoading(String dayLoading) { this.dayLoading = dayLoading; }
    public String getOrderPoint() { return orderPoint; }
    public void setOrderPoint(String orderPoint) { this.orderPoint = orderPoint; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<Price> getPrices() { return prices; }
    public void setPrices(List<Price> prices) { this.prices = prices; }
} 