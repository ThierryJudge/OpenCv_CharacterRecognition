package release1;

import java.awt.Color;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.ml.KNearest;

import com.main.ImageAnalyser;
import com.utils.AnalyseReturn;

public class GUI 
{
	private JLabel imageView;
	private JFrame frame;
	
	private String windowName;
	private ArrayList<Mat> images;
	private Mat currentImage;
	
	private KNearest knn;
	
	private JButton returnButton;
	
	private GuiListener listener;
	
	
	public GUI(String windowName, ArrayList<Mat> images, KNearest knn, GuiListener listener) 
	{
		super();
		this.windowName = windowName;
		this.images = images;
		this.currentImage = images.get(0);
		this.knn = knn;
		this.listener = listener;
	}
	
	public void updateView(Mat newImage) 
	{	
		Image outputImage = toBufferedImage(newImage);
		imageView.setIcon(new ImageIcon(outputImage));
	}
	
	public void init() 
	{
		setSystemLookAndFeel();
		frame = createJFrame(windowName);

		updateView(images.get(0));

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private JFrame createJFrame(String windowName)
	{
		JFrame frame = new JFrame(windowName);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		
		setUpImageButtons(frame);
		setupImage(frame);
		setUpSendButton(frame);
		setUpReturnButton(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private void setUpImageButtons(JFrame frame)
	{
		for(Mat image : images)
		{
			JButton button = new JButton("Image " + (images.indexOf(image) + 1));
			button.addActionListener(new ActionListener() 
			{
				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					System.out.println("Image " + (images.indexOf(image) + 1));
					currentImage = image;
					updateView(currentImage);
					resetButton();
					
				}
			});
			
			button.setAlignmentX(Component.TOP_ALIGNMENT);
			frame.add(button);
		}
	}
	
	private void setUpSendButton(JFrame frame)
	{
		JButton button = new JButton("Send");
		button.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				ImageAnalyser imageAnalyser = new ImageAnalyser(currentImage.clone(), knn);
				AnalyseReturn ret = imageAnalyser.analyse();
				System.out.println(ret.string);
				updateView(ret.mat);
				listener.newPlate(ret.string);
			}
		});
		
		frame.add(button);
	}
	
	private void setUpReturnButton(JFrame frame)
	{
		returnButton = new JButton("       ");
		returnButton.setContentAreaFilled(false);
		returnButton.setOpaque(true);
		returnButton.setBorderPainted(false);
		frame.add(returnButton);
	}
	
	public void setButtonRed()
	{
		returnButton.setBackground(Color.red);
	}
	
	public void setButtonGreen()
	{
		returnButton.setBackground(Color.GREEN);
	}
	
	private void resetButton()
	{
		returnButton.setBackground(null);
	}
	
	private void setupImage(JFrame frame) 
	{
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(640, 480));
		frame.add(imageScrollPane);
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
