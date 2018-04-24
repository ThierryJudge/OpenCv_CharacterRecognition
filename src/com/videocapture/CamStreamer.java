package com.videocapture;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.ml.KNearest;
import org.opencv.videoio.VideoCapture;

import com.main.ImageAnalyser;
import com.main.Trainer;
import com.utils.AnalyseReturn;
import com.utils.GuiButtonListener;
import com.utils.ImageViewer;
import com.utils.VideoViewer;



public class CamStreamer {

	static Listener listener = new Listener();
	static VideoViewer imageViewer = new VideoViewer(listener);
	static VideoCapture camera;
	static KNearest knn;

	
	public static void main (String args[])
	{
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		knn = KNearest.create();
		Trainer.train("digits.png", knn);
		
		camera = new VideoCapture(0);

        Mat frame = new Mat();
        camera.read(frame); 

        if(!camera.isOpened()){
            System.out.println("Error");
            return;
        }
        imageViewer.show(frame, "webcam", false);
        while(true){        

            if (camera.read(frame)){
            	imageViewer.update(frame);
            }
        } 
        
	}
	
	public static class Listener implements GuiButtonListener
	{
		@Override
		public void guiButtonClicked() 
		{
			  Mat frame = new Mat();
			  camera.read(frame); 
			  ImageAnalyser imageAnalyser = new ImageAnalyser(frame.clone(), knn);
			  AnalyseReturn ret = imageAnalyser.analyse();
			  System.out.println(ret.string);
			  ImageViewer.showImage(ret.mat, "Capture", false);
			  
		
		}
	}

}
