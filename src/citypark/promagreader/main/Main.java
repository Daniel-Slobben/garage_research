package citypark.promagreader.main;

import citypark.promagreader.comm.*;
import citypark.promagreader.gui.*;
import citypark.promagreader.threading.*;
import gnu.io.*;

public class Main {
	private Comm comm;
	private CommSetting setting;
	private MainScreen mainscreen;
	private Inenuitrijden inenuitrijden;
	
	public Main() throws Exception {
		setting=new CommSetting("COM4", 9600, 
				SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE, SerialPort.FLOWCONTROL_NONE);
		comm=new Comm(setting);
      	//mainscreen=new MainScreen(comm.getOut());
      	inenuitrijden=new Inenuitrijden(comm.getOut());
		new ThreadManager(comm.getIn(), inenuitrijden, 250L);
	}
	
	
}
