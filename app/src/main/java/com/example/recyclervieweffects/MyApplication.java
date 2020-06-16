package com.example.recyclervieweffects;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class MyApplication extends Application {

    public static WeakReference<Context> contextWeakReference;

    @Override
    public void onCreate() {
        super.onCreate();
        contextWeakReference = new WeakReference<Context>(this);
    }

    static Context getAppContext() {
        return contextWeakReference.get();
    }
}
