package com.hesabix.katebposapp.service;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "KatebPosAppPrefs";
    private static final String KEY_TOKEN = "x-auth-token";
    private static final String KEY_SELECTED_BUSINESS_ID = "selected_business_id";
    private static final String KEY_SELECTED_BUSINESS_NAME = "selected_business_name";
    private static final String KEY_SELECTED_BUSINESS_LEGAL_NAME = "selected_business_legal_name";
    private static final String KEY_SELECTED_BUSINESS_FIELD = "selected_business_field";
    private static final String KEY_SELECTED_BUSINESS_NATIONAL_ID = "selected_business_national_id";
    private static final String KEY_SELECTED_BUSINESS_ECONOMIC_CODE = "selected_business_economic_code";
    private static final String KEY_SELECTED_BUSINESS_REGISTRATION_NUMBER = "selected_business_registration_number";
    private static final String KEY_SELECTED_BUSINESS_ADDRESS = "selected_business_address";
    private static final String KEY_SELECTED_BUSINESS_TEL = "selected_business_tel";
    private static final String KEY_SELECTED_BUSINESS_MOBILE = "selected_business_mobile";
    private static final String KEY_SELECTED_BUSINESS_VAT = "selected_business_vat";
    private static final String KEY_SELECTED_BUSINESS_MAIN_CURRENCY = "selected_business_main_currency";
    private static final String KEY_SELECTED_BUSINESS_WALLET_ENABLED = "selected_business_wallet_enabled";
    private static final String KEY_SELECTED_BUSINESS_WALLET_MATCH_BANK = "selected_business_wallet_match_bank";
    private static final String KEY_SELECTED_BUSINESS_UPDATE_SELL_PRICE = "selected_business_update_sell_price";
    private static final String KEY_SELECTED_BUSINESS_UPDATE_BUY_PRICE = "selected_business_update_buy_price";
    private static final String KEY_SELECTED_BUSINESS_PROFIT_CALC_TYPE = "selected_business_profit_calc_type";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public void saveSelectedBusinessId(int id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECTED_BUSINESS_ID, id);
        editor.apply();
    }

    public int getSelectedBusinessId() {
        return prefs.getInt(KEY_SELECTED_BUSINESS_ID, -1);
    }

    public void saveSelectedBusinessName(String name) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_NAME, name);
        editor.apply();
    }

    public String getSelectedBusinessName() {
        return prefs.getString(KEY_SELECTED_BUSINESS_NAME, "");
    }

    public void saveSelectedBusinessLegalName(String legalName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_LEGAL_NAME, legalName);
        editor.apply();
    }

    public String getSelectedBusinessLegalName() {
        return prefs.getString(KEY_SELECTED_BUSINESS_LEGAL_NAME, "");
    }

    public void saveSelectedBusinessField(String field) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_FIELD, field);
        editor.apply();
    }

    public String getSelectedBusinessField() {
        return prefs.getString(KEY_SELECTED_BUSINESS_FIELD, "");
    }

    public void saveSelectedBusinessNationalId(String nationalId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_NATIONAL_ID, nationalId);
        editor.apply();
    }

    public String getSelectedBusinessNationalId() {
        return prefs.getString(KEY_SELECTED_BUSINESS_NATIONAL_ID, "");
    }

    public void saveSelectedBusinessEconomicCode(String economicCode) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_ECONOMIC_CODE, economicCode);
        editor.apply();
    }

    public String getSelectedBusinessEconomicCode() {
        return prefs.getString(KEY_SELECTED_BUSINESS_ECONOMIC_CODE, "");
    }

    public void saveSelectedBusinessRegistrationNumber(String registrationNumber) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_REGISTRATION_NUMBER, registrationNumber);
        editor.apply();
    }

    public String getSelectedBusinessRegistrationNumber() {
        return prefs.getString(KEY_SELECTED_BUSINESS_REGISTRATION_NUMBER, "");
    }

    public void saveSelectedBusinessAddress(String address) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_ADDRESS, address);
        editor.apply();
    }

    public String getSelectedBusinessAddress() {
        return prefs.getString(KEY_SELECTED_BUSINESS_ADDRESS, "");
    }

    public void saveSelectedBusinessTel(String tel) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_TEL, tel);
        editor.apply();
    }

    public String getSelectedBusinessTel() {
        return prefs.getString(KEY_SELECTED_BUSINESS_TEL, "");
    }

    public void saveSelectedBusinessMobile(String mobile) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_MOBILE, mobile);
        editor.apply();
    }

    public String getSelectedBusinessMobile() {
        return prefs.getString(KEY_SELECTED_BUSINESS_MOBILE, "");
    }

    public void saveSelectedBusinessVat(String vat) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_VAT, vat);
        editor.apply();
    }

    public String getSelectedBusinessVat() {
        return prefs.getString(KEY_SELECTED_BUSINESS_VAT, "");
    }

    public void saveSelectedBusinessMainCurrency(String mainCurrency) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_MAIN_CURRENCY, mainCurrency);
        editor.apply();
    }

    public String getSelectedBusinessMainCurrency() {
        return prefs.getString(KEY_SELECTED_BUSINESS_MAIN_CURRENCY, "");
    }

    public void saveSelectedBusinessWalletEnabled(boolean walletEnabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SELECTED_BUSINESS_WALLET_ENABLED, walletEnabled);
        editor.apply();
    }

    public boolean getSelectedBusinessWalletEnabled() {
        return prefs.getBoolean(KEY_SELECTED_BUSINESS_WALLET_ENABLED, false);
    }

    public void saveSelectedBusinessWalletMatchBank(String walletMatchBank) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_WALLET_MATCH_BANK, walletMatchBank);
        editor.apply();
    }

    public String getSelectedBusinessWalletMatchBank() {
        return prefs.getString(KEY_SELECTED_BUSINESS_WALLET_MATCH_BANK, "");
    }

    public void saveSelectedBusinessUpdateSellPrice(boolean updateSellPrice) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SELECTED_BUSINESS_UPDATE_SELL_PRICE, updateSellPrice);
        editor.apply();
    }

    public boolean getSelectedBusinessUpdateSellPrice() {
        return prefs.getBoolean(KEY_SELECTED_BUSINESS_UPDATE_SELL_PRICE, false);
    }

    public void saveSelectedBusinessUpdateBuyPrice(boolean updateBuyPrice) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_SELECTED_BUSINESS_UPDATE_BUY_PRICE, updateBuyPrice);
        editor.apply();
    }

    public boolean getSelectedBusinessUpdateBuyPrice() {
        return prefs.getBoolean(KEY_SELECTED_BUSINESS_UPDATE_BUY_PRICE, false);
    }

    public void saveSelectedBusinessProfitCalcType(String profitCalcType) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SELECTED_BUSINESS_PROFIT_CALC_TYPE, profitCalcType);
        editor.apply();
    }

    public String getSelectedBusinessProfitCalcType() {
        return prefs.getString(KEY_SELECTED_BUSINESS_PROFIT_CALC_TYPE, "");
    }

    public void clearSelectedBusinessId() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_SELECTED_BUSINESS_ID);
        editor.remove(KEY_SELECTED_BUSINESS_NAME);
        editor.remove(KEY_SELECTED_BUSINESS_LEGAL_NAME);
        editor.remove(KEY_SELECTED_BUSINESS_FIELD);
        editor.remove(KEY_SELECTED_BUSINESS_NATIONAL_ID);
        editor.remove(KEY_SELECTED_BUSINESS_ECONOMIC_CODE);
        editor.remove(KEY_SELECTED_BUSINESS_REGISTRATION_NUMBER);
        editor.remove(KEY_SELECTED_BUSINESS_ADDRESS);
        editor.remove(KEY_SELECTED_BUSINESS_TEL);
        editor.remove(KEY_SELECTED_BUSINESS_MOBILE);
        editor.remove(KEY_SELECTED_BUSINESS_VAT);
        editor.remove(KEY_SELECTED_BUSINESS_MAIN_CURRENCY);
        editor.remove(KEY_SELECTED_BUSINESS_WALLET_ENABLED);
        editor.remove(KEY_SELECTED_BUSINESS_WALLET_MATCH_BANK);
        editor.remove(KEY_SELECTED_BUSINESS_UPDATE_SELL_PRICE);
        editor.remove(KEY_SELECTED_BUSINESS_UPDATE_BUY_PRICE);
        editor.remove(KEY_SELECTED_BUSINESS_PROFIT_CALC_TYPE);
        editor.apply();
    }

    public void clearUserInfo() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void clearAllPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}