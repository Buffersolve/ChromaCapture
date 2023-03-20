package com.buffersolve.chromacapture.domain.repository;

import com.buffersolve.chromacapture.domain.model.ColorModel;

public interface Repository {

    ColorModel analyze(byte[] imageData);

}
