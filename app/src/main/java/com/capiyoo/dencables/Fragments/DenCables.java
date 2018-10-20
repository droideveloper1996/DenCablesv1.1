package com.capiyoo.dencables.Fragments;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DenCables extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
