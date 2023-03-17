package com.buffersolve.chromacapture.application;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class ChromaCaptureApplication extends Application {

        @Override
        public void onCreate() {
            DynamicColors.applyToActivitiesIfAvailable(this);
            super.onCreate();



        }


}
