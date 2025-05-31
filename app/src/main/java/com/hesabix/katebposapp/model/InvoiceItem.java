package com.hesabix.katebposapp.model;

public class InvoiceItem {
    private Commodity commodity;
    private double quantity;
    private double price;
    private double total;

    public InvoiceItem(Commodity commodity, double quantity) {
        this.commodity = commodity;
        this.quantity = quantity;
        this.price = commodity.getPriceSell();
        this.total = quantity * price;
    }

    public Commodity getCommodity() { return commodity; }
    public void setCommodity(Commodity commodity) { this.commodity = commodity; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.total = quantity * price;
    }
    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        this.total = quantity * price;
    }
    public double getTotal() { return total; }
} 