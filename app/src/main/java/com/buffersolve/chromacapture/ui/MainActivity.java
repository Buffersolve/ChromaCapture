package com.buffersolve.chromacapture.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.buffersolve.chromacapture.databinding.ActivityMainBinding;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        binding.cameraBtn.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainerView.getId(), new CameraFragment())
                        .commit());

        if (!OpenCVLoader.initDebug()) {
            // OpenCV initialization failed
            Log.d("OpenCVLOAD", "OpenCV initialization failed");
        } else {
            // OpenCV initialization succeeded
            Log.d("OpenCVLOAD", "OpenCV initialization succeeded");

        }

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);

                        // Load the image
//                        File imgFile = new File(String.valueOf(uri));
//                        String imagePath = "/storage/emulated/0/Pictures/red-color-solid-background-1920x1080.png";
                        String imagePath = "/storage/emulated/0/Pictures/IMG_20230313_160032.jpg";
//                        String imagePath = "/storage/emulated/0/Pictures/Colorcard-hex_ccffe5-www.colorbook.io.webp";
//                        String imagePath = "/storage/emulated/0/Pictures/Colorcard-hex_ccccff-www.colorbook.io.png";
                        Log.d("OpenCVLOAD", imagePath);

                        Mat img = Imgcodecs.imread(imagePath);
                        if (img.empty()) {
                            // Failed to load image
                            Log.d("OpenCVLOAD", "Failed to load image");
                        } else {
                            Log.d("OpenCVLOAD", "Success to load image");

                            Mat resizedImage = new Mat();

                            // Resize image to a smaller size for faster processing
                            Imgproc.resize(img, resizedImage, new Size(100, 100));
                            List<Scalar> colorList = new ArrayList<>();
                            for (int i = 0; i < resizedImage.rows(); i++) {
                                for (int j = 0; j < resizedImage.cols(); j++) {
                                    double[] pixel = resizedImage.get(i, j);
                                    Scalar color = new Scalar(pixel[2], pixel[1], pixel[0]);
                                    colorList.add(color);
                                }
                            }

                            Map<Scalar, Integer> colorCountMap = new HashMap<>();
                            for (Scalar color : colorList) {
                                Integer count = colorCountMap.get(color);
                                if (count == null) {
                                    count = 0;
                                }
                                colorCountMap.put(color, count + 1);
                            }

                            int maxCount = 0;
                            Scalar dominantColor = null;
                            for (Map.Entry<Scalar, Integer> entry : colorCountMap.entrySet()) {
                                if (entry.getValue() > maxCount) {
                                    maxCount = entry.getValue();
                                    dominantColor = entry.getKey();
                                }
                            }

                            assert dominantColor != null;
                            int red = (int) dominantColor.val[0];
                            int green = (int) dominantColor.val[1];
                            int blue = (int) dominantColor.val[2];

                            String hex = String.format("#%02x%02x%02x", red, green, blue);

                            System.out.println("Dominant Color: R=" + red + ", G=" + green + ", B=" + blue);
                            System.out.println("Hex Color: " + hex);

                            // Display image
                            ImageView imageView = binding.imageView;
//                            Bitmap bitmap = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
//                            Utils.matToBitmap(img, bitmap);
//                            imageView.setImageBitmap(bitmap);

                        }


                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        binding.pickerBtn.setOnClickListener(v ->
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(mediaType)
                        .build())
        );

    }


}