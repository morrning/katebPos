package com.hesabix.katebposapp.retrofit;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Business {
    private int id;
    private String owner;
    private String name;
    @SerializedName("legal_name")
    private String legalName;
    private String field;
    @SerializedName("shenasemeli")
    private String nationalId;
    @SerializedName("codeeqtesadi")
    private String economicCode;
    @SerializedName("shomaresabt")
    private String registrationNumber;
    private String country;
    private String ostan;
    private String shahrestan;
    private String postalcode;
    private String tel;
    private String mobile;
    private String address;
    private String website;
    @SerializedName("maliyatafzode")
    private String vat;
    @SerializedName("arzmain")
    private Currency mainCurrency;
    @SerializedName("zarinpalCode")
    private String zarinpalCode;
    @SerializedName("smsCharge")
    private String smsCharge;
    private boolean shortlinks;
    @SerializedName("walletEnabled")
    private boolean walletEnabled;
    @SerializedName("walletMatchBank")
    private String walletMatchBank;
    @SerializedName("updateSellPrice")
    private Boolean updateSellPrice;
    @SerializedName("updateBuyPrice")
    private boolean updateBuyPrice;
    @SerializedName("profitCalcType")
    private String profitCalcType;
    private Year year;
    private List<Currency> moneys;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getEconomicCode() { return economicCode; }
    public void setEconomicCode(String economicCode) { this.economicCode = economicCode; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getOstan() { return ostan; }
    public void setOstan(String ostan) { this.ostan = ostan; }
    public String getShahrestan() { return shahrestan; }
    public void setShahrestan(String shahrestan) { this.shahrestan = shahrestan; }
    public String getPostalcode() { return postalcode; }
    public void setPostalcode(String postalcode) { this.postalcode = postalcode; }
    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getVat() { return vat; }
    public void setVat(String vat) { this.vat = vat; }
    public Currency getMainCurrency() { return mainCurrency; }
    public void setMainCurrency(Currency mainCurrency) { this.mainCurrency = mainCurrency; }
    public String getZarinpalCode() { return zarinpalCode; }
    public void setZarinpalCode(String zarinpalCode) { this.zarinpalCode = zarinpalCode; }
    public String getSmsCharge() { return smsCharge; }
    public void setSmsCharge(String smsCharge) { this.smsCharge = smsCharge; }
    public boolean isShortlinks() { return shortlinks; }
    public void setShortlinks(boolean shortlinks) { this.shortlinks = shortlinks; }
    public boolean isWalletEnabled() { return walletEnabled; }
    public void setWalletEnabled(boolean walletEnabled) { this.walletEnabled = walletEnabled; }
    public String getWalletMatchBank() { return walletMatchBank; }
    public void setWalletMatchBank(String walletMatchBank) { this.walletMatchBank = walletMatchBank; }
    public Boolean getUpdateSellPrice() { return updateSellPrice; }
    public void setUpdateSellPrice(Boolean updateSellPrice) { this.updateSellPrice = updateSellPrice; }
    public boolean isUpdateBuyPrice() { return updateBuyPrice; }
    public void setUpdateBuyPrice(boolean updateBuyPrice) { this.updateBuyPrice = updateBuyPrice; }
    public String getProfitCalcType() { return profitCalcType; }
    public void setProfitCalcType(String profitCalcType) { this.profitCalcType = profitCalcType; }
    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }
    public List<Currency> getMoneys() { return moneys; }
    public void setMoneys(List<Currency> moneys) { this.moneys = moneys; }
}
