package release2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
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
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.utils.GuiButtonListener;

public class Release2Gui 
{
	private JLabel imageView;
	private JLabel newImageView;
	
	private ArrayList<JLabel> imageLabels;
	
	private ArrayList<Mat> images;
	JFrame frame;
	GuiButtonListener buttonListener;
	
	
	public Release2Gui(GuiButtonListener listener) {
		this.buttonListener = listener;
		
		images = new ArrayList<>();
		imageLabels = new ArrayList<>();
		
		for(int i = 0; i < 6; i++)
		{
			images.add(Imgcodecs.imread("capture.png"));
			imageLabels.add(new JLabel());
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
		frame = createJFrame(windowName, image.rows(), image.cols(), scrollable);
		
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void update(Mat image)
	{
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
	}
	
	public void newCapture(Mat image, ArrayList<Mat> mats)
	{
		Image loadedImage = toBufferedImage(image);
		newImageView.setIcon(new ImageIcon(loadedImage));
		int i = 0;
		for(Mat mat : mats)
		{
			Imgproc.resize(mat,mat, new Size(mat.width() * 0.75, mat.height() * 0.75));
			System.out.println(mat.size());
			loadedImage = toBufferedImage(mat);
			imageLabels.get(i).setIcon(new ImageIcon(loadedImage));
			i++;
		}
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
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(new GridBagLayout());
		
		
		if(buttonListener != null)
		{
			JButton but = getButton();
			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(but, gbc);
			gbc.weighty = 0.1;
		}
		
		imageView = new JLabel();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;
		gbc.weighty = 1;
		panel.add(imageView, gbc);
		
		gbc.anchor = 10;
		
		newImageView = new JLabel();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.gridy = 2;
		panel.add(newImageView, gbc);
		
		
		for(JLabel label : imageLabels) 
		{
			int x, y;
			int i = imageLabels.indexOf(label); 
			x = i % 2;
			y = (i - x) / 2;
			gbc.gridx = x + 1;
			gbc.gridy = y + 1;
			panel.add(label, gbc);
				
		}
			
		
		frame.add(panel);
		
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
	
	private JButton getButton()
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
		return button;
	}
}
