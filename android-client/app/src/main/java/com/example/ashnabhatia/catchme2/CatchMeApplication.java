package com.example.ashnabhatia.catchme2;

import android.app.Application;

/**
 * Created by bananer on 26.09.15.
 */
public class CatchMeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HttpApi.setupAuth(this);
    }
}
