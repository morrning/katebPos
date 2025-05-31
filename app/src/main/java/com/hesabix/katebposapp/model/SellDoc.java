package com.hesabix.katebposapp.model;

public class SellDoc {
    private int id;
    private String dateSubmit;
    private String date;
    private String type;
    private String code;
    private String des;
    private String amount;
    private String submitter;
    private Label label;
    private Person person;
    private int relatedDocsCount;
    private String relatedDocsPays;
    private double profit;
    private String discountAll;
    private String transferCost;

    public static class Label {
        private String code;
        private String label;

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class Person {
        private int id;
        private String nikename;
        private String code;

        public int getId() {
            return id;
        }

        public String getNikename() {
            return nikename;
        }

        public String getCode() {
            return code;
        }
    }

    public int getId() {
        return id;
    }

    public String getDateSubmit() {
        return dateSubmit;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }

    public String getAmount() {
        return amount;
    }

    public String getSubmitter() {
        return submitter;
    }

    public Label getLabel() {
        return label;
    }

    public Person getPerson() {
        return person;
    }

    public int getRelatedDocsCount() {
        return relatedDocsCount;
    }

    public String getRelatedDocsPays() {
        return relatedDocsPays;
    }

    public double getProfit() {
        return profit;
    }

    public String getDiscountAll() {
        return discountAll;
    }

    public String getTransferCost() {
        return transferCost;
    }
} 