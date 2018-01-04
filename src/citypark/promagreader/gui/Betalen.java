package citypark.promagreader.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.sql.ResultSet;

import citypark.promagreader.main.Main;
import citypark.promagreader.threading.Out;
import citypark.promagreader.gui.*;
import citypark.client.*;

public class Betalen extends JFrame 
{
	private int rekNr;
	private JTextField textfield1;
	public JTextField statuscolor, melding;
	private JPasswordField pinField;
	public Out out;
	private JPanel pane;
	private JButton ok;
	private int bankpasnr;
	private int pasNr;
	public static double bedrag = 0.0;

public Betalen(int BankPasNr, int PasNr) 
{
	pasNr = PasNr;
	System.out.println("het lognr " + Inenuitrijden.getLogNr(pasNr));
	System.out.println("het pasnr " + pasNr);
	bankpasnr = BankPasNr;
	pane = new JPanel(new GridLayout(1, 2));
	ok = new JButton("ok");
	textfield1 = new JTextField("Voer de pincode van uw pas in"); 
	pinField = new JPasswordField(4);
	  
	textfield1.setHorizontalAlignment(JTextField.CENTER);
	pinField.setHorizontalAlignment(JPasswordField.CENTER);
	  
	textfield1.setEditable(false);
	melding = new JTextField();
	melding.setHorizontalAlignment(JTextField.CENTER);
	melding.setEditable(false);
	melding.setForeground(Color.RED);
	
	setLayout(new FlowLayout());
	
	pane.add(melding);
	pane.add(ok);
	
	
	
	 ok.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 try{
				 if(pinCheck(bankpasnr)==false)
				 {
					 melding.setText("Ongeldige pincode");
				 }
				 else
				 {
					 Inenuitrijden.setPinCheckDoneTrue();
				 }
				} catch (Exception b) {
					b.printStackTrace();
				}
		 }
	 });
	
	
	add(textfield1);
	add(pinField);
	add(pane);


	Container content = getContentPane();
	content.setLayout(new GridLayout(3, 1, 0, 0));
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int height = screenSize.height;
	int width = screenSize.width;
	setSize(width/2, height/2);
	setLocationRelativeTo(null);
	setTitle("Pincode check");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(500, 300);
	setVisible(true); 
	
	}

	public boolean getPinCheck()
	{
		return Inenuitrijden.getPinCheck();
	}
	
	@SuppressWarnings("deprecation")
	private boolean pinCheck(int bankPasNr) throws Exception 
	{
		System.out.println(Inenuitrijden.getLogNr(pasNr));
		setBedrag(Tarievering(Inenuitrijden.getLogNr(pasNr)));
		ResultSet rsRekNr;
		PreparedStatement prepstmtRekNr = Inenuitrijden.ConnectionBANK("SELECT Rekeningnr FROM rekening WHERE Pasnr ="+bankpasnr+"");
		rsRekNr = prepstmtRekNr.executeQuery();
		while(rsRekNr.next())
		{
			rekNr = rsRekNr.getInt(1);
		}
		prepstmtRekNr.close();
	
		boolean returnvalue;
		if(Client.transferpin(rekNr, 100000, bedrag, Integer.parseInt(pinField.getText()))==true) 
		{
			returnvalue = true;
		}
		else
		{
			returnvalue = false;
		}
		
		return returnvalue;
	}
	
	   public static double Tarievering(int LogNr) {
	       double value = 0;
	       String logNr = Integer.toString(LogNr);
	       try {
	       // Construct data
	       System.out.println("nice " + logNr);
	       String data = URLEncoder.encode("lognr", "UTF-8") + "=" + URLEncoder.encode(logNr, "UTF-8");

	       // Send data
	       URL url = new URL("http://citypark.haijeploeg.nl/tarievering.php");            //
	       URLConnection conn = url.openConnection();
	       conn.setDoOutput(true);
	       OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	       wr.write(data);
	       wr.flush();
	       System.out.println(data);
	       System.out.println("bedrag " + value);
	       // Get the response
	       BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	       String line;
	       while ((line = rd.readLine()) != null) {
	            value = Double.parseDouble(line);
	       }
	       wr.close();
	       rd.close();
	       
	       
	   } 
	       catch (Exception e) 
	   {
	   }
	       return value;
	   } 
	   
	   
	  public static void setBedrag(double Bedrag)
	  {
		  bedrag = Bedrag;
	  }
	
	  public static double getBedrag()
	  {
		  return bedrag;
	  }
	  
	  public static void clearBedrag()
	  {
		  bedrag = 0.0;
	  }
	  
	

}
