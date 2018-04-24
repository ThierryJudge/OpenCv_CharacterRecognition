package release2;

import org.opencv.core.Core;

public class Release2 
{


	public static void main(String[] args) 
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		MainController controller = new MainController();
		controller.run();
	}

}
