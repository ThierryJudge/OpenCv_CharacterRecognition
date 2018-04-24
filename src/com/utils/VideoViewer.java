package com.utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;



public class VideoViewer
{
	
	private JLabel imageView;
	JFrame frame;
	GuiButtonListener buttonListener;
	
	
	public VideoViewer(GuiButtonListener listener) {
		this.buttonListener = listener;
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
		frame = createJFrame(windowName, image.rows(), image.cols(), scrollable);
		
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			
	}
	
	public void update(Mat image)
	{
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
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
		
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		imageView = new JLabel();
		
	
		
		
		if(buttonListener != null)
		{
			setUpButton(frame);
		}
		frame.add(imageView);
		
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void setUpButton(JFrame frame)
	{
		JButton button = new JButton("Capture");
		button.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				buttonListener.guiButtonClicked();
			}
		});
		button.setAlignmentX(Component.TOP_ALIGNMENT);
		frame.add(button);
	}
}
