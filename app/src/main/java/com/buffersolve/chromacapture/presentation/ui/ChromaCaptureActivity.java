package com.buffersolve.chromacapture.presentation.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;
import com.buffersolve.chromacapture.databinding.ActivityMainBinding;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import com.buffersolve.chromacapture.presentation.viewmodel.ChromaCaptureViewModel;
import com.buffersolve.chromacapture.presentation.viewmodel.ChromaCaptureViewModelFactory;

import java.io.ByteArrayOutputStream;

public class ChromaCaptureActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ChromaCaptureViewModel viewModel;
    private static final Integer PERM_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Camera permission request
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERM_REQUEST_CODE);
        }

        // Camera Btn click
        binding.cameraBtn.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraActivityResultLauncher.launch(cameraIntent);
        });

        // ViewModel Factory
        ChromaCaptureViewModelFactory viewModelFactory = new ChromaCaptureViewModelFactory();
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChromaCaptureViewModel.class);

    }

    // Camera
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    // Data
                    Intent data = result.getData();
                    assert data != null;

                    // Photo from intent
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    // Convert the Bitmap to a byte array without compression
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    byte[] imageData = outputStream.toByteArray();

                    // Round corners
                    Bitmap roundedBitmap = roundCorners(photo);

                    // Display photo
                    binding.imageView.setImageBitmap(roundedBitmap);

                    // Execute
                    viewModel.analyze(imageData);
                    viewModel.getAnalyzeData().observe(this, new Observer<>() {
                        @Override
                        public void onChanged(ColorModel colorModel) {
                            if (colorModel.getError() == null) {

                                // RGB
                                System.out.println("Dominant Color: " + colorModel.getRgb());
                                binding.tvRGB.setText(colorModel.getRgb());

                                // HEX
                                System.out.println("Hex Color: " + colorModel.getHex());
                                binding.tvHEX.setText(colorModel.getHex());

                                // Get the Surface from the TextureView
                                Surface surface = new Surface(binding.textureView.getSurfaceTexture());

                                try {
                                    // Lock the Surface for editing
                                    Canvas canvas = surface.lockCanvas(null);

                                    // Create a Paint object with the desired color
                                    Paint paint = new Paint();
                                    paint.setColor(Color.parseColor(colorModel.getHex())); // Replace with desired color

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
                        }
                    });
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private Bitmap roundCorners(Bitmap photo) {

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

        return roundedBitmap;
    }

}
