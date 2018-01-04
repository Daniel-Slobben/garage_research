package citypark.promagreader.gui;


import java.util.concurrent.locks.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import citypark.promagreader.threading.*;

public class Inenuitrijden extends JFrame implements ActionListener
{
	private boolean insert = true, update = true;
	public volatile boolean pinPasCheck = false;
	private boolean needBankPas = false;
	public boolean bankPasScan = false;
	public static boolean pinCheckDone = false;
	public Color originalColor;
	private java.util.ArrayList passenRowData, pasTypeData, pasRFID, typePas;
	public java.util.ArrayList pasNrs, blockedOrNotAbo1, blockedOrNotAbo2, blockedOrNotBez1, blockedOrNotBez2;
	public static java.util.ArrayList LogNrs;
	private JButton beep, beeps;
	private JTextField id, textfield1;
	public JTextField statuscolor, melding;
	private JTextArea area;
	public Out out;
	private int newMaxLogNr;
	public static int logNr;
	public int AboPasnr1, BezPasnr1, AdhocPasnr1, BankPasNr, AboPasnr2, BezPasnr2, AdhocPasnr2, BankPasPasnr,  Pasnr, AdhocPasnr, currentPasNr;
	public String AboPastype, BezPastype, AdhocPastype;
	public static boolean dontInsertDuplicate = true;
	public static boolean canInsert = true;
	public static boolean dontSubtractMultipleTimes = true;
	private String tijdsTegoed = "17:00:00", newTijdsTegoed;
	public final Lock lock = new ReentrantLock();
	public static boolean running = true;
		 
		 
		  public Inenuitrijden(Out out) 
		  {
			  LogNrs = new java.util.ArrayList(); 
			  canInsert = true;
			  pasNrs = new java.util.ArrayList();  
			  textfield1 = new JTextField("Houdt u pas voor de scanner en wacht op de pieptoon");
			  textfield1.setHorizontalAlignment(JTextField.CENTER);
			  statuscolor = new JTextField();
			  originalColor = statuscolor.getBackground();

			  melding = new JTextField();
			  melding.setHorizontalAlignment(JTextField.CENTER);
		
			  area=new JTextArea(30, 80);
			  id=new JTextField(25);
			  id.setEditable(false);

			  textfield1.setEditable(false); 
			  melding.setEditable(false);
		 
			  setLayout(new FlowLayout());
			  add(textfield1);
			  add(statuscolor);
			  add(melding);
		 
		
			  Container content = getContentPane();
			  content.setLayout(new GridLayout(3, 1, 0, 0));
			  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			  int height = screenSize.height;
			  int width = screenSize.width;
			  setSize(width/2, height/2);
			  setLocationRelativeTo(null);
			  setTitle("RFIDScanner");
			  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			  setSize(500, 300);
			  setVisible(true);
			  
			  this.out=out;  
	     
		  }
		  
		  
		  @Override
			public void actionPerformed(ActionEvent e) 
		    {
				try 
				{
					if (e.getSource()==beep) out.beep();
					if (e.getSource()==beeps) out.beeps();

				} 
				catch(Exception ex) 
				{
					ex.printStackTrace();
				}
		    }
		  
		  
		  
		  
		    public void setText(String s) 
		    {
				area.append(s);
			}
			
			public String getText() 
			{
				return area.getText();
			}
			
			public int setID(String s) 
			{
				if (DatabaseCheck(s)==1) 
				{
					return 1;
				} 
				else if (DatabaseCheck(s)==2) 
				{
					return 2;
				}
				else if (DatabaseCheck(s)==3)
				{
					return 3;
				}
				else if (DatabaseCheck(s)==4)
				{						
					System.out.println("Aanwezig in parkeergarage: " + pasNrs);
					pasNrs.remove(new Integer(getPasNr()));
					System.out.println("Aanwezig in parkeergarage: " + pasNrs);
					dontInsertDuplicate = true;
					setPinCheckDoneFalse();
					setBankPasScanFalse();
					clearBankPasNr();
					return 4;
				}
				else if (DatabaseCheck(s)==5)
				{
					return 5;
				}
				else
				{
					return 3;
				}


				
			}
			
			public void setPasNr(int pasNr)
			{
				Pasnr = pasNr;
			}
			
			public int getPasNr()
			{
				return Pasnr;
			}
			
			public void setBankPasScanTrue()
			{
				bankPasScan = true;
			}
			
			public void setBankPasScanFalse()
			{
				bankPasScan = false;
			}
			
			public boolean getBankPasScan()
			{
				return bankPasScan;
			}
			
			public void setAdhocPasNr(int pasNr)
			{
				AdhocPasnr = pasNr;
			}
			
			public int getAdhocPasNr()
			{
				return AdhocPasnr;
			}
			
			public void setBankPasNr(int BankPasPasnr)
			{
				BankPasNr = BankPasPasnr;
			}
			
			public int getBankPasNr()
			{
				return BankPasNr;
			}
			
			public void clearBankPasNr()
			{
				BankPasNr = 0;
			}
			
			public static void setPinCheckDoneTrue()
			{
				pinCheckDone = true;
			}
			
			public static void setPinCheckDoneFalse()
			{
				pinCheckDone = false;
			}
			
			public static boolean getPinCheck()
			{
				return pinCheckDone;
			}
			
			public static void setLogNr(int LogNr, int PasNr)
			{
				LogNrs.add(LogNr);
				LogNrs.add(PasNr);
				System.out.println("De lognummers" + LogNrs);
			}
			
			public static int getLogNr(int PasNr)
			{
				int logNrIndex = (LogNrs.indexOf(PasNr) - 1);
				logNr = (int)LogNrs.get(logNrIndex);
				return logNr;
			}
			
			public static void clearLogNr(int PasNr)
			{
				LogNrs.remove((LogNrs.indexOf(PasNr) - 1));
				LogNrs.remove(LogNrs.indexOf(PasNr));
				
			}
			
			public int DatabaseCheck(Object RFIDnr)           													//check of de pashouder de parkeergarage mag inrijden
			{
				int returnvalue = 3;
				passenRowData = new java.util.ArrayList();
				passenRowData = new java.util.ArrayList();
				pasTypeData = new java.util.ArrayList();
				pasTypeData = new java.util.ArrayList();
				pasRFID = new java.util.ArrayList();
				typePas = new java.util.ArrayList();
				blockedOrNotAbo1 = new java.util.ArrayList();
				blockedOrNotAbo2 = new java.util.ArrayList();
				blockedOrNotBez1 = new java.util.ArrayList();
				blockedOrNotBez2 = new java.util.ArrayList();
				//loggingsNrs = new java.util.ArrayList();
				try {
					if(getBankPasScan()==true)
					{
						PreparedStatement prepstmt1 = ConnectionBANK("SELECT RFID FROM bankpassen");
				 		ResultSet rs1;
				 		rs1 = prepstmt1.executeQuery();
				 		while(rs1.next())
				 		{
			                passenRowData.add(rs1.getString(1));
			                pasTypeData.add("Bankpas");
			            }
					}
					else
					{
					PreparedStatement prepstmt1 = ConnectionCP("SELECT RFIDnr, Pastype FROM passen");
					
			        
					ResultSet rs1;
		 			rs1 = prepstmt1.executeQuery();
		 			
		    		
			 		
			 		while(rs1.next())
			 		{
		                passenRowData.add(rs1.getString(1));
		                pasTypeData.add(rs1.getString(2));
		            }

			 			prepstmt1.close();
					}
		    			
	    	    }
				catch (Exception se) 
				{
					se.printStackTrace();
				}	
				
				if(passenRowData.contains(RFIDnr)) // kijkt of het RFIDnr in de database van CityPark is geregistreerd
				{
					try
					{
						PreparedStatement prepstmt3;
						String RFIDnrString = RFIDnr.toString();
						if(getBankPasScan()==true)
						{
							prepstmt3 = ConnectionBANK("SELECT RFID, Pasnr FROM bankpassen WHERE RFID =?");
							prepstmt3.setString(1, RFIDnrString);
						}
						else
						{
							prepstmt3 = ConnectionCP("SELECT RFIDnr, Pasnr, Pastype FROM passen WHERE RFIDnr =?");
							prepstmt3.setString(1, RFIDnrString);
						}
						ResultSet rs3;
						rs3 = prepstmt3.executeQuery();
			 		
						while(rs3.next())
						{
							if(getBankPasScan()==true)
		                {
							typePas.add("Bankpas");
							pasRFID.add(rs3.getString(1));
							dontInsertDuplicate = false;
							needBankPas = false;
		                }
		                else
		                {
		                	typePas.add(rs3.getString(3));
		                
							pasRFID.add(rs3.getString(1));
		                if(!pasNrs.contains(rs3.getInt(2)))
		                {
		                	currentPasNr = rs3.getInt(2);
		                	pasNrs.add(rs3.getInt(2));
		                	dontInsertDuplicate = true;
		                }
		                else
		                {
		                	currentPasNr = rs3.getInt(2);
		                	dontInsertDuplicate = false;
		                }
		                }
		                
		                	
		            }	
						
			 		ResultSet rsAbo;
			 		ResultSet rsBez;
			 		ResultSet rsAdhoc;
			 		ResultSet rsBank;
			 		if(dontInsertDuplicate == true)
			 		{
			 			dontInsertDuplicate = false;
			 				if(((typePas.get(0))).equals("Abonneepas"))
			 				{
			 					PreparedStatement prepstmtAbo = ConnectionCP("SELECT passen.RFIDnr, passen.Pastype, passen.Pasnr , Geblokkeerd FROM passen, abonnementspassen WHERE RFIDnr =? AND abonnementspassen.Pasnr =?");
			 					prepstmtAbo.setString(1, RFIDnrString);
			 					prepstmtAbo.setInt(2, currentPasNr);
			 					rsAbo = prepstmtAbo.executeQuery();
			 					int i = 0;
			 					while(rsAbo.next() & i < 1)
			 					{
			 						blockedOrNotAbo1.add(rsAbo.getString(1));
			 						blockedOrNotAbo1.add(rsAbo.getString(2));
			 						blockedOrNotAbo1.add(rsAbo.getInt(3));
			 						blockedOrNotAbo1.add(rsAbo.getInt(4));	
			 						i++;
			 					}
			 					AboPasnr1 = (int)blockedOrNotAbo1.get(2);	
			 					prepstmtAbo.close();
			 				}
			 				else if(((typePas.get(0))).equals("Bezoekerspas"))
			 				{
			 					PreparedStatement prepstmtBez = ConnectionCP("SELECT passen.RFIDnr, passen.Pastype, passen.Pasnr , Geblokkeerd FROM passen, bezoekerspassen WHERE RFIDnr =? AND bezoekerspassen.Pasnr =?");
			 					prepstmtBez.setString(1, RFIDnrString);
			 					prepstmtBez.setInt(2, currentPasNr);
			 					rsBez = prepstmtBez.executeQuery();
			 					int i = 0;
			 					while(rsBez.next() & i < 1)
			 					{
			 						blockedOrNotBez1.add(rsBez.getString(1));
			 						blockedOrNotBez1.add(rsBez.getString(2));
			 						blockedOrNotBez1.add(rsBez.getInt(3));
			 						blockedOrNotBez1.add(rsBez.getInt(4));
			 						i = 1;
			 						
			 					}
			 				
			 					BezPasnr1 = (int)blockedOrNotBez1.get(2);
			 					prepstmtBez.close();
			 				}
			 				else if(((typePas.get(0))).equals("Adhoc-pas"))
			 				{
			 					PreparedStatement prepstmtAdhoc = ConnectionCP("SELECT Pasnr FROM passen WHERE RFIDnr =?");
			 					prepstmtAdhoc.setString(1, RFIDnrString);
			 					rsAdhoc = prepstmtAdhoc.executeQuery();
			 					while(rsAdhoc.next())
			 					{
			 						AdhocPasnr1 = rsAdhoc.getInt(1);
			 					}
			 					prepstmtAdhoc.close();
			 				}
			 				else
			 				{
			 				}
			 				prepstmt3.close();
			 				
			 				if(blockedOrNotAbo1.isEmpty()==false)
			 				{
			 					if((int)blockedOrNotAbo1.get(3) == 1)
			 					{
			 						pasNrs.remove(new Integer(AboPasnr1));
			 						returnvalue = 2;
			 						insert = false;
			 					}
			 					else
			 					{
			 						insert = true;
			 					}
			 				}
			 				
			 				if(blockedOrNotBez1.isEmpty()==false)
			 				{
			 					if((int)blockedOrNotBez1.get(3) == 1)
			 					{
			 						pasNrs.remove(new Integer(BezPasnr1));
			 						returnvalue = 2;
			 						insert = false;
			 					}
			 					else
			 					{
			 						insert = true;
			 					}
			 				}
			 				if(insert == true) 
			 				{
			 					try
			 					{
			 						PreparedStatement prepstmt5 = ConnectionCP("SELECT MAX(Lognr) FROM logging");
			 						ResultSet rs5;	
			 						rs5 = prepstmt5.executeQuery();
			 						while(rs5.next())
			 						{
			 							newMaxLogNr = rs5.getInt(1) + 1;        
			 						}
			 						
			 						java.util.Date today = new java.util.Date();
			 						java.sql.Timestamp timeStamp = new java.sql.Timestamp(today.getTime());	
						
			 						PreparedStatement prepstmt6 = ConnectionCP("INSERT INTO logging(Lognr, Pasnr, Pastype, Inlogtijd, Uitchecktijd, Bedrag, Aantal_uren)" + "VALUES (?, ?, ?, ?, ?, ?, ?)");
			 						
			 						prepstmt6.setInt (1, newMaxLogNr);
			 						if(((typePas.get(0))).equals("Abonneepas"))
			 						{
			 							setPasNr(AboPasnr1);
			 						}
			 						else if(((typePas.get(0))).equals("Bezoekerspas"))
			 						{
			 							setPasNr(BezPasnr1);
			 						}
			 						else if(((typePas.get(0))).equals("Adhoc-pas"))
			 						{
			 							setPasNr(AdhocPasnr1);
			 							setLogNr(newMaxLogNr, AdhocPasnr1);
			 						}
			 						else 
			 						{
			 						}
			 						prepstmt6.setInt (2, getPasNr());
			 						prepstmt6.setString (3, (String)typePas.get(0));
			 						prepstmt6.setTimestamp(4, timeStamp);
			 						prepstmt6.setObject (5, null);
			 						prepstmt6.setObject (6, null);
			 						prepstmt6.setObject (7, null);
			 						
			 						prepstmt6.executeUpdate();
			 						prepstmt6.close();
			 						returnvalue = 1;
			 					}
			 					catch (Exception se) 
			 					{
			 						se.printStackTrace();
			 					}	
			 				}
			 				else
			 				{
			 				}
			 				canInsert = false;
			 				dontInsertDuplicate = true;
			 			}
			 		else
			 		{
			 			//uitrijden
			 			ResultSet rsAbo2;
				 		ResultSet rsBez2;
				 		ResultSet rsAdhoc2;
				 		ResultSet rsBankPas;
				 		
			 			
			 			if(((typePas.get(0))).equals("Abonneepas"))
		 				{
		 					PreparedStatement prepstmtAbo2 = ConnectionCP("SELECT passen.RFIDnr, passen.Pastype, passen.Pasnr , abonnementspassen.Geblokkeerd FROM passen, abonnementspassen WHERE RFIDnr =? AND abonnementspassen.Pasnr =?");
		 					prepstmtAbo2.setString(1, RFIDnrString);
		 					prepstmtAbo2.setInt(2, currentPasNr);
		 					rsAbo2 = prepstmtAbo2.executeQuery();
		 					while(rsAbo2.next())
		 					{
		 						blockedOrNotAbo2.add(rsAbo2.getString(1));
		 						blockedOrNotAbo2.add(rsAbo2.getString(2));
		 						blockedOrNotAbo2.add(rsAbo2.getInt(3));
		 						blockedOrNotAbo2.add(rsAbo2.getInt(4));
		 					}
		 					AboPasnr2 = (int)blockedOrNotAbo2.get(2);
		 					prepstmtAbo2.close();
		 				}
		 				else if(((typePas.get(0))).equals("Bezoekerspas"))
		 				{
		 					PreparedStatement prepstmtBez2 = ConnectionCP("SELECT passen.RFIDnr, passen.Pastype, passen.Pasnr , bezoekerspassen.Geblokkeerd FROM passen, bezoekerspassen WHERE RFIDnr =? AND bezoekerspassen.Pasnr =?");
		 					prepstmtBez2.setString(1, RFIDnrString);
		 					prepstmtBez2.setInt(2, currentPasNr);
		 					rsBez2 = prepstmtBez2.executeQuery();
		 					while(rsBez2.next())
		 					{
		 						blockedOrNotBez2.add(rsBez2.getString(1));
		 						blockedOrNotBez2.add(rsBez2.getString(2));
		 						blockedOrNotBez2.add(rsBez2.getInt(3));
		 						blockedOrNotBez2.add(rsBez2.getInt(4));
		 					}	
		 					BezPasnr2 = (int)blockedOrNotBez2.get(2);
		 					prepstmtBez2.close();
		 				}
		 				else if(((typePas.get(0))).equals("Adhoc-pas"))
		 				{
		 					PreparedStatement prepstmtAdhoc2 = ConnectionCP("SELECT Pasnr FROM passen WHERE RFIDnr =?");
		 					prepstmtAdhoc2.setString(1, RFIDnrString);
		 					rsAdhoc2 = prepstmtAdhoc2.executeQuery();
		 					while(rsAdhoc2.next())
		 					{
		 						AdhocPasnr2 = rsAdhoc2.getInt(1);
		 					}
		 					prepstmtAdhoc2.close();
		 				}
		 				else if(((typePas.get(0))).equals("Bankpas"))
		 				{
		 					PreparedStatement prepstmtBankPas = ConnectionBANK("SELECT Pasnr FROM bankpassen WHERE RFID =?");
		 					prepstmtBankPas.setString(1, RFIDnrString);
		 					rsBankPas = prepstmtBankPas.executeQuery();
		 					while(rsBankPas.next())
		 					{
		 						BankPasPasnr = rsBankPas.getInt(1);
		 					}
		 					prepstmtBankPas.close();
		 				}
		 				
		 				prepstmt3.close();
		 				
		 				if(blockedOrNotAbo2.isEmpty()==false)
		 				{
		 					if((int)blockedOrNotAbo2.get(3) == 1)
		 					{
		 						pasNrs.remove(new Integer(AboPasnr2));
		 						returnvalue = 2;
		 						update = false;
		 					}
		 					else
		 					{
		 						update = true;
		 					}
		 				}
		 				
		 				if(blockedOrNotBez2.isEmpty()==false)
		 				{
		 					if((int)blockedOrNotBez2.get(3) == 1)
		 					{
		 						pasNrs.remove(new Integer(BezPasnr2));
		 						returnvalue = 2;
		 						update = false;
		 					}
		 					else
		 					{
		 						update = true;
		 					}
		 				}
		 				
		 				if(update==true) 
		 				{
		 					if(dontSubtractMultipleTimes == true)
						 		{
		 						dontSubtractMultipleTimes = false;
				 			try
				 			{	
				 				if(((typePas.get(0))).equals("Abonneepas"))
		 						{
				 					setPasNr(AboPasnr2);
		 						}
		 						else if((typePas.get(0)).equals("Bezoekerspas"))
		 						{
		 							setPasNr(BezPasnr2);
		 						}
		 						else if((typePas.get(0)).equals("Adhoc-pas"))
		 						{
		 							setPasNr(AdhocPasnr2);
		 						}
		 						else if((typePas.get(0)).equals("Bankpas"))
		 						{	
		 							setPasNr(AdhocPasnr2);
		 							setBankPasNr(BankPasPasnr);
		 						}
		 						else 
		 						{
		 							
		 						}
				 				if((typePas.get(0)).equals("Bezoekerspas"))
				 				{
				 					PreparedStatement prepstmtTijdsTegoed = ConnectionCP("SELECT Saldo FROM bezoekerspassen WHERE Pasnr ="+getPasNr()+"");
				 					ResultSet rsTijdsTegoed;	
				 					rsTijdsTegoed = prepstmtTijdsTegoed.executeQuery();
				 					while(rsTijdsTegoed.next())
				 					{
				 						tijdsTegoed  = rsTijdsTegoed.getTime(1).toString();
				 					}		
				 					prepstmtTijdsTegoed.close();
				 				}
				 				else if((typePas.get(0)).equals("Adhoc-pas"))
				 				{
				 					
							 		System.out.println("Houdt u bankpas voor de scanner");
							 		needBankPas = true;
							 		setAdhocPasNr(AdhocPasnr2);
							 		
				 				}
							 		
							 		
				 				
				 				else
				 				{}
				 				
				 			
				 			
				 				java.util.Date today = new java.util.Date();
				 				java.sql.Timestamp oldTimeStamp = new java.sql.Timestamp(today.getTime());;
				 				
				 				PreparedStatement prepstmt7 = ConnectionCP("SELECT Inlogtijd FROM logging WHERE Pasnr ="+getPasNr()+"");
						 		ResultSet rs7;	
						 		rs7 = prepstmt7.executeQuery();
						 		while(rs7.next())
						 		{
						 			oldTimeStamp = rs7.getTimestamp(1);        
						 		}		
						 		java.sql.Timestamp newTimeStamp = new java.sql.Timestamp(today.getTime());
						 		String date1 = newTimeStamp.toString();
						 		String date2 = oldTimeStamp.toString(); 
						 		
						 		String tijdsverschil = (TimeManager.getTimeDifference(date1, date2));
						 		String aantaluren = TimeManager.getTimeDifference(date1, date2);
						 		if (!tijdsTegoed.equals("17:00:00"))
			 					{
						 			//newTijdsTegoed = tijdsTegoed - aantaluren ;
			 					}
						 		if((typePas.get(0)).equals("Bezoekerspas"))
						 		{
						 			System.out.println("nieuw tijdstegoed" + newTijdsTegoed);
			 						PreparedStatement prepstmtUpdateTijdsTegoed = ConnectionCP("UPDATE bezoekerspassen SET Saldo = ? WHERE Pasnr ="+getPasNr()+"");
					 			
			 						//prepstmtUpdateTijdsTegoed.setDouble(1, newTijdsTegoed);

			 						prepstmtUpdateTijdsTegoed.executeUpdate();
			 						prepstmtUpdateTijdsTegoed.close();
			 						returnvalue = 4;
						 		}
						 		else
						 		{
						 		}
						 		if(needBankPas == true)
						 		{
						 			returnvalue = 5;
						 		}
						 		else
						 		{	
						 			if(BankPasNr != 0)
						 			{
						 			Betalen PinPas = new Betalen(BankPasNr, getPasNr()) ;
						 			synchronized(this) {
						 			while(getPinCheck()==false)
						 			{
						 				getPinCheck();
						 			}
						 			PinPas.dispose();
						 			
						 			clearLogNr(getPasNr());
						 			}
						 			}
						 			else
						 			{
						 			}
						 			
						 			
						 			
						 		System.out.println("Database geupdate!");
						 		PreparedStatement prepstmt8 = ConnectionCP("UPDATE logging SET Uitchecktijd = ?, Bedrag = ?, Aantal_uren = ? WHERE Pasnr ="+getPasNr()+" AND Inlogtijd =?");
						 		
						 		prepstmt8.setTimestamp(1, newTimeStamp);
						 		prepstmt8.setDouble(2, Betalen.getBedrag());
						 		prepstmt8.setString(3, tijdsverschil);
						 		prepstmt8.setTimestamp(4, oldTimeStamp);
						 		
						 		prepstmt8.executeUpdate();
						 		prepstmt8.close();
						 		returnvalue = 4;
						 		dontSubtractMultipleTimes = false;
						 		setBankPasScanFalse();
						 		Betalen.clearBedrag();
						 		}	
				 			}
				 			
				 			
				 			catch (Exception se) 
							{
								se.printStackTrace();
							}	
						 		}
		 					else
		 					{
		 						if(needBankPas == true)
						 		{
						 			return 5;
						 		}
						 		else
						 		{
						 			return 4;
						 		}

		 					}
		 				}
		 				
			 		}
			 		
					}
	
					
					catch (Exception se) 
					{
						se.printStackTrace();
					}	
				
					
					
		    	}
				
		    	else
		    	{
		    		if(BankPasNr != 0)
			 		{	
		    			returnvalue = 4;
			 		}
		    		else
		    		{
		    			returnvalue = 3;
		    		}
		    		
		    	}
				return returnvalue;

			}
			
			public static PreparedStatement ConnectionCP(String prepareStatement)
			{
				PreparedStatement prepstmt = null;
				try 
				{
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					//System.out.println("JDBC driver loaded");

					// establish a connection
					Connection connection = DriverManager.getConnection
					("jdbc:mysql://localhost/ploeg1cp", "root", "");
					//System.out.println("Connection to database established");
					
					prepstmt = connection.prepareStatement(prepareStatement);
					
				}
				catch (Exception se) 
				{
					se.printStackTrace();
				}
					return prepstmt;
			}
			
			public static PreparedStatement ConnectionBANK(String prepareStatement)
			{
				PreparedStatement prepstmt = null;
				try 
				{
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					//System.out.println("JDBC driver loaded");

					// establish a connection
					Connection connection = DriverManager.getConnection
					("jdbc:mysql://localhost/ploeg1bank", "root", "");
					//System.out.println("Connection to database established");
					
					prepstmt = connection.prepareStatement(prepareStatement);
					
				}
				catch (Exception se) 
				{
					se.printStackTrace();
				}
					return prepstmt;
			}
				
				
}
			
			
			
			
