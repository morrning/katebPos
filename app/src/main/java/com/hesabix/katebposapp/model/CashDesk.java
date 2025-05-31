package com.hesabix.katebposapp.model;

public class CashDesk {
    private int id;
    private String code;
    private String name;
    private String des;
    private Double balance;
    private Money money;

    public static class Money {
        private int id;
        private String label;
        private String name;
        private String shortName;
        private String symbol;

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public String getName() {
            return name;
        }

        public String getShortName() {
            return shortName;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }
} 