package com.poornamith.ricetest;

import android.app.Application;
import android.graphics.Bitmap;

/**
 * Created by poornamith on  , 10, 2017.
 */

public class GlobalVariables extends Application{

    private double gaussianSigma;
    private int erodeKernal;
    private int thresholdVal;
    private int thresholdMax;
    private int limitUpper;
    private int limitLower;

    public double getGaussianSigma() {
        return gaussianSigma;
    }

    public void setGaussianSigma(double gaussianSigma) {
        this.gaussianSigma = gaussianSigma;
    }

    public int getErodeKernal() {
        return erodeKernal;
    }

    public void setErodeKernal(int erodeKernal) {
        this.erodeKernal = erodeKernal;
    }

    public int getThresholdVal() {
        return thresholdVal;
    }

    public void setThresholdVal(int thresholdVal) {
        this.thresholdVal = thresholdVal;
    }

    public int getThresholdMax() {
        return thresholdMax;
    }

    public void setThresholdMax(int thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    public int getLimitUpper() {
        return limitUpper;
    }

    public void setLimitUpper(int limitUpper) {
        this.limitUpper = limitUpper;
    }

    public int getLimitLower() {
        return limitLower;
    }

    public void setLimitLower(int limitLower) {
        this.limitLower = limitLower;
    }
}
