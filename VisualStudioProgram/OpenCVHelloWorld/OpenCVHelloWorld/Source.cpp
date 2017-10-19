#include<opencv2/opencv.hpp>
#include<iostream>
#include<conio.h>

using namespace std;
using namespace cv;

Mat oImage;// variable for original image
Mat grayImage;//varible for gary image
Mat blurImage;//variable for blure image
Mat cannyImage;//variable for canny image

int numObject;//variable for wich represent of object on the image
int thresh = 90;
int maxThresh = 255;

int val = 0;
int area[200];

void draw_Contuors(int, void*);
void calculate();
RNG rng(12345);

int main() {

	oImage = imread("../data/IMG_20171016_121633.jpg");//load image into oImage variable
	namedWindow("Original Image", CV_WINDOW_NORMAL);
	imshow("Original Image", oImage);
	//waitKey(0);//wait until press anykey

	cvtColor(oImage, grayImage, CV_BGR2GRAY);//ORIGINAL IMAGGE TO GRAY
	namedWindow("Grayscale", CV_WINDOW_NORMAL);//screen for image
	imshow("Grayscale", grayImage);//show image
								   //waitKey(0);// wait untill press anykey 

	

	GaussianBlur(grayImage,//source image of gausse
		blurImage,//output image of gausse
		Size(11, 11),//size of the applied mask
		1.5);//sigma value of gauss mask
	
	medianBlur(blurImage, blurImage, 5);

	namedWindow("Blur Image", CV_WINDOW_NORMAL);
	imshow("Blur Image", blurImage);

	//adaptiveThreshold(blurImage, blurImage, 255,
	//	ADAPTIVE_THRESH_GAUSSIAN_C,
	//	THRESH_BINARY, 101, 1);

	Mat kernal = getStructuringElement(MORPH_ELLIPSE, Size(21, 21), Point(1,1));
	erode(blurImage, blurImage, kernal);

	
	threshold(blurImage, blurImage, 0, 255, CV_THRESH_OTSU | CV_THRESH_BINARY);
	//dilate(blurImage, blurImage, Mat()); //dilate to imporove quality of binary image


	namedWindow("Erosion", CV_WINDOW_NORMAL);
	imshow("Erosion", blurImage);
	//waitKey(0);//wait until press anykey


	//createTrackbar("canny Thresh", "mage mala", &thresh, maxThresh, draw_Contuors);

	draw_Contuors(0, 0);


	//calc
	//calculate();


	waitKey(0);
	return(0);

}

void draw_Contuors(int, void*) {//function for draw contuors
	numObject = 0;

	vector<vector<Point>>contuors;//vector for contuors
	vector<Vec4i> hierarchy;//contour for hierarchyg

	Canny(blurImage,//source image for canny
		cannyImage,//output image for canny
		thresh,//low treshold value for canny
		maxThresh);//high treshold value for canny
	namedWindow("canny mala", CV_WINDOW_NORMAL);
	imshow("canny mala", cannyImage);
	//waitKey(0);//wait until press anykey

	findContours(cannyImage, contuors, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));

	Mat drawing = Mat::zeros(cannyImage.size(), CV_8UC3);

	//int riceCount = 0;
	val = contuors.size();
	//int sum = 0;
	int counter = 0;
	for (int i = 0; i < contuors.size(); i = i + 2) {

		//area[i] = contourArea(contuors[i]);
		//sum += area[i];
		//if (contourArea(contuors[i]) > 500)
		//{
			area[counter++] = contourArea(contuors[i]);

			Scalar color = Scalar(rng.uniform(0, 255), rng.uniform(0, 255), rng.uniform(0, 255));
			drawContours(drawing, contuors, i, color, 5, 8, hierarchy, 500, Point());
		//}

		namedWindow("Contuors", CV_WINDOW_NORMAL);
		imshow("Contuors", drawing);

		//cout << "area:" << area[] << endl;
	}


	cout << endl << "Grains: " << counter << endl;
	//sum = sum / contuors.size();


	//cout << "no of rice:" << /*contuors.size()*/  << endl << endl;

	//cout << "sum: " << sum;
	std::sort(area, area + counter);
	/*for (int i = 0; i<val; i++)
	{
	for (int j = i + 1; j<val; j++)
	{
	if (area[i]>area[j])
	{
	temp = area[i];
	area[i] = area[j];
	area[j] = temp;
	}
	}
	}*/

	for (int i = 0; i < counter; i++)
	{

		cout << area[i] << ", ";
	}

	/*int temp = 0;
	int tempPt = 0;
	for (int i = 0; i < contuors.size() - 1; i++)
	{
	if (temp <= (area[i + 1] - area[i]))
	{
	temp = (area[i + 1] - area[i]);
	tempPt = i;
	}
	}

	cout << endl << "dff: " << temp << " at " << tempPt;
	*/

}

void calculate() 
{
	int sum = 0;
	int counter = 0;
	for (int i = 0; i < val; i++)
	{
		if (area[i] > 0)
		{
			sum += area[i];
			counter++;
		}
	}

	cout << endl << "Sum: " << sum;
	
	sum = sum / counter;

	cout << "Avg: " << sum << "\tGrains: " << counter << endl;


	int minVal = 0, midVal = 0, maxVal = 0;

	int maxArea = area[counter - 1];

	for (int i = 0; i < counter; i++)
	{
		//float newArea = area[i];
		int grainAreaPara = ((float)area[i] / (float)maxArea) * 100;

		if (grainAreaPara >= 75)
		{
			maxVal++;
		}
		else if (grainAreaPara >= 25)
		{
			midVal++;
		}
		else
		{
			minVal++;
		}



		cout << grainAreaPara << ", ";
	}
	
	cout << endl << "min: " << minVal << endl << "mid: " << midVal << endl << "max: " << maxVal;

	cout << endl;

	if ((maxVal > midVal) && (maxVal > minVal))
	{
		cout << ((float)maxVal / (float)counter) * 100;
	}

	if ((midVal > maxVal) && (midVal > minVal))
	{
		cout << ((float)midVal / (float)counter) * 100;
	}

	if ((minVal > maxVal) && (minVal > midVal))
	{
		cout << ((float)minVal / (float)counter) * 100;
	}


}