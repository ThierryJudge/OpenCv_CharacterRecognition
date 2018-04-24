package release1;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.p10.lib.Client;
import com.p10.lib.MessageReceivedListener;

public class clientTest {

	public static void main(String[] args) {
		ClientUser user = new ClientUser();
		user.run();

	}
	
	public static  class ClientUser implements MessageReceivedListener 
	{
		Client client;
		public void run()
		{
			System.out.println("Client V02 is starting");
			client = new Client(61234, "localhost",  this);
			
			
			client.listen();
			
			while(true)
			{
				System.out.println("\n \nEnter a number");
				Scanner sc = new Scanner(System.in);
				int i = 0;
				
				try
			    {
					i = sc.nextInt();
			    }
			    catch (InputMismatchException exception)
			    {
			        System.out.println("Integers only, please.");
			    }
				
				client.write(Integer.toString(i));
				
			}
			
		}
		
		@Override
		public void messageReceived(String message) 
		{
			System.out.println("ClientMain: " + message);
			
		}
	}

}
