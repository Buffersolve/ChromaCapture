package com.buffersolve.chromacapture.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.buffersolve.chromacapture.databinding.ActivityMainBinding;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
//        binding.cameraBtn.setOnClickListener(v ->
//                getSupportFragmentManager().beginTransaction()
//                        .replace(binding.fragmentContainerView.getId(), new CameraFragment())
//                        .commit());

        binding.cameraBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraActivityResultLauncher.launch(cameraIntent);
        });


    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Data
                    Intent data = result.getData();
                    assert data != null;

                    Bitmap photo = (Bitmap) data.getExtras().get("data");


                    // Convert the Bitmap to a byte array without compression
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    byte[] imageData = outputStream.toByteArray();

//                    String imagePath = "/storage/emulated/0/Pictures/IMG_20230320_013655.jpg";

                    // Create a new Bitmap with rounded corners
                    Bitmap roundedBitmap = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvasForRound = new Canvas(roundedBitmap);
                    Paint paintForRound = new Paint();
                    Rect rect = new Rect(0, 0, photo.getWidth(), photo.getHeight());
                    RectF rectF = new RectF(rect);
                    // Corner Radius
                    float radius = 24;
                    paintForRound.setAntiAlias(true);
                    canvasForRound.drawRoundRect(rectF, radius, radius, paintForRound);
                    paintForRound.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvasForRound.drawBitmap(photo, rect, rect, paintForRound);

                    // Display photo
                    binding.imageView.setImageBitmap(roundedBitmap);

                    //OpenCV
                    // Decode the byte array as a Mat object
                    Mat img = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);

//                    Load the image:
//                    Mat img = Imgcodecs.imread(imagePath);

                    if (img.empty()) {
                        // Failed to load image
                        Log.d("OpenCVLOAD", "Failed to load image");
                    } else {

                        Log.d("OpenCVLOAD", "Success to load image");

                        Mat resizedImage = new Mat();

                        // Resize image to a smaller size for faster processing
                        Imgproc.resize(img, resizedImage, new Size(100, 100));

                        // Loop over each pixel and add its color to a list
                        List<Scalar> colorList = new ArrayList<>();
                        for (int i = 0; i < resizedImage.rows(); i++) {
                            for (int j = 0; j < resizedImage.cols(); j++) {
                                double[] pixel = resizedImage.get(i, j);
                                Scalar color = new Scalar(pixel[2], pixel[1], pixel[0]);
                                colorList.add(color);
                            }
                        }

                        // Count the frequency of each color
                        Map<Scalar, Integer> colorCountMap = new HashMap<>();
                        for (Scalar color : colorList) {
                            Integer count = colorCountMap.get(color);
                            if (count == null) {
                                count = 0;
                            }
                            colorCountMap.put(color, count + 1);
                        }

                        // Find the color with the highest frequency
                        int maxCount = 0;
                        Scalar dominantColor = null;
                        for (Map.Entry<Scalar, Integer> entry : colorCountMap.entrySet()) {
                            if (entry.getValue() > maxCount) {
                                maxCount = entry.getValue();
                                dominantColor = entry.getKey();
                            }
                        }

                        // Convert to RGB and HEX
                        assert dominantColor != null;
                        int red = (int) dominantColor.val[0];
                        int green = (int) dominantColor.val[1];
                        int blue = (int) dominantColor.val[2];

                        String rgb = "R=" + red + ", G=" + green + ", B=" + blue;
                        String hex = String.format("#%02x%02x%02x", red, green, blue);

                        // RGB
                        System.out.println("Dominant Color: " + rgb);
                        binding.tvRGB.setText(rgb);

                        // HEX
                        System.out.println("Hex Color: " + hex);
                        binding.tvHEX.setText(hex);


                        // Get the Surface from the TextureView

                        Surface surface = new Surface(binding.textureView.getSurfaceTexture());


                        try {

                            // Lock the Surface for editing
                            Canvas canvas = surface.lockCanvas(null);

                            // Create a Paint object with the desired color
                            Paint paint = new Paint();
                            paint.setColor(Color.parseColor(hex)); // Replace with desired color

                            // Draw a rectangle with the desired color onto the Canvas
                            canvas.drawRect(0, 0, binding.textureView.getWidth(), binding.textureView.getHeight(), paint);

                            // Unlock the Canvas and Surface
                            surface.unlockCanvasAndPost(canvas);


                        } catch (Exception e) {
                            Log.d("EXEPTION", "SMTH WENT WRONG");
                        } finally {
                            surface.release();
                        }

                    }

                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

                }
            });

}
