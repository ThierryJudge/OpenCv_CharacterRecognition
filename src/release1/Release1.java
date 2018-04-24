package release1;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.ml.KNearest;

import com.gpio.ServoController;
import com.main.Trainer;

public class Release1
{

	//static Client client;
	static GUI gui;
	static ServoController servoController;
	
	public static void main(String[] args) 
	{
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		servoController = new ServoController();
		
		KNearest knn = KNearest.create();
		Trainer.train("digits.png", knn);
		
		String fileName1 = "numberPlates/image1.PNG";
		String fileName2 = "numberPlates/image2.PNG";
		String fileName3 = "numberPlates/image3.PNG";
		String fileName4 = "numberPlates/image4.PNG";
		String fileName5 = "numberPlates/image5.PNG";
		
		ArrayList<Mat> images = new ArrayList<>();
		
		images.add(Imgcodecs.imread(fileName1));
		images.add(Imgcodecs.imread(fileName2));
		images.add(Imgcodecs.imread(fileName3));
		images.add(Imgcodecs.imread(fileName4));
		images.add(Imgcodecs.imread(fileName5));
		
		for(Mat image : images)
		{
			if(image.dataAddr() == 0)
			{
				System.out.println("Can't open file: " + (images.indexOf(image) - 1));
				return;
			}
		}
		
		/*
		//client = new Client(61234, "192.168.0.101", new ClientListener());
		client = new Client(61234, "localhost", new ClientListener());
		client.listen();
		*/
		
		
		Listener listener = new Listener();
		
		gui = new GUI("GUI", images, knn, listener);
		gui.init();
	}
	
	
	
	/*
	 * Used because the Main class cannot be used as "this" in the main function because it is static
	 */
	/*
	private static class ClientListener implements MessageReceivedListener
	{
		@Override
		public void messageReceived(String message) 
		{
			System.out.println(message);
			if(message.equals("YES"))
			{
				gui.setButtonGreen();
			}
			else if(message.equals("NO"))
			{
				gui.setButtonRed();
			}
		}
	}
	*/
	
	
	private static class Listener implements GuiListener
	{
		/*
		 * PLATES
		 * 123456
		 * 898631
		 * 457361
		 * 941729
		 */
		ArrayList<String> knownPlates = new ArrayList<>();

		public Listener() 
		{
			knownPlates.add("123456");
			knownPlates.add("898631");
			knownPlates.add("941729");
			
		}
		
		@Override
		public void newPlate(String s) 
		{
			
			s.toUpperCase();
			
			for(String str : knownPlates)
			{
				if(str.equals(s))
				{
					gui.setButtonGreen();
					servoController.openWithTimer();
					return;
				}
			}
			
			gui.setButtonRed();
			servoController.close();
			
			
			/*
			if(client.isConnected())
			{
				client.write(s);
			}
			*/
			
		}
		
	}

}
