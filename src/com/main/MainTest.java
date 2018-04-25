package com.main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;

import com.utils.Constants;
import com.utils.ImageViewer;

public class MainTest 
{
	
	final static int K = 1;
	
	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		KNearest numberKnn;
		
		numberKnn = KNearest.create();
		Trainer.train("digits.png", numberKnn, Constants.NUMBER_OF_DIGITS, Constants.NUMBERS);
		
		
		ImageAnalyser imageAnalyser = new ImageAnalyser("capture.png", numberKnn);
		imageAnalyser.analyse(true);
	}
}

