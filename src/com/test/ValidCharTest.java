package com.test;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.utils.PossibleChar;

public class ValidCharTest
{
	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//String filePath = "images/test8.jpg";
		String filePath = "capture2.png";
		
		Mat image = Imgcodecs.imread(filePath);
		
		if(image.dataAddr()==0)
		{
			System.out.println("Couldn't open file " + filePath);
		}
		else
		{

			CharGUI gui = new CharGUI("Valid Char GUI", image);
			gui.init();
		}

	}
	
	
	private static class CharGUI extends AbstractGUI
	{

		private int minArea = 100;
		private int minWidth = 30;
		private int minHeight = 30;
		private int minAspectRatio = 25;  // * 100
		private int maxAspectRatio = 150;
		
		private int maxWidth = 70; 
		private int maxHeight = 100;
		
		
		Mat threshImage = new Mat();
		
		public CharGUI(String windowName, Mat image) 
		{
			super(windowName, image);
			
			Mat grayImage = new Mat();
			Mat blurImage = new Mat();
			
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(grayImage, blurImage, new Size(5,5), 0);
			Imgproc.adaptiveThreshold(blurImage, threshImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 19, 9);
			
		}

		@Override
		public Mat updateImage() 
		{
			Mat newImage = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0,0,0));
			
			Mat threshCopyImage = threshImage.clone();
			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			Imgproc.findContours(threshCopyImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			
			for(MatOfPoint mat : contours) 
			{
				PossibleChar possibleChar = new PossibleChar(mat.toList(), Imgproc.boundingRect(mat), Imgproc.contourArea(mat));
				
				if(possibleChar.boundingRect.area() > minArea &&
						possibleChar.boundingRect.width > minWidth &&
						possibleChar.boundingRect.height > minHeight &&
						possibleChar.boundingRect.height < maxHeight &&
						possibleChar.boundingRect.width < maxWidth &&
				        (minAspectRatio/100) < possibleChar.aspectRatio && 
				        possibleChar.aspectRatio < (maxAspectRatio/100)) 
				    {
						Imgproc.drawContours(newImage, contours, contours.indexOf(mat), new Scalar(255, 255, 255), 1);
				    }
			}
			
			return newImage;
			
		}

		@Override
		public void addSliders(JFrame frame) 
		{
			addSlider(frame, "Min Area", 10, 750, minArea, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					minArea = (int)source.getValue();
					updateView();	
					
				}
			});
			
			addSlider(frame, "Min Width", 1, 50, minWidth, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					minWidth = (int)source.getValue();
					updateView();	
					
				}
			});
			
			addSlider(frame, "Max Width", 1, 150, maxWidth, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					maxWidth = (int)source.getValue();
					updateView();	
					
				}
			});
			
			addSlider(frame, "Min Heigh", 1, 50, minHeight, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					minHeight = (int)source.getValue();
					updateView();	
					
				}
			});
			
			addSlider(frame, "Max Heigh", 1, 150, maxHeight, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					maxHeight = (int)source.getValue();
					updateView();	
					
				}
			});
			
			addSlider(frame, "Min aspect ratio", 10, 150, minAspectRatio, new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					minAspectRatio = (int)source.getValue();
					updateView();	
					
				}
			});

			addSlider(frame, "Max aspect ration ", 50, 400, maxAspectRatio, new ChangeListener() {
	
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JSlider source = (JSlider)e.getSource();
					maxAspectRatio = (int)source.getValue();
					updateView();	
					
				}
			});
			
		}
		
	}
}
