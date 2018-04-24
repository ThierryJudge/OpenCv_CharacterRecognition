package com.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;

public abstract class AbstractGUI 
{
	JLabel imageView;
	JFrame frame;
	
	String windowName;
	Mat image;
	
	
	public AbstractGUI(String windowName, Mat image) 
	{
		super();
		this.windowName = windowName;
		this.image = image;
	}
	
	public void updateView() 
	{
		Mat newImage = updateImage();
		
		Image outputImage = toBufferedImage(newImage);
		imageView.setIcon(new ImageIcon(outputImage));
	}
	
	public abstract Mat updateImage();

	public void init() 
	{
		setSystemLookAndFeel();
		initGUI();
	}

	private void initGUI() 
	{
		frame = createJFrame(windowName);

		updateView();

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	
	private JFrame createJFrame(String windowName)
	{
		JFrame frame = new JFrame(windowName);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		
		addSliders(frame);
		
		setupImage(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	public abstract void addSliders(JFrame frame);
	
	private void setupImage(JFrame frame) 
	{
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		frame.add(imageScrollPane);
	}
	
	void addSlider(JFrame frame, String name, int min, int max, int initial, ChangeListener changeListener)
	{
		JLabel sliderLabel = new JLabel(name, JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JSlider levelSlider = new JSlider(JSlider.HORIZONTAL,min, max, initial);
		
		int tickSpacing = (max - min)/10;
		if(tickSpacing == 0)
		{
			tickSpacing = 1;
		}
		levelSlider.setMajorTickSpacing(tickSpacing);
		levelSlider.setMinorTickSpacing(1);
		levelSlider.setPaintTicks(true);
		levelSlider.setPaintLabels(true);
		levelSlider.addChangeListener(changeListener);

		frame.add(sliderLabel);
		frame.add(levelSlider);
	}
	
	private void setSystemLookAndFeel() 
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage toBufferedImage(Mat matrix)
	{
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);  
		return image;
	}

}
