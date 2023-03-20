package com.buffersolve.chromacapture.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.buffersolve.chromacapture.data.repository.RepositoryImplementation;
import com.buffersolve.chromacapture.domain.usecase.AnalyzeUseCase;

public class ChromaCaptureViewModelFactory implements ViewModelProvider.Factory {
    private final RepositoryImplementation repository = new RepositoryImplementation();
    private final AnalyzeUseCase analyzeUseCase = new AnalyzeUseCase( repository);

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChromaCaptureViewModel.class)) {
            return (T) new ChromaCaptureViewModel(analyzeUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
