package com.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Utils 
{

	public static String getTimeString(long time)
	{
		long sec = time / 1000;
		int min = (int) sec / 60;
		int secLeft = (int) sec % 60;
		int mili = (int) time % 1000;
		
		
		return min + " minutes, " + secLeft + " seconds, " + mili + " miliseconds";
	}
	
	public static void sortByX(ArrayList<PossibleChar> possibleChars)
	{
		Collections.sort(possibleChars, new Comparator<PossibleChar>() 
		{

			@Override
			public int compare(PossibleChar o1, PossibleChar o2) 
			{
				return Double.compare(o1.boundingRect.x, o2.boundingRect.x);
			}
		});
	}
	
	public static void sortByY(ArrayList<PossibleChar> possibleChars)
	{
		Collections.sort(possibleChars, new Comparator<PossibleChar>() 
		{

			@Override
			public int compare(PossibleChar o1, PossibleChar o2) 
			{
				return Double.compare(o1.boundingRect.y, o2.boundingRect.y);
			}
		});
	}
}
