package com.hesabix.katebposapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Person {
    @SerializedName("id")
    private long id;
    
    @SerializedName("code")
    private String code;
    
    @SerializedName("nikename")
    private String nikename;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("tel")
    private String tel;
    
    @SerializedName("mobile")
    private String mobile;
    
    @SerializedName("mobile2")
    private String mobile2;
    
    @SerializedName("des")
    private String des;
    
    @SerializedName("company")
    private String company;
    
    @SerializedName("shenasemeli")
    private String shenasemeli;
    
    @SerializedName("codeeghtesadi")
    private String codeeghtesadi;
    
    @SerializedName("sabt")
    private String sabt;
    
    @SerializedName("keshvar")
    private String keshvar;
    
    @SerializedName("ostan")
    private String ostan;
    
    @SerializedName("shahr")
    private String shahr;
    
    @SerializedName("postalcode")
    private String postalcode;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("website")
    private String website;
    
    @SerializedName("fax")
    private String fax;
    
    @SerializedName("birthday")
    private String birthday;
    
    @SerializedName("speedAccess")
    private boolean speedAccess;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("prelabel")
    private String prelabel;
    
    @SerializedName("accounts")
    private List<PersonAccount> accounts;
    
    @SerializedName("types")
    private List<PersonType> types;
    
    @SerializedName("bs")
    private double bs;
    
    @SerializedName("bd")
    private double bd;
    
    @SerializedName("balance")
    private double balance;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNikename() {
        return nikename;
    }

    public void setNikename(String nikename) {
        this.nikename = nikename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getShenasemeli() {
        return shenasemeli;
    }

    public void setShenasemeli(String shenasemeli) {
        this.shenasemeli = shenasemeli;
    }

    public String getCodeeghtesadi() {
        return codeeghtesadi;
    }

    public void setCodeeghtesadi(String codeeghtesadi) {
        this.codeeghtesadi = codeeghtesadi;
    }

    public String getSabt() {
        return sabt;
    }

    public void setSabt(String sabt) {
        this.sabt = sabt;
    }

    public String getKeshvar() {
        return keshvar;
    }

    public void setKeshvar(String keshvar) {
        this.keshvar = keshvar;
    }

    public String getOstan() {
        return ostan;
    }

    public void setOstan(String ostan) {
        this.ostan = ostan;
    }

    public String getShahr() {
        return shahr;
    }

    public void setShahr(String shahr) {
        this.shahr = shahr;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isSpeedAccess() {
        return speedAccess;
    }

    public void setSpeedAccess(boolean speedAccess) {
        this.speedAccess = speedAccess;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrelabel() {
        return prelabel;
    }

    public void setPrelabel(String prelabel) {
        this.prelabel = prelabel;
    }

    public List<PersonAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PersonAccount> accounts) {
        this.accounts = accounts;
    }

    public List<PersonType> getTypes() {
        return types;
    }

    public void setTypes(List<PersonType> types) {
        this.types = types;
    }

    public double getBs() {
        return bs;
    }

    public void setBs(double bs) {
        this.bs = bs;
    }

    public double getBd() {
        return bd;
    }

    public void setBd(double bd) {
        this.bd = bd;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
} 