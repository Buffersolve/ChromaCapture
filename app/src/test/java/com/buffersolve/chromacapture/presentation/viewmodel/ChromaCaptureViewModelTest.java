package com.buffersolve.chromacapture.presentation.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.buffersolve.chromacapture.LiveDataTestUtil;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import com.buffersolve.chromacapture.domain.usecase.AnalyzeUseCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChromaCaptureViewModelTest {

    private ChromaCaptureViewModel viewModel;
//    private ChromaCaptureViewModel mockedViewModel;

    @Mock
    private AnalyzeUseCase analyzeUseCase;

    @Before
    public void setUp() {
//        mockedViewModel = mock(ChromaCaptureViewModel.class);
        viewModel = new ChromaCaptureViewModel(analyzeUseCase);
    }

    @Test
    public void testAnalyze() throws InterruptedException {
        byte[] imageData = new byte[] { 0x00, 0x01, 0x02, 0x03 };

        // Define the expected output from the use case
        ColorModel expectedOutput = new ColorModel("rgb", "HEX", null);

        // Mock the execute method of the use case to return the expected output
        when(analyzeUseCase.execute(imageData)).thenReturn(expectedOutput);

        // Call the analyze method of the view model
        viewModel.analyze(imageData);

        // Verify that the analyzeData LiveData is updated with the expected output
//        new LiveDataTestUtil().observeForTesting(viewModel.analyzeData);
        ColorModel analyzeData = LiveDataTestUtil.getOrAwaitValue(viewModel.analyzeData);

        assertEquals(expectedOutput, analyzeData);
    }

}
