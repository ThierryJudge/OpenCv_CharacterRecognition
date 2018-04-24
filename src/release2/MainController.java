package release2;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.ml.KNearest;
import org.opencv.videoio.VideoCapture;

import com.gpio.GPIOController;
import com.gpio.GpioButtonListener;
import com.gpio.ServoController;
import com.imagecreation.ImageCreator;
import com.main.ImageAnalyser;
import com.main.Trainer;
import com.p10.lib.Client;
import com.p10.lib.MessageReceivedListener;
import com.utils.AnalyseReturn;
import com.utils.Constants;
import com.utils.GuiButtonListener;
import com.utils.ImageViewer;
import com.utils.VideoViewer;




public class MainController implements GuiButtonListener, GpioButtonListener, MessageReceivedListener
{
	
	
	
	private final boolean RUN_ON_PI = true;
	
	//VideoViewer videoViewer = new VideoViewer(this);
	Release2Gui videoViewer = new Release2Gui(this);
	VideoCapture camera;
	KNearest numberKnn;
	Client client;
	ServoController servoController;
	GPIOController gpioController;
	
	int cameraNumber = 0;
	
	
	WebAppSimulator simulator = new WebAppSimulator();


	public void run() 
	{

		if(RUN_ON_PI)
		{
			servoController = new ServoController();
			gpioController = new GPIOController(this);
			gpioController.start();
			cameraNumber = 0;
		}
		
		
		numberKnn = KNearest.create();
		Trainer.train("digits.png", numberKnn);
		
		
		client = new Client(61234, "192.168.0.101", this);
		client.listen();
		
		camera = new VideoCapture(0);
		 
		if(!camera.isOpened())
		{
            System.out.println("Error camera could not be opened, check camera number");
            return;
        }
		
		Mat frame = new Mat();
		camera.read(frame); 
        videoViewer.show(frame, "webcam", false);
        
        while(true)
        {        
            if (camera.read(frame))
            {
            	videoViewer.update(frame);
            }
        } 
	}

	private void buttonClicked()
	{
		if(RUN_ON_PI)
		{
			gpioController.resetLed0();
			gpioController.resetLed1();
		}
		
		Mat frame = new Mat();
		camera.read(frame); 
		ImageCreator.save(frame, "capture2.png");
		ImageAnalyser imageAnalyser = new ImageAnalyser(frame.clone(), numberKnn);
		AnalyseReturn ret = imageAnalyser.analyse(false);
		System.out.println("Plate: " + ret.string);
		
		videoViewer.newCapture(ret.mat, imageAnalyser.getImages());
		
		if(ret.string.length() != 6)
		{
			gpioController.setLed0(); //invalid 
			return;
		}
		
		if(client.isConnected())
		{
			client.write(ret.string);
		}
		
	}

	@Override
	public void guiButtonClicked() 
	{
		buttonClicked();
	}
	
	@Override
	public void gpioButtonClick() 
	{
		buttonClicked();
	}
	
	private class WebAppSimulator
	{
		/*
		 * PLATES
		 * 123456
		 * 898631
		 * 457361
		 * 941729
		 */
		ArrayList<String> knownPlates = new ArrayList<>();

		public WebAppSimulator() 
		{
			knownPlates.add("123456");
			knownPlates.add("898631");
			knownPlates.add("941729");
		}
		
		
		public void newPlate(String s) 
		{
			
			s.toUpperCase();
			
			for(String str : knownPlates)
			{
				if(str.equals(s))
				{
					servoController.openWithTimer();
					return;
				}
			}
			
			servoController.close();
			gpioController.setLed1(); //wrong plate
			
			
	
			
		}
		
	}
	
	
	@Override
	public void messageReceived(String message) 
	{
		if(message.equals("YES"))
		{
			servoController.openWithTimer();
		}
		else if(message.equals("NO"))
		{
			servoController.close();
			gpioController.setLed1(); //wrong plate
		}
		
	}

	
}
