package com.gpio;

import java.io.IOException;

public class ServoController 
{
	
	static final int OPEN_TIME = 5000; // 5 seconds
	
	static final int CLOSE_POSITION = 145;
	static final int OPEN_POSITION = 85;
	boolean isOpen;
	
	CloseThread closeThread;
	
	
	Runtime runtime;
	public ServoController() 
	{
		runtime = Runtime.getRuntime();
		try {
			runtime.exec("gpio mode 1 pwm");
			runtime.exec("gpio pwm-ms");
			runtime.exec("gpio pwmc 192");
			runtime.exec("gpio pwmr 2000");
			
			System.out.println("Center");
			runtime.exec("gpio pwm 1 " + CLOSE_POSITION); //center
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		isOpen = false;
	}
	
	
	public boolean open()
	{
		if(!isOpen)
		{
			System.out.println("Right");
			try {
				runtime.exec("gpio pwm 1 " + OPEN_POSITION);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			isOpen = true;
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	public boolean close()
	{
		if(isOpen)
		{	
			System.out.println("Center");
			try {
				runtime.exec("gpio pwm 1 " + CLOSE_POSITION);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			isOpen = false;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}
	
	public boolean openWithTimer()
	{
		if(!isOpen)
		{
			try {
				runtime.exec("gpio pwm 1 50");//right
			} catch (IOException e) {
				e.printStackTrace();
			} 
			isOpen = true;
			closeThread = new CloseThread();
			closeThread.start();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private class CloseThread extends Thread
	{
		@Override
		public void run() 
		{
			super.run();
			try {
				Thread.sleep(OPEN_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			close();
			
		}
	}
	

}
