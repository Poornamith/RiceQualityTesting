package com.poornamith.ricetest;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //create a MainActivity TAG
    private static final String TAG="MainActivity";

    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMERA_PIC_REQUEST = 1888;

    ImageView imageView;

    Button buttonCalc;
    Button buttonBrowse;

    TextView textViewResult;
    TextView textViewQuality;
    TextView textViewHigh;
    TextView textViewMid;
    TextView textViewLow;

    TextView textViewDebug;
    String picturePath;

    double mainGaussianSigma = 1.5;
    int mainErrodeKernal = 21;
    int mainThresholdVal = 200;
    int mainThresholdMax = 255;
    int mainLimitUpper = 75;
    int mainLimitLower = 25;

    int grainsCount;
    List<Double> area = new ArrayList<>();


    //final GlobalVariables globalVariables = new GlobalVariables();


    //check for configuration loopholes
    static {
        if(OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV successfully loaded");
        }
        else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    //initialization
    private void init() {

        imageView = (ImageView) findViewById(R.id.image_view);

        buttonBrowse = (Button) findViewById(R.id.button_browse);
        buttonCalc = (Button) findViewById(R.id.button_calc);

        textViewResult = (TextView) findViewById(R.id.text_view_result);
        textViewQuality = (TextView) findViewById(R.id.text_view_quality);
        textViewHigh = (TextView) findViewById(R.id.text_view_high);
        textViewMid = (TextView) findViewById(R.id.text_view_mid);
        textViewLow = (TextView) findViewById(R.id.text_view_low);

        textViewDebug = (TextView) findViewById(R.id.text_view_debug);

        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewResult.setText("0");
                textViewQuality.setText("");
                textViewHigh.setText("0");
                textViewMid.setText("0");
                textViewLow.setText("0");

                //create new intent to select an image from gallery (filters: Images)
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //globalSettings();

                    Log.d(TAG, " " + mainGaussianSigma + " " + mainErrodeKernal + " " + mainThresholdVal + " " + mainThresholdMax);

                    if(mainGaussianSigma == 0) {
                        mainGaussianSigma = 1.5;
                        mainErrodeKernal = 21;
                        mainThresholdVal = 200;
                        mainThresholdMax = 255;
                        mainLimitUpper = 75;
                        mainLimitLower = 25;
                    }

                    //Image processing
                    openCVImgProcess();

                    //Percentage Calculation
                    percentCalc();

                }
                catch (Exception ex) {

                    textViewDebug.setText("Error!");
                    Log.d(TAG, ex.getMessage());
                }
            }
        });

    }

    //global settings initialization
    private void globalSettings() {

        final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();

        mainGaussianSigma = globalVariables.getGaussianSigma();
        mainErrodeKernal = globalVariables.getErodeKernal();
        mainThresholdVal = globalVariables.getThresholdVal();
        mainThresholdMax = globalVariables.getThresholdMax();
        mainLimitUpper = globalVariables.getLimitUpper();
        mainLimitLower = globalVariables.getLimitLower();

        Log.d(TAG, " " + mainGaussianSigma + " " + mainErrodeKernal + " " + mainThresholdVal + " " + mainThresholdMax);
    }

    //OpenCV image processing
    private void openCVImgProcess() {

        //Create a Mat image from image path
        Mat src = imread(picturePath, CvType.CV_8UC4);
        Mat srcEdited = new Mat();

        //Convert Original src to grayscale
        Imgproc.cvtColor(
                src,                        // input image
                src,                        // output image
                Imgproc.COLOR_BGR2GRAY);    //conversion method

        //openCV Size variable
        org.opencv.core.Size s = new Size(11, 11);
        //Apply Gaussian Blur
        Imgproc.GaussianBlur(
                src,                // input image
                srcEdited,          // output image
                s,                  // Mask size
                mainGaussianSigma);               // Sigma value of Gauss mask

        //openCV Size variable
        s = new Size(mainErrodeKernal, mainErrodeKernal);
        Mat kernal = Imgproc.getStructuringElement(
                Imgproc.MORPH_ELLIPSE,              // shape
                s);                                 // anchor position within the element
        //Erode
        Imgproc.erode(
                srcEdited,          // input image
                srcEdited,          // output image
                kernal);            // kernal for erosion


        //srcEdited = srcEdited > 200;
        //Imgproc.threshold(srcEdited, srcEdited, mainThresholdVal, mainThresholdMax, Imgproc.THRESH_BINARY);

        Imgproc.Canny(
                srcEdited,             // input image
                srcEdited,             // output image
                50,                    // threshold
                255);                  // max threshold

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();      // hierarchy mat
        Imgproc.findContours(
                srcEdited,              // input image
                contours,               // output vector
                hierarchy,              // output array of Mat
                Imgproc.RETR_TREE,      // contour retrieval mode
                Imgproc.CHAIN_APPROX_SIMPLE);

        //Mat source = new Mat();
        //int count = 0;
        area.clear();
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {

            area.add(Imgproc.contourArea(contours.get(contourIdx)));

            if ((area.get(contourIdx) > 300) && (area.get(contourIdx) < 3000)) {
            Imgproc.drawContours(src, contours, contourIdx, new Scalar(255, 0, 0), 5);

            Log.d(TAG, "area: " + Double.toString(area.get(contourIdx)));
            //count++;
            }
        }

        grainsCount = contours.size();
        Log.d(TAG, Integer.toString(grainsCount));
        textViewDebug.setText(Integer.toString(grainsCount / 2 + 1));

        //convert to Bitmap (Mat to Bitmap) to display in ImageView
        Bitmap bm = Bitmap.createBitmap(srcEdited.cols(), srcEdited.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(srcEdited, bm);
        imageView.setImageBitmap(bm);

    }

    //Calculations
    private void percentCalc() {

        //sort the list and get max
        Collections.sort(area);

        //get the maximum
        double maxArea = area.get(area.size() - 1);

        //get the sum of the areas
        double sum = 0;
        for (double val : area) {

            sum += val;
            Log.d(TAG, Double.toString(val));
        }

        Log.d(TAG, "Max: " + Double.toString(maxArea) + "Sum = " + sum);

        sum = sum / grainsCount;

        Log.d(TAG, "new Sum: " + sum);

        int minVal = 0, midVal = 0, maxVal = 0;

        for (double val : area) {

            //Grain area parameter
            double grainAreaPara = (val / maxArea) * 100;

            //classify each grain and count
            if(grainAreaPara >= mainLimitUpper) {
                maxVal++;
            }
            else if(grainAreaPara >= mainLimitLower) {
                midVal++;
            }
            else {
                minVal++;
            }

            Log.d(TAG, "GrainAreaPara: " + grainAreaPara);
        }

        Log.d(TAG, "Min: " + minVal + "Mid: " + midVal + "Max: " + maxVal);

        //disply the values
        maxVal =  (int)(((double)maxVal / (double)grainsCount ) * 100);
        midVal =  (int)(((double)midVal / (double)grainsCount ) * 100);
        minVal =  (int)(((double)minVal / (double)grainsCount ) * 100);

        Log.d(TAG, "Min: " + minVal + "Mid: " + midVal + "Max: " + maxVal);

        /*textViewHigh.setText(maxVal + "%");
        textViewMid.setText(midVal + "%");
        textViewLow.setText(minVal + "%");*/
        animateTextView(0, maxVal, textViewHigh);
        animateTextView(0, midVal, textViewMid);
        animateTextView(0, minVal, textViewLow);

        if ((maxVal > midVal) && (maxVal > minVal)) {
            animateTextView(0, maxVal, textViewResult);
            //textViewResult.setText(maxVal + "%");
            textViewQuality.setText("HIGH");
        }

        if ((midVal > maxVal) && (midVal > minVal)) {
            animateTextView(0, midVal, textViewResult);
            //textViewResult.setText(midVal + "%");
            textViewQuality.setText("MEDIUM");
        }

        if ((minVal > maxVal) && (minVal > midVal)) {
            //textViewResult.setText(minVal + "%");
            animateTextView(0, minVal, textViewResult);
            textViewQuality.setText("LOW");
        }
    }

    //text Animation
    public void animateTextView(int initialValue, int finalValue, final TextView  textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(1500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString());

            }
        });
        valueAnimator.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                /*String directoryPath = Environment.getExternalStorageDirectory() + "/";
                String filePath = directoryPath+Long.toHexString(System.currentTimeMillis())+".jpg";
                File directory = new File(directoryPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }*/

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //intent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();

        //final GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();

        //globalVariables.setBm();
    }

    //get the browsed Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Log.d(TAG, "Image Path: " + picturePath);

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK ) {

            //String Path= mImageCaptureUri.getPath();
            //Bitmap image = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(image);
            //picturePath = image.getP

            /*Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            //ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
*/
            /*Bitmap photo = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            picturePath = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), photo, "Title", null);
            Uri tempUri =  Uri.parse(picturePath);

            Cursor cursor = getContentResolver().query(tempUri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            File finalFile = new File(cursor.getString(idx));


            picturePath = finalFile.getPath();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
*/
        }
    }




    @Override
    protected  void onResume() {
        super.onResume();

        try {
            globalSettings();
        }
        catch (Exception ex) {
            textViewDebug.setText("Error!");
            Log.d(TAG, ex.getMessage());
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //intent.putExtra("message", message);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);

        }
        else if (id == R.id.nav_gallery) {
            //create new intent to select an image from gallery (filters: Images)
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);

        }
        else if (id == R.id.nav_manage) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //intent.putExtra("message", message);
            startActivity(intent);

        }
        else if (id == R.id.nav_team) {

            Intent intent = new Intent(MainActivity.this, TeamActivity.class);
            //intent.putExtra("message", message);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
