package com.poornamith.ricetest;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Created by poornamith on 05, 08, 2017.
 */

public class Rice {

    Mat src, srcEdited;
    String path;
    int thresh = 90, threshMax = 255;

    //Constructor
    public Rice(Mat src) {
        this.src = src;
    }

    //getters
    public Mat getSrc() {
        return src;
    }

    //Calculations
    public void calculateVolume() {

        Imgproc.cvtColor(src, srcEdited, Imgproc.COLOR_BGR2GRAY);

        //Imgproc.GaussianBlur(srcEdited, srcEditedSize(9,9), 1.5);

        //drawContuors();
    }

    /*private void drawContuors() {

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        // find contours:
        Imgproc.findContours(srcEdited, contours, hierarchy,
                Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(src, contours, contourIdx,
                    new Scalar(0, 0, 255), -1);
        }
    }*/

}
