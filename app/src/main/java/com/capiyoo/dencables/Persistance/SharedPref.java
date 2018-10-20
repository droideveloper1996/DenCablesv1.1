package com.capiyoo.dencables.Persistance;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "mydata";
    public static final String IS_FIRST_TIME_LOGIN = "first_time_login";
    public static final String IS_KEY_ACIVATED = "key_activated";
    public static final String FIREBASE_UID = "firebase_uid";
    SharedPreferences.Editor editor;

    public static final String FIRM_NAME = "firm_name";
    public static final String FIRM_ADDRESS1 = "firm_address1";
    public static final String FIRM_ADDRESS2 = "firm_address2";

    public static final String FIRM_AUTHORITY = "firm_authority";
    public static final String FIRM_CONTACT = "firm_contact";

    public static final String STATE = "state";
    public static final String CITY = "city";

    public static final String LINEMAN_NAME = "lineman_name";
    public static final String LINEMAN_NAME_ID = "lineman_name_id";
    public static final String LINEMAN_NAME_KEY = "lineman_key";

    public static final String PACKAGE_NAME = "package_name";
    public static final String PACKAGE_PRICE = "package_price";
    public static final String _ORDER_ID = "order_id";


    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static final String ORDER_TIME = "order_time";
    public static final String ORDER_DATE = "order_date";

    public void isKeyActivated(boolean status) {
        editor.putBoolean(IS_KEY_ACIVATED, status);
        editor.commit();

    }

    public boolean getActivationStatus() {
        return sharedPreferences.getBoolean(IS_KEY_ACIVATED, false);
    }

    public void isFirstTimeLogin(boolean bool) {
        editor.putBoolean(IS_FIRST_TIME_LOGIN, bool);
        editor.commit();
    }

    public boolean getFirstTimeLoginCheck() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LOGIN, true);
    }

    public void setFirmName(String str) {
        editor.putString(FIRM_NAME, str);
        editor.commit();
    }

    public void putMyUid(String uid) {
        editor.putString(FIREBASE_UID, uid);
        editor.commit();
    }

    public void putFirmAddress1(String str) {
        editor.putString(FIRM_ADDRESS1, str);
        editor.commit();
    }

    public void putFirmAddress2(String str) {
        editor.putString(FIRM_ADDRESS2, str);
        editor.commit();
    }

    public String getFirebaseUid() {
        return sharedPreferences.getString(FIREBASE_UID, null);
    }

    public String getFirmName() {
        return sharedPreferences.getString(FIRM_NAME, null);
    }

    public void setFirmAuthority(String str) {
        editor.putString(FIRM_AUTHORITY, str);
        editor.commit();
    }

    public String getFirmAddress1() {
        return sharedPreferences.getString(FIRM_ADDRESS1, null);
    }

    public void setFirmContact(String str) {
        editor.putString(FIRM_CONTACT, str);
        editor.commit();
    }

    public String getFirmAddress2() {
        return sharedPreferences.getString(FIRM_ADDRESS2, null);
    }

    public void setState(String state) {
        editor.putString(STATE, state);
        editor.commit();
    }

    public String getFirmAuthority() {
        return sharedPreferences.getString(FIRM_AUTHORITY, null);
    }


    public void setCity(String state) {
        editor.putString(CITY, state);
        editor.commit();
    }

    public String getFirmContact() {
        return sharedPreferences.getString(FIRM_CONTACT, null);
    }

    String getState() {
        return sharedPreferences.getString(STATE, null);
    }

    public String getCity() {
        return sharedPreferences.getString(CITY, null);
    }

    public String getLinemanName() {
        return sharedPreferences.getString(LINEMAN_NAME, null);
    }

    public void setLinemanName(String linemanId) {

        editor.putString(LINEMAN_NAME, linemanId);
        editor.commit();
    }

    String getLinemanId() {
        return sharedPreferences.getString(LINEMAN_NAME_ID, null);
    }

    public void setLinemanId(String linemanId) {

        editor.putString(LINEMAN_NAME_ID, linemanId);
        editor.commit();
    }

    public String getLinemanKey() {
        return sharedPreferences.getString(LINEMAN_NAME_KEY, null);
    }

    public void setLinemanKey(String linemanKey) {

        editor.putString(LINEMAN_NAME_KEY, linemanKey);
        editor.commit();
    }

    public String getPackageName() {
        return sharedPreferences.getString(PACKAGE_NAME, null);
    }

    public void setPackageName(String packageName) {

        editor.putString(PACKAGE_NAME, packageName);
        editor.commit();
    }

    public String getPackagePrice() {
        return sharedPreferences.getString(PACKAGE_PRICE, null);
    }

    public void setPackagePrice(String packageprice) {

        editor.putString(PACKAGE_PRICE, packageprice);
        editor.commit();
    }

    public String getOrderId() {
        return sharedPreferences.getString(_ORDER_ID, null);
    }

    public void setOrderId(String orderId) {

        editor.putString(_ORDER_ID, orderId);
        editor.commit();
    }

    public String getOrderDate() {
        return sharedPreferences.getString(ORDER_DATE, null);
    }

    public void setOrderDate(String orderDate) {

        editor.putString(ORDER_DATE, orderDate);
        editor.commit();
    }

    public void setOrderTIme(String orderTime) {

        editor.putString(ORDER_TIME, orderTime);
        editor.commit();
    }

    public String getOrderTime() {
        return sharedPreferences.getString(ORDER_TIME, null);
    }


}
