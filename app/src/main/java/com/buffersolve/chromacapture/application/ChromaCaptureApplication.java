package com.buffersolve.chromacapture.application;

import android.app.Application;
import android.util.Log;

import com.google.android.material.color.DynamicColors;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;

public class ChromaCaptureApplication extends Application {

        @Override
        public void onCreate() {
            DynamicColors.applyToActivitiesIfAvailable(this);
            super.onCreate();

            if (!OpenCVLoader.initDebug()) {
                Log.d("OpenCVLOAD", "OpenCV initialization failed");
            } else {
                Log.d("OpenCVLOAD", "OpenCV initialization succeeded");

            }

        }
}
