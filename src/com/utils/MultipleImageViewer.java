package com.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;


public class MultipleImageViewer 
{
	private JLabel imageView;
	
	public MultipleImageViewer(String windowName, ArrayList<AnalyseReturn> images) 
	{
		JFrame frame = createJFrame(windowName, images);
		
		updateView(images.get(0).mat);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	
	public void updateView(Mat newImage) 
	{	
		Image outputImage = toBufferedImage(newImage);
		imageView.setIcon(new ImageIcon(outputImage));
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

	private JFrame createJFrame(String windowName, ArrayList<AnalyseReturn> images) {
		JFrame frame = new JFrame(windowName);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		setUpImageButtons(frame, images);
		setupImage(frame);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return frame;
	}
	
	private void setUpImageButtons(JFrame frame, ArrayList<AnalyseReturn> images)
	{
		for(AnalyseReturn image : images)
		{
			JButton button = new JButton(image.string);
			button.addActionListener(new ActionListener() 
			{
				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					System.out.println("Image " + (images.indexOf(image) + 1));
					updateView(image.mat);
				}
			});
			
			button.setAlignmentX(Component.TOP_ALIGNMENT);
			frame.add(button);
		}
	}
	
	private void setupImage(JFrame frame) 
	{
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		frame.add(imageScrollPane);
	}
}
