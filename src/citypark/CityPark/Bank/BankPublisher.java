/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citypark.CityPark.Bank;

import javax.xml.ws.Endpoint;

public class BankPublisher {

    public static void main(String[ ] args) {

      // 1st argument is the publication URL
      // 2nd argument is an SIB instance

      Endpoint.publish("http://127.0.0.1:9876/bank", new BankImpl());

    }
}