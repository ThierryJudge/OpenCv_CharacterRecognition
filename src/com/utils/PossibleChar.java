package com.utils;

import java.util.List;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class PossibleChar 
{
	private static final double MIN_PIXEL_AREA = 100;
	private static final int MIN_PIXEL_WIDTH = 2;
	private static final int MIN_PIXEL_HEIGHT = 5;
	private static final float MIN_ASPECT_RATIO = 0.25f;
	private static final float MAX_ASPECT_RATIO = 1.5f;
	private static final int MAX_WIDTH = 70; 
	private static final int MAX_HEIGHT = 100;
	
	
	
	private static int MAX_DISTANCE_BETWEEN_CHARS = 300;
	public List<Point> points;
	public Rect boundingRect;
	public double area;
	public float aspectRatio;
	public float cX, cY;
	
	public PossibleChar(List<Point> points, Rect boundingRect, double area) {
		super();
		this.points = points;
		this.boundingRect = boundingRect;
		this.area = area;
		this.aspectRatio = (float) boundingRect.width / (float) boundingRect.height;
		
		cX = boundingRect.x + boundingRect.width / 2;
		cY = boundingRect.y + boundingRect.height / 2;
	}

	
	public boolean isContourValid()
	{
		if (this.boundingRect.area() > MIN_PIXEL_AREA &&
		    	this.boundingRect.width > MIN_PIXEL_WIDTH && 
		    	this.boundingRect.height > MIN_PIXEL_HEIGHT &&
				this.boundingRect.height < MAX_HEIGHT &&
				this.boundingRect.width < MAX_WIDTH &&
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

	public static boolean isContourValid(PossibleChar contourWithData) 
	{
	    if (contourWithData.boundingRect.area() > MIN_PIXEL_AREA &&
	    	contourWithData.boundingRect.width > MIN_PIXEL_WIDTH && 
	    	contourWithData.boundingRect.height > MIN_PIXEL_HEIGHT &&
	        MIN_ASPECT_RATIO < contourWithData.aspectRatio && 
	        contourWithData.aspectRatio < MAX_ASPECT_RATIO) 
	    {
	        return true;
	    }
	    else 
	    {
	        return false;
	    }
	}


	public boolean matches(PossibleChar contourWithData)
	{
		if(//inRange(contourWithData.area, this.area, 0.75) 
				inRange(contourWithData.boundingRect.height, this.boundingRect.height, 0.3)
			&& inRange(contourWithData.boundingRect.width, this.boundingRect.width, 0.5))
		{
			return true;
		}
		return false;
	}
	
	public boolean inRange(double area2, double area3, double d)
	{
		return (area2 < area3 * (1 + d) && area2 > area3 * (1 - d));
	}

	public boolean inRange(int area2, int area3, double d)
	{
		return inRange((double)area2, (double) area3, d);
	}

	public boolean intersects(PossibleChar contourWithData)
	{
		return this.cY > contourWithData.boundingRect.y 
				&& this.cY < (contourWithData.boundingRect.y + contourWithData.boundingRect.height);
	}

	public boolean isNear(PossibleChar contourWithData)
	{
		return Math.abs(this.boundingRect.x - contourWithData.boundingRect.x) < MAX_DISTANCE_BETWEEN_CHARS;
	}
}
