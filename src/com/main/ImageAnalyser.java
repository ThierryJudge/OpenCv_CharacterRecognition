package com.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.rmi.CORBA.Util;

import org.omg.CORBA.IMP_LIMIT;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;

import com.utils.AnalyseReturn;
import com.utils.Constants;
import com.utils.ImageViewer;
import com.utils.MultipleImageViewer;
import com.utils.PossibleChar;
import com.utils.PossiblePlate;
import com.utils.Utils;

public class ImageAnalyser 
{
	
	KNearest knn;
	KNearest letterKnn;
	Mat image;
	Mat grayImage = new Mat();
	Mat blurImage = new Mat();
	Mat threshImage = new Mat();
	Mat contoursImage;
	Mat validContourImage;
	
	ArrayList<PossibleChar> possibleChars = new ArrayList<PossibleChar>();
	ArrayList<PossiblePlate> possiblePlates = new ArrayList<>();
	
	
	public ImageAnalyser(String fileName, KNearest knn) 
	{
		this.image = Imgcodecs.imread(fileName);
		this.knn = knn;
		
		init();
	}
	
	public ImageAnalyser(Mat image, KNearest knn)
	{
		this.knn = knn;
		this.image = image;
		
		init();
	}
	
	public ImageAnalyser(String fileName, KNearest knn, KNearest letterKnn)
	{
		this.knn = knn;
		this.letterKnn = letterKnn;
		this.image = Imgcodecs.imread(fileName);
		
		init();
	}
	
	private void init()
	{
		if(image.dataAddr() == 0)
		{
			System.out.println("Can't open file");
			return;
		}
		
		contoursImage = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0,0,0));
		validContourImage = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0,0,0));
	}
	

	public AnalyseReturn analyse(boolean showSteps)
	{	
		
		
		preprocess();
	
		getListOfChars();
		
		getListOfPossiblePlates();
		
		
		for(PossiblePlate possiblePlate : possiblePlates)
		{
			possiblePlate.drawRectAroundPlate(image);
			System.out.println("New PossiblePlate");
			String returnString = "";
			
			for(PossibleChar character : possiblePlate.chars)
			{	
				Mat roi = threshImage.submat(character.boundingRect);
				Imgproc.resize(roi, roi, new Size(Constants.SIZE, Constants.SIZE));
				roi.convertTo(roi, CvType.CV_32F);
				
				Mat res = new Mat();
		        float p = knn.findNearest(roi.reshape(1,1), Constants.K, res);
		        
				Imgproc.putText(image,Character.toString ((char) (int) p), character.boundingRect.br(), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0));
				System.out.println(Character.toString ((char) (int) p));
				
				returnString += Character.toString ((char) (int) p);
			}
			possiblePlate.setPlate(returnString);
		
		}
		
		
		
		if(showSteps)
		{
			showSteps();
		}
		
		String returnString = "";
		
		for(PossiblePlate possiblePlate : possiblePlates)
		{
			if(possiblePlate.getPlate().length() > returnString.length())
			{
				returnString = possiblePlate.getPlate();
			}
		}
		
		return new AnalyseReturn(returnString, image);
	}
	
	private void preprocess()
	{
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
		Imgproc.bilateralFilter(grayImage, blurImage, 9,100,100);
		Imgproc.adaptiveThreshold(blurImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 19, 9);
		
	}
	
	private void getListOfChars()
	{
		Mat threshCopyImage = threshImage.clone(); //findContours modifies the image;
		
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(threshCopyImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for(MatOfPoint mat : contours) 
		{
			PossibleChar possibleChar = new PossibleChar(mat.toList(), Imgproc.boundingRect(mat), Imgproc.contourArea(mat));
			
			Imgproc.drawContours(contoursImage, contours, contours.indexOf(mat), new Scalar(255, 255, 255), 1);
			
			if(possibleChar.isContourValid())
			{
				possibleChars.add(possibleChar);
				Imgproc.drawContours(validContourImage, contours, contours.indexOf(mat), new Scalar(255, 255, 255), 1);
			}
			else
			{
				Rect rect = possibleChar.boundingRect;
				Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 255, 0),1, 8,0);
			}
		}
		
		eliminateInnerChars();
		
		for(PossibleChar possibleChar : possibleChars)
		{
			Rect rect = possibleChar.boundingRect;
			Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 0, 255),1, 8,0);
		}
		
		Utils.sortByY(possibleChars);
		
	}
	
	private void getListOfPossiblePlates() 
	{
		ArrayList<PossibleChar> possibleCharsCopy =  (ArrayList<PossibleChar>) possibleChars.clone();
		ArrayList<PossibleChar> deleteList;
		
		for(PossibleChar contour : possibleChars)
		{
			List<PossibleChar> charList = new ArrayList<>();
			deleteList = new ArrayList<>();
			
			for(PossibleChar innerContour : possibleCharsCopy)
			{
				if(contour.intersects(innerContour) && contour.matches(innerContour)) //&& contour.isNear(innerContour) )
				{
					charList.add(innerContour);
					deleteList.add(innerContour);
				}
			}
			
			
			possibleCharsCopy.removeAll(deleteList);
			
			if(charList.size() >= 3)
			{
				Utils.sortByX((ArrayList<PossibleChar>) charList);
				PossiblePlate PossiblePlate = new PossiblePlate(charList);
				if(PossiblePlate.isValid())
				{
					possiblePlates.add(PossiblePlate);
				}
			}
			
		}
	}

	private void eliminateInnerChars()
	{
		ArrayList<PossibleChar> deleteList = new ArrayList<PossibleChar>();
		
		for(PossibleChar p : possibleChars)
		{
			for(PossibleChar p2 : possibleChars)
			{
				if(p != p2)
				{
					if(p.boundingRect.contains(p2.boundingRect.br()) && p.boundingRect.contains(p2.boundingRect.tl()))
					{
						deleteList.add(p2);
					}
				}
			}
		}
		
		possibleChars.removeAll(deleteList);
	}
	
	
	private void showSteps()
	{	
		ArrayList<AnalyseReturn> list = new ArrayList<>();
		
		AnalyseReturn img  = new AnalyseReturn("Image", image);
		list.add(img);
		AnalyseReturn gray = new AnalyseReturn("Grayscale", grayImage);
		list.add(gray);
		AnalyseReturn blur = new AnalyseReturn("Blur", blurImage);
		list.add(blur);
		AnalyseReturn tresh  = new AnalyseReturn("Threshold", threshImage);
		list.add(tresh);
		AnalyseReturn conturs  = new AnalyseReturn("Contours", contoursImage);
		list.add(conturs);
		AnalyseReturn vcontours  = new AnalyseReturn("Valid Contours", validContourImage);
		list.add(vcontours);
		
		
		new MultipleImageViewer("STEPS", list);
		
		
	}
	
	public ArrayList<Mat> getImages()
	{
		ArrayList<Mat> mats = new ArrayList<>();
		mats.add(grayImage);
		mats.add(blurImage);
		mats.add(threshImage);
		mats.add(contoursImage);
		mats.add(validContourImage);
		mats.add(image);
		
		return mats;
	}
	
	public AnalyseReturn analyse()
	{	
		
		String returnString = "";
		preprocess();
	
		getListOfChars();
	
		getListOfPossiblePlates();
		
		
		Utils.sortByX(possibleChars);
		for(PossibleChar character : possibleChars)
		{	
			Mat roi = threshImage.submat(character.boundingRect);
			Imgproc.resize(roi, roi, new Size(Constants.SIZE, Constants.SIZE));
			roi.convertTo(roi, CvType.CV_32F);
			
			Mat res = new Mat();
	        float p = knn.findNearest(roi.reshape(1,1), Constants.K, res);
	        
			Imgproc.putText(image,Character.toString ((char) (int) p), character.boundingRect.br(), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0));
			
			returnString += Character.toString ((char) (int) p);
		}
		
		showSteps();
		return new AnalyseReturn(returnString, image);
	}
	public AnalyseReturn analyse2Sections(boolean showSteps)
	{	
		
		if(letterKnn == null)
		{
			System.out.println("ERROR: NO SECOND KNN");
			return null;
		}
		
		preprocess();
	
		getListOfChars();
		
		getListOfPossiblePlates();
		
		
		for(PossiblePlate possiblePlate : possiblePlates)
		{
			possiblePlate.drawRectAroundPlate(image);
			System.out.println("New PossiblePlate");
			String returnString = "";
			
			
			int i = 0;
			for(; i < 3; i++)
			{
				Mat roi = threshImage.submat(possiblePlate.chars.get(i).boundingRect);
				Imgproc.resize(roi, roi, new Size(Constants.SIZE, Constants.SIZE));
				roi.convertTo(roi, CvType.CV_32F);
				
				Mat res = new Mat();
		        float p = letterKnn.findNearest(roi.reshape(1,1), Constants.K, res);
		        
				Imgproc.putText(image,Character.toString ((char) (int) p), possiblePlate.chars.get(i).boundingRect.br(), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0));
				System.out.println(Character.toString ((char) (int) p));
				
				returnString += Character.toString ((char) (int) p);
			}
			
			for(; i < possiblePlate.chars.size(); i++)
			{
				Mat roi = threshImage.submat(possiblePlate.chars.get(i).boundingRect);
				Imgproc.resize(roi, roi, new Size(Constants.SIZE, Constants.SIZE));
				roi.convertTo(roi, CvType.CV_32F);
				
				Mat res = new Mat();
		        float p = knn.findNearest(roi.reshape(1,1), Constants.K, res);
		        
				Imgproc.putText(image,Character.toString ((char) (int) p), possiblePlate.chars.get(i).boundingRect.br(), Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0));
				System.out.println(Character.toString ((char) (int) p));
				
				returnString += Character.toString ((char) (int) p);
			}
			
			possiblePlate.setPlate(returnString);
		
		}
		
		
		
		if(showSteps)
		{
			showSteps();
		}
		
		String returnString = "";
		
		for(PossiblePlate possiblePlate : possiblePlates)
		{
			if(possiblePlate.getPlate().length() > returnString.length())
			{
				returnString = possiblePlate.getPlate();
			}
		}
		
		return new AnalyseReturn(returnString, image);
	}
}
