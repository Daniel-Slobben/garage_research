/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citypark.CityPark.Bank;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface Bank {
    
    @WebMethod boolean doCreditCheck(int accountnr, double amount);
    @WebMethod boolean transfer(int accountfrom, int accountto, double amount);
    @WebMethod boolean transferpin(int accountfrom, int accountto, double amount, int pin);
}