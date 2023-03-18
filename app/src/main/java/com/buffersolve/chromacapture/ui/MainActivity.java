package com.buffersolve.chromacapture.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.buffersolve.chromacapture.databinding.ActivityMainBinding;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cameraBtn.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainerView.getId(), new CameraFragment())
                .commit());


    }

//    if(!OpenCVLoader.initDebug()){
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
//    }
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                    // OpenCV library loaded successfully
//                    break;
//                default:
//                    super.onManagerConnected(status);
//                    break;
//            }
//        }
//    };

}