package com.buffersolve.chromacapture.data.repository;

import com.buffersolve.chromacapture.data.picanalyze.PictureAnalyze;
import com.buffersolve.chromacapture.domain.model.ColorModel;
import com.buffersolve.chromacapture.domain.repository.Repository;

public class RepositoryImplementation implements Repository {

    public ColorModel analyze(byte[] imageData) {
        PictureAnalyze pictureAnalyze = new PictureAnalyze(imageData);
        return pictureAnalyze.analyze();
    }


}
