package com.imagecreation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collector.Characteristics;

import javax.swing.plaf.synth.SynthSeparatorUI;

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

import com.utils.ImageViewer;


import com.utils.Constants;
public class ImageCreator 
{

	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		System.out.println("Characters: " + Constants.CHARACTERS.length());

		
		int width = Constants.NUMBER_SIZE * (Constants.DIGITS_PER_ROW + 1) + Constants.SPACING * 4;
		//int hieght = Constants.NUMBER_SIZE * (Constants.NUMBER_OF_CHARS + 1) * Constants.NUMBER_OF_FONTS;
		int hieght = Constants.NUMBER_SIZE * (Constants.NUMBER_OF_LETTERS + 1) * Constants.NUMBER_OF_FONTS;
		Mat image = new Mat(hieght,width, CvType.CV_8UC1, new Scalar(255,255,255));
		
		
		draw(image);
		show(image, "Letters");
		save(image, "Letters.png");
		
	}
	
	private static void draw(Mat image)
	{
		for(int k = 0; k < Constants.NUMBER_OF_FONTS; k++)
		{
			//for(int i = 0; i < Constants.NUMBER_OF_CHARS; i++)
			for(int i = 0; i < Constants.NUMBER_OF_LETTERS; i++)
			{
				//System.out.println(String.valueOf(Constants.CHARACTERS.charAt(i)) + ": " + Constants.CHARACTERS.charAt(i));
				for(int j = 0; j < Constants.DIGITS_PER_ROW; j++)
				{
					//int rowIndex = k * Constants.NUMBER_OF_CHARS + (i+1);
					int rowIndex = k * Constants.NUMBER_OF_LETTERS + (i+1);
					/*
					Imgproc.putText(image,String.valueOf(Constants.CHARACTERS.charAt(i)), 
							new Point(j*Constants.NUMBER_SIZE + Constants.SPACING ,(rowIndex)*Constants.NUMBER_SIZE - Constants.SPACING ), 
							k, 2, new Scalar(0, 0, 0), 5);
							*/
					Imgproc.putText(image,String.valueOf(Constants.CAP_LETTERS.charAt(i)), 
							new Point(j*Constants.NUMBER_SIZE + Constants.SPACING ,(rowIndex)*Constants.NUMBER_SIZE - Constants.SPACING ), 
							k, 2, new Scalar(0, 0, 0), 5);
				}
			}
		}
	}

	
	private static Mat getSingleNumber(Mat image, int i, int j)
	{
		return image.submat(new Rect(j*Constants.NUMBER_SIZE,(i)*Constants.NUMBER_SIZE, Constants.NUMBER_SIZE, Constants.NUMBER_SIZE + Constants.SPACING * 2));
		
	}
	
	public static void show(Mat image, String name)
	{
		if(image.dataAddr() == 0)
		{
			System.out.println("Can't open file");
		}
		else
		{
			(new ImageViewer()).show(image, name, true);
		}
	}
	
	public static void save(Mat mat, String filePath)
	{
		boolean check = Imgcodecs.imwrite(filePath, mat);;
		System.out.println(check);
	}


}
