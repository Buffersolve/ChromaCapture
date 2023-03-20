package com.buffersolve.chromacapture.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.buffersolve.chromacapture.domain.model.ColorModel;
import com.buffersolve.chromacapture.domain.repository.Repository;
import com.buffersolve.chromacapture.domain.usecase.AnalyzeUseCase;

public class ChromaCaptureViewModel extends ViewModel {
    private final AnalyzeUseCase analyzeUseCase;
    public ChromaCaptureViewModel(AnalyzeUseCase analyzeUseCase) {
        this.analyzeUseCase = analyzeUseCase;
    }

    private final MutableLiveData<ColorModel> _analyzeData = new MutableLiveData<>();
    public final LiveData<ColorModel> analyzeData
            = _analyzeData;

//    public LiveData<ColorModel> analyzeData() {
//        return _analyzeData;
//    }


    // Analyze Pic
    public void analyze(byte[] imageData) {
        ColorModel data = analyzeUseCase.execute(imageData);
        _analyzeData.setValue(data);
    }


}
