/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citypark.client;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import citypark.CityPark.Bank.*;

public class Client 
{
	//private Bank bank;

	public static boolean transferpin(int af, int at, double am, int pin) throws Exception 
	{	
		URL url = new URL("http://localhost:8080/CityPark_Bank/BankImplService?wsdl");
		QName qname = new QName("http://Bank.CityPark/", "BankImplService");
		QName qname2 = new QName("http://Bank.CityPark/", "BankImplPort");
		Service service = Service.create(url, qname);
		Bank bank = service.getPort(qname2, Bank.class);
		return bank.transferpin(af, at, am, pin);
	}
}