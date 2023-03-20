package com.buffersolve.chromacapture.data.repository;

import static org.junit.Assert.assertNotNull;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mockk.MockK;

public class RepositoryImplementationTest {

    @Test
    public void testAnalyze() {
        RepositoryImplementation repositoryImplementation = new RepositoryImplementation();
        byte[] imageData = new byte[]{0x1, 0x2, 0x3, 0x4, 0x5};

        ColorModel colorModel = repositoryImplementation.analyze(imageData);

        assertNotNull(colorModel);
    }

}
