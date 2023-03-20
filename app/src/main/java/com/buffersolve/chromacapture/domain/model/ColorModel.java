package com.buffersolve.chromacapture.domain.model;

public class ColorModel {
    private String rgb;
    private String hex;
    private Error error;

    public ColorModel(String rgb, String hex, Error error) {
        this.rgb = rgb;
        this.hex = hex;
        this.error = error;
    }

    public String getRgb() {
        return rgb;
    }

    public String getHex() {
        return hex;
    }

    public Error getError() {
        return error;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
