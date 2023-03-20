package com.buffersolve.chromacapture.domain.usecase;

import com.buffersolve.chromacapture.domain.model.ColorModel;
import com.buffersolve.chromacapture.domain.repository.Repository;

public class AnalyzeUseCase {
Repository repository;

    public AnalyzeUseCase(Repository repository) {
        this.repository = repository;
    }

    public ColorModel execute(byte[] imageData) {
        return repository.analyze(imageData);
    }

}
