package com.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;

public class ImageViewer 
{
	
	private JLabel imageView;
	
	
	
	public static void showImage(Mat image, String name, boolean scrollable)
	{
		if(image.dataAddr() == 0)
		{
			System.out.println("Can't open file");
		}
		else
		{
			(new ImageViewer()).show(image, name, scrollable);
		}
	}
	
	public void show(Mat image)
	{
		show(image, "");
	}
	
	public void show(Mat image, String windowName)
	{
		show(image, windowName, false);
	}
	
	public void show(Mat image, String windowName, boolean scrollable)
	{
		setSystemLookAndFeel();
		
		JFrame frame = createJFrame(windowName, image.rows(), image.cols(), scrollable);
		
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		
		
		
	}

	private Image toBufferedImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if(matrix.channels() > 1)
		{
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer);
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}

	private JFrame createJFrame(String windowName, int w, int h, boolean scrollable) {
		JFrame frame = new JFrame(windowName);
		imageView = new JLabel();
		
		if(scrollable)
		{
			final JScrollPane imageScrollPane = new JScrollPane(imageView);
			imageScrollPane.setPreferredSize(new Dimension(640, 480));
			frame.add(imageScrollPane, BorderLayout.CENTER);
		}
		else
		{
			frame.add(imageView);
		}
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return frame;
	}

	private void setSystemLookAndFeel() {
		// TODO Auto-generated method stub
		
	}
	

}
