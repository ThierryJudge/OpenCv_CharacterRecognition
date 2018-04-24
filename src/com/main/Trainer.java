package com.main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.utils.Converters;

import com.utils.Constants;
import com.utils.ImageViewer;
import com.utils.Utils;

public class Trainer {

	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		KNearest knn = KNearest.create();
		
		trainAndTest("digits.png", knn, true);
		//trainAndTest("characters.png", knn, true);
	}
	
	private static void trainAndTest(String trainImage, KNearest knn, boolean exit)
	{
		long startTime = System.currentTimeMillis();
		long endTime;  
		long totalTime;
		
		Mat digits = Imgcodecs.imread(trainImage);
		
		if(digits.dataAddr() == 0)
		{
			System.out.println("Can't open file");
			return;
		}
		
		Mat trainData = new Mat();
		List<Integer> trainLabels = new ArrayList<Integer>();
		
		List<Mat> testData = new ArrayList<Mat>();
		List<Integer> testLabels = new ArrayList<Integer>();
		
		
		for(int i = 0; i < Constants.NUMBER_OF_CHARS * Constants.NUMBER_OF_FONTS; i++)
		{
			for(int j = 0; j < Constants.DIGITS_PER_ROW; j++)
			{
				Mat num = getSingleNumber(digits, i, j);
				
				Mat subMat;
				Mat grayImage = new Mat();
				Mat blurImage = new Mat();
				Mat threshImage = new Mat();

				Imgproc.cvtColor(num, grayImage, Imgproc.COLOR_BGR2GRAY);
				Imgproc.GaussianBlur(grayImage, blurImage, new Size(5,5), 0);
				Imgproc.adaptiveThreshold(blurImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
			
				ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Mat hierarchy = new Mat();
				Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				
				if(contours.size() > 0)
				{
					//subMat = threshImageCopy.submat(Imgproc.boundingRect(contours.get(0)));  	// train with threshold image
					subMat = num.submat(Imgproc.boundingRect(contours.get(0)));					// train with regular image
					subMat.convertTo(subMat, CvType.CV_32F);
					Imgproc.resize(subMat, subMat, new Size(Constants.SIZE, Constants.SIZE));
					
					if(j % 2 == 0) // add to training set 
					{
						trainData.push_back(subMat.reshape(1,1));
						trainLabels.add(Integer.valueOf(Constants.CHARACTERS.charAt(i % Constants.NUMBER_OF_CHARS)));
					}
					else // add to testing set
					{
						testData.add(subMat.reshape(1,1));
						testLabels.add(Integer.valueOf(Constants.CHARACTERS.charAt(i % Constants.NUMBER_OF_CHARS)));
					}
				}
			}
		}
		
		knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabels));
		
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("Training time: " + Utils.getTimeString(totalTime));
		
		//TEST 
		startTime = System.currentTimeMillis();
		
		int successfulTests = 0;
		int failedTests = 0;
		for(int i = 0; i < testData.size(); i++)
		{
			Mat res = new Mat();
			float p = knn.findNearest(testData.get(i).reshape(1,1), Constants.K, res);
			char result = ((char) (int) p);
			char expected = ((char) (int) testLabels.get(i));
			
			if(result == expected)
			{
				successfulTests++;
			}
			else
			{
				failedTests++;
				System.out.println("TEST FAILED: EXPECTED: " + expected + ", GOT: " + result);
			}
		}
		
		endTime   = System.currentTimeMillis();
		totalTime = endTime - startTime;
		
		System.out.println("Test results --------- ");
		System.out.println("Time: " + Utils.getTimeString(totalTime));
		System.out.println("Total tests: " + testData.size());
		System.out.println("Successful tests: " + successfulTests);
		System.out.println("Failed tests: " + failedTests);
		System.out.println("Rate: " + (float) successfulTests/ (float)testData.size() * 100 + "%");
	
		if(exit)
		{
			System.exit(0);
		}
		
	}
	
	
	public static boolean train(String trainImage, KNearest knn, int numberOfChars, String labels)
	{
		Mat digits = Imgcodecs.imread(trainImage);
		
		if(digits.dataAddr() == 0)
		{
			System.out.println("Can't open file");
			return false;
		}
		
		Mat trainData = new Mat();
		List<Integer> trainLabels = new ArrayList<Integer>();
		
		
		for(int i = 0; i < numberOfChars * Constants.NUMBER_OF_FONTS; i++)
		{
			for(int j = 0; j < Constants.DIGITS_PER_ROW; j++)
			{
				Mat num = getSingleNumber(digits, i, j);
				
				Mat subMat;
				Mat grayImage = new Mat();
				Mat blurImage = new Mat();
				Mat threshImage = new Mat();
				Mat threshImageCopy = new Mat();

				//preprocess image
				Imgproc.cvtColor(num, grayImage, Imgproc.COLOR_BGR2GRAY);
				Imgproc.GaussianBlur(grayImage, blurImage, new Size(5,5), 0);
				Imgproc.adaptiveThreshold(blurImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
				
				threshImageCopy = threshImage.clone();
				
				//find all contours
				ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Mat hierarchy = new Mat();
				Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				
				//add sub-images to training data
				if(contours.size() > 0)
				{
					subMat = threshImageCopy.submat(Imgproc.boundingRect(contours.get(0)));
					subMat.convertTo(subMat, CvType.CV_32F);
					Imgproc.resize(subMat, subMat, new Size(Constants.SIZE, Constants.SIZE));
					
					trainData.push_back(subMat.reshape(1,1));
					trainLabels.add(Integer.valueOf(labels.charAt(i % numberOfChars)));
				}
			}
		}
		
		//ImageViewer.showImage(digits, "digits", true);
	
		knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabels));
		
		return knn.isTrained();
	}
	
	public static boolean train(String trainImage, KNearest knn)
	{
		Mat digits = Imgcodecs.imread(trainImage);
		
		if(digits.dataAddr() == 0)
		{
			System.out.println("Can't open file");
			return false;
		}
		
		Mat trainData = new Mat();
		List<Integer> trainLabels = new ArrayList<Integer>();
		
		
		for(int i = 0; i < Constants.NUMBER_OF_CHARS * Constants.NUMBER_OF_FONTS; i++)
		{
			for(int j = 0; j < Constants.DIGITS_PER_ROW; j++)
			{
				Mat num = getSingleNumber(digits, i, j);
				
				Mat subMat;
				Mat grayImage = new Mat();
				Mat blurImage = new Mat();
				Mat threshImage = new Mat();
				Mat threshImageCopy = new Mat();

				//preprocess image
				Imgproc.cvtColor(num, grayImage, Imgproc.COLOR_BGR2GRAY);
				Imgproc.GaussianBlur(grayImage, blurImage, new Size(5,5), 0);
				Imgproc.adaptiveThreshold(blurImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
				
				threshImageCopy = threshImage.clone();
				
				//find all contours
				ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Mat hierarchy = new Mat();
				Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
				
				//add sub-images to training data
				if(contours.size() > 0)
				{
					subMat = threshImageCopy.submat(Imgproc.boundingRect(contours.get(0)));
					subMat.convertTo(subMat, CvType.CV_32F);
					Imgproc.resize(subMat, subMat, new Size(Constants.SIZE, Constants.SIZE));
					
					trainData.push_back(subMat.reshape(1,1));
					trainLabels.add(Integer.valueOf(Constants.CHARACTERS.charAt(i % Constants.NUMBER_OF_CHARS)));
				}
			}
		}
		
		//ImageViewer.showImage(digits, "digits", true);
	
		knn.train(trainData, Ml.ROW_SAMPLE, Converters.vector_int_to_Mat(trainLabels));
		
		return knn.isTrained();
	
	}
	
	private static Mat getSingleNumber(Mat image, int i, int j)
	{
		return image.submat(new Rect(j*Constants.NUMBER_SIZE,(i)*Constants.NUMBER_SIZE, Constants.NUMBER_SIZE, Constants.NUMBER_SIZE + 20));
		
	}

}
