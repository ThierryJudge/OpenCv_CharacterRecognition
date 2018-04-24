package com.gpio;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class GPIOController 
{
    
    GpioPinDigitalInput btn;
    
    GpioPinDigitalOutput pin0;
    GpioPinDigitalOutput pin1;
    
    GpioButtonListener buttonListener;
    
    public GPIOController(GpioButtonListener buttonListener) 
    {
		this.buttonListener = buttonListener;
	}
    
	public void start()
	{
		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

        btn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_DOWN);
        btn.setShutdownOptions(true);
        btn.addListener(
    			new GpioPinListenerDigital()
    			{
    				@Override
    				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
    				{
    					if(event.getState() == PinState.HIGH)
    					{
    						System.out.println("GPIO BUTTON CLICK");
    						buttonListener.gpioButtonClick();
    					}
    						
    				}

    			}
    		);
        pin0 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "MyLED", PinState.HIGH); 
        pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "MyLED", PinState.HIGH);
        
        pin0.setShutdownOptions(true, PinState.LOW);
        pin1.setShutdownOptions(true, PinState.LOW);
	}
	
	
	public void setLed0()
	{
		pin0.high();
	}
	
	public void resetLed0()
	{
		pin0.low();
	}
	
	public void setLed1()
	{
		pin1.high();
	}
	
	public void resetLed1()
	{
		pin1.low();
	}
	
}
