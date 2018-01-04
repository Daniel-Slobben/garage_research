package citypark.promagreader.threading;

import java.awt.Color;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import citypark.promagreader.decoder.*;
import citypark.promagreader.gui.*;

public class ThreadManager extends Thread {
	private In in;
	private MainScreen main;
	private Inenuitrijden inenuitrijden;
	private Betalen bankPasScan;
	private long pollinterval;
	private Decoder dec;
	public static boolean Threadrunning = false;
	public Thread thisThread;
	
	public ThreadManager(In in, Inenuitrijden inenuitrijden, long pollinterval) 
	{
		this.in=in;
		this.inenuitrijden=inenuitrijden;
		this.pollinterval=pollinterval;
		dec=new Decoder();
		start();
	}

	@Override
	public void run() {
		thisThread = Thread.currentThread();
		while (true) {
			if(Threadrunning == false)
			{
				try
				{
				synchronized(thisThread){
			    sleep(1000);
				}
				}
				catch (Exception ex) 
				{
					
					ex.printStackTrace();
					Thread.currentThread().interrupt();
				}
				Threadrunning = true;
				try {
					String s=in.getBuffer();
					if(s!=null) 
					{					
						inenuitrijden.setText(s);
						int status = inenuitrijden.setID(dec.decodeLastValue(inenuitrijden.getText()));
						setStatus(status);	
						synchronized(thisThread){
							
						    thisThread.wait(3000);
						    
						}
					}

					inenuitrijden.statuscolor.setBackground(inenuitrijden.originalColor); 
					inenuitrijden.melding.setText(""); 
					synchronized(thisThread){
					    thisThread.wait(3000);
					}
					inenuitrijden.dontInsertDuplicate = true;
					inenuitrijden.dontSubtractMultipleTimes = true;
					inenuitrijden.running = false;
					
				} 
				catch (Exception ex) 
				{
					
					ex.printStackTrace();
					Thread.currentThread().interrupt();
				}
					Threadrunning = false;
			}
			else
			{}	
			
		}
	
	}
	
	public void setStatus(int status)
	{
		if(inenuitrijden.statuscolor.getBackground()==inenuitrijden.originalColor)
		{
		if (status==1) 
		{
			inenuitrijden.statuscolor.setBackground(Color.GREEN);
			inenuitrijden.melding.setText("U kunt verder rijden");
			
			try 
	    	 {
	    		 inenuitrijden.out.beep();
	    	 }
	    	 catch(Exception ex) 
	    	 {
				ex.printStackTrace();
			 }

		} 
		else if(status==2)
		{
			inenuitrijden.statuscolor.setBackground(Color.RED);
			inenuitrijden.melding.setText("Uw pas is geblokkeerd");
			try 
	    	 {
	    		 inenuitrijden.out.beeps();
	    	 }
	    	 catch(Exception ex) 
	    	 {
				ex.printStackTrace();
			 }
		}
		else if(status==3)
		{
			inenuitrijden.statuscolor.setBackground(Color.RED);
			inenuitrijden.melding.setText("Ongeldige pas");
			try 
	    	 {
	    		 inenuitrijden.out.beeps();
	    	 }
	    	 catch(Exception ex) 
	    	 {
				ex.printStackTrace();
			 }
		}
		else if(status==4)
		{
			inenuitrijden.setBankPasScanFalse();
			inenuitrijden.statuscolor.setBackground(Color.BLUE);
			inenuitrijden.melding.setText("Peace bitches");
			inenuitrijden.canInsert = true;
			try 
	    	 {
	    		 inenuitrijden.out.beep();
	    	 }
	    	 catch(Exception ex) 
	    	 {
				ex.printStackTrace();
			 }
		}
		
		else if(status==5)
		{
			inenuitrijden.statuscolor.setBackground(Color.YELLOW);
			inenuitrijden.melding.setText("Houdt u bankpas voor de scanner!");
			inenuitrijden.setBankPasScanTrue();
			try 
	    	 {
	    	 }
	    	 catch(Exception ex) 
	    	 {
				ex.printStackTrace();
			 }
		}
		}
		else
		{
			try 
			{
			    Thread.sleep(2000);               
			} 
			catch(InterruptedException ex) 
			{
			    Thread.currentThread().interrupt();
			}
			inenuitrijden.statuscolor.setBackground(inenuitrijden.originalColor); 
			inenuitrijden.melding.setText(""); 
		}
	}
	
	
}
