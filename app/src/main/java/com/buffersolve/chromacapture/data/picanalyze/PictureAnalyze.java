package com.buffersolve.chromacapture.data.picanalyze;

import android.util.Log;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureAnalyze {
    byte[] imageData;
//    ColorModel colorResult;

    public PictureAnalyze(byte[] imageData) {
        this.imageData = imageData;
    }
    //OpenCV
    // Decode the byte array as a Mat object

    public ColorModel analyze() {
        Mat img = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.IMREAD_UNCHANGED);

        // Load the image:
        // Mat img = Imgcodecs.imread(imagePath);

        if (img.empty()) {
            // Failed to load image
            Log.d("OpenCVLOAD", "Failed to load image");
            return new ColorModel(null, null, new Error());
        } else {
            Log.d("OpenCVLOAD", "Success to load image");

            // Resize image to a smaller size for faster processing
            Mat resizedImage = new Mat();
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

            // Result
            String rgb = "R=" + red + ", G=" + green + ", B=" + blue;
            String hex = String.format("#%02x%02x%02x", red, green, blue);

//            colorResult.setRgb(rgb);
//            colorResult.setHex(hex);
            return new ColorModel(rgb, hex, null);

        }

    }

}
