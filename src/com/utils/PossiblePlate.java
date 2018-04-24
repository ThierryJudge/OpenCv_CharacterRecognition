package com.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class PossiblePlate 
{
	private static final double MIN_PIXEL_AREA = 600;
	private static final int MIN_PIXEL_WIDTH = 2;
	private static final int MIN_PIXEL_HEIGHT = 5;
	private static final float MIN_ASPECT_RATIO = 3f;
	private static final float MAX_ASPECT_RATIO = 10f;
	
	
	public List<PossibleChar> chars = new ArrayList<>();
	Rect rect;
	float aspectRatio;
	String plate;
	
	public PossiblePlate(List<PossibleChar> chars)
	{
		this.chars = chars;
		this.rect = new Rect(chars.get(0).boundingRect.tl(), chars.get(chars.size()-1).boundingRect.br());
		this.aspectRatio = (float) rect.width / (float) rect.height;
	}
	
	public boolean isValid()
	{
		if (this.rect.area() > MIN_PIXEL_AREA &&
		        MIN_ASPECT_RATIO < this.aspectRatio && 
		        this.aspectRatio < MAX_ASPECT_RATIO) 
		    {
		        return true;
		    }
		    else 
		    {
		        return false;
		    }
		
	}
	
	public void drawRectAroundPlate(Mat image)
	{
		Imgproc.rectangle(image, this.rect.tl(), this.rect.br(), new Scalar(255, 0, 255),1, 8,0);
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}
	
	
	
	
}
