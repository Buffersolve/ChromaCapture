package com.buffersolve.chromacapture.data.picanalyze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import com.buffersolve.chromacapture.R;
import com.buffersolve.chromacapture.data.picanalyze.PictureAnalyze;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayOutputStream;

public class PictureAnalyzeTest {

    private byte[] imageData;

    @Before
    public void setUp() {
        // Get drawable and convert to bitmap
        Drawable drawable = ContextCompat.getDrawable(InstrumentationRegistry.getInstrumentation().getTargetContext(), R.drawable.img_20230313_160032);
        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        // Convert the bitmap to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        imageData = outputStream.toByteArray();
    }

    @Test
    public void testPictureAnalyzeRGB() {

        // Instance
        PictureAnalyze analyzer = new PictureAnalyze();

        // Run color analysis
        ColorModel result = analyzer.analyze(imageData);

        // Verify
        assertNotNull(result);
        assertNull(result.getError());
        assertEquals("R=231, G=224, B=214", result.getRgb());
    }

    @Test
    public void testPictureAnalyzeHEX() {

        // Instance
        PictureAnalyze analyzer = new PictureAnalyze();

        // Run color analysis
        ColorModel result = analyzer.analyze(imageData);

        // Verify
        assertNotNull(result);
        assertNull(result.getError());
        assertEquals("#e7e0d6", result.getHex());
    }

    @Test
    public void testPictureAnalyzeShouldReturnError() {

        // Instance
        PictureAnalyze analyzer = new PictureAnalyze();
        byte[] nullImageData = new byte[1];

        // Run color analysis
        ColorModel result = analyzer.analyze(nullImageData);

        // Verify
        assertNotNull(result);
        assertNull(result.getRgb());
        assertNull(result.getHex());
        assertNotNull(result.getError());
    }

}
