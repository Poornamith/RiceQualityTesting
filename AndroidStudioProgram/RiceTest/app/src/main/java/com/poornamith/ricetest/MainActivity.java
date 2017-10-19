package com.poornamith.ricetest;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button buttonCalc;
    Button buttonBrowse;
    ImageView imageView;
    String picturePath;

    private static int RESULT_LOAD_IMAGE = 1;


    //create a MainActivity TAG
    private static final String TAG="MainActivity";

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
        try {

            imageView = (ImageView) findViewById(R.id.image_view);

            buttonBrowse = (Button) findViewById(R.id.button_browse);
            buttonBrowse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //create new intent to select an image from gallery (filters: Images)
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            });

            buttonCalc = (Button) findViewById(R.id.button_calc);
            buttonCalc.setEnabled(true);
            buttonCalc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Create a Mat image from image path
                    Mat src = imread(picturePath, CvType.CV_8UC4);
                    Mat srcEdited = new Mat();

                    //Convert Original src to grayscale
                    Imgproc.cvtColor(
                            src,                        // input image
                            src,                        // output image
                            Imgproc.COLOR_BGR2GRAY);    //conversion method

                    //openCV Size variable
                    org.opencv.core.Size s = new Size(9,9);
                    //Apply Gaussian Blur
                    Imgproc.GaussianBlur(
                            src,                // input image
                            srcEdited,          // output image
                            s,                  // Mask size
                            1.5);               // Sigma value of Gauss mask

                    //openCV Size variable
            /*org.opencv.core.Size s = new Size(21,21);
            Mat kernal = Imgproc.getStructuringElement(
                    Imgproc.MORPH_ELLIPSE,              // shape
                    s,                                  // k size
                    org.opencv.Point(1,1));             // anchor position within the element
            //Erode
            Imgproc.erode(
                    srcEdited,          // input image
                    srcEdited,          // output image
                    kernal);            // kernal for erosion
            */


                    //srcEdited = srcEdited > 200;
                    Imgproc.threshold(srcEdited, srcEdited, 200, 255, Imgproc.THRESH_BINARY);

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

                    Mat source = new Mat();
                    //int count = 0;
                    for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                        double area = Imgproc.contourArea(contours.get(contourIdx));

                        //if (area > 100) {

                        Imgproc.drawContours(src, contours, contourIdx, new Scalar(255, 0, 0), 5);
                        Log.d(TAG, "area: " + Double.toString(area));
                        //count++;
                        //}
                    }
                    Log.d(TAG, Integer.toString(contours.size()));
                    //textView.setText("No: of samples: " + Integer.toString(contours.size()));



                    //convert to Bitmap (Mat to Bitmap) to display in ImageView
                    Bitmap bm = Bitmap.createBitmap(srcEdited.cols(), srcEdited.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(src, bm);
                    imageView.setImageBitmap(bm);

                }
            });

            Log.d(TAG, "Initialization Complete");
        }
        catch (Exception ex) {
            Log.d(TAG, "Error Initialization. ERROR: " + ex.getMessage());
        }
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
