package com.capiyoo.dencables;

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
    public static final String FIRM_ADDRESS = "firm_address";
    public static final String FIRM_AUTHORITY = "firm_authority";
    public static final String FIRM_CONTACT = "firm_contact";

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    void isKeyActivated(boolean status) {
        editor.putBoolean(IS_KEY_ACIVATED, status);
        editor.commit();

    }

    boolean getActivationStatus() {
        return sharedPreferences.getBoolean(IS_KEY_ACIVATED, false);
    }

    void isFirstTimeLogin(boolean bool) {
        editor.putBoolean(IS_FIRST_TIME_LOGIN, bool);
        editor.commit();
    }

    boolean getFirstTimeLoginCheck() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LOGIN, true);
    }

    void putMyUid(String uid) {
        editor.putString(FIREBASE_UID, uid);
        editor.commit();
    }

    String getFirebaseUid() {
        return sharedPreferences.getString(FIREBASE_UID, "null");
    }

    public void setFirmName(String str) {
        editor.putString(FIRM_NAME, str);
        editor.commit();
    }

    String getFirmName() {
        return sharedPreferences.getString(FIRM_NAME, "null");
    }

    public void putFirmAddress(String str) {
        editor.putString(FIRM_ADDRESS, str);
        editor.commit();
    }

    String getFirmAddress() {
        return sharedPreferences.getString(FIRM_ADDRESS, null);
    }

    public void setFirmAuthority(String str) {
        editor.putString(FIRM_AUTHORITY, str);
        editor.commit();
    }

    String getFirmAuthority() {
        return sharedPreferences.getString(FIRM_AUTHORITY, null);
    }

    public void setFirmContact(String str) {
        editor.putString(FIRM_CONTACT, str);
        editor.commit();
    }

    String getFirmContact() {
        return sharedPreferences.getString(FIRM_CONTACT, null);
    }

}
