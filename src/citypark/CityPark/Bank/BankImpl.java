/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citypark.CityPark.Bank;

import javax.jws.WebService;
import java.sql.*;

@WebService(endpointInterface = "CityPark.Bank.Bank")

public class BankImpl implements Bank {
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost/ploeg1bank";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    public Connection getDBConnection() {
        Connection dbConnection = null;
        
        try {         
            Class.forName(DB_DRIVER).newInstance();

            // establish a connection
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            
            return dbConnection;
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
        }
        return dbConnection;   
    }
        
    /**
     *
     * @param accountnr
     * @param amount
     * @return
     */
    @Override
    public boolean doCreditCheck(int accountnr, double amount) { 
        Connection con = getDBConnection();
        double bedrag;
        double krediet;

        String selectStatement =    "SELECT * "
                                    + "FROM rekening "
                                    + "WHERE Rekeningnr = ?;";
        

        try{
            con.setAutoCommit(false);
            PreparedStatement prepStmt = con.prepareStatement(selectStatement);
            prepStmt.setInt(1, accountnr);
            ResultSet rs = prepStmt.executeQuery();

            if(rs.next()){
                bedrag = rs.getDouble("Bedrag");
                krediet = rs.getInt("Kredietlimiet");
                bedrag -= amount;
                if(bedrag > krediet){
                    return true;
                }
                else{
                    return false;
                }
            } 
        }
        catch(SQLException e) {
              System.out.println(e.getMessage());
        }
        return false;
    }
    
    /**
     *
     * @param accountfrom
     * @param accountto
     * @param amount
     * @return
     */
    @Override
    public boolean transfer(int accountfrom, int accountto, double amount){
        Connection con = getDBConnection();
        double bedragFrom, bedragTo;
        boolean creditCheck;
        
        String selectStatement = "SELECT Bedrag FROM rekening WHERE Rekeningnr = ?";
        
        try {
            con.setAutoCommit(false);
            PreparedStatement fromStmt = con.prepareStatement(selectStatement);
            PreparedStatement toStmt = con.prepareStatement(selectStatement);
            fromStmt.setInt(1, accountfrom);
            toStmt.setInt(1, accountto);
            ResultSet rsFrom = fromStmt.executeQuery();
            ResultSet rsTo = toStmt.executeQuery();
            
            creditCheck = doCreditCheck(accountfrom, amount);
            if(creditCheck == true && rsFrom.next() && rsTo.next()){
                bedragFrom = (rsFrom.getDouble("Bedrag") - amount);
                bedragTo = (rsTo.getDouble("Bedrag") + amount);
                updateRekening(accountfrom, bedragFrom);
                updateRekening(accountto, bedragTo);
                insertTransaction(accountfrom, accountto, amount);
                con.commit();
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception ex) {
            try {
                con.rollback();
                ex.getMessage();
            }
            catch (SQLException sqx) {
                sqx.getMessage();
            }
        }
        return false;
    }
    
    /**
     *
     * @param accountfrom
     * @param accountto
     * @param amount
     * @param pin
     * @return
     */
    @Override
    public boolean transferpin(int accountfrom, int accountto, double amount, int pin){
        Connection con = getDBConnection();
        double bedragFrom, bedragTo;
        boolean creditCheck, pincodeCheck;
        
        String selectStatement = "SELECT Bedrag FROM rekening WHERE Rekeningnr = ?";
        
        try {
            con.setAutoCommit(false);
            PreparedStatement fromStmt = con.prepareStatement(selectStatement);
            PreparedStatement toStmt = con.prepareStatement(selectStatement);
            fromStmt.setInt(1, accountfrom);
            toStmt.setInt(1, accountto);
            ResultSet rsFrom = fromStmt.executeQuery();
            ResultSet rsTo = toStmt.executeQuery();
            
            pincodeCheck = checkPincode(accountfrom, pin);
            creditCheck = doCreditCheck(accountfrom, amount);
            if(creditCheck == true && pincodeCheck == true && rsFrom.next() && rsTo.next()){
                bedragFrom = (rsFrom.getDouble("Bedrag") - amount);
                bedragTo = (rsTo.getDouble("Bedrag") + amount);
                updateRekening(accountfrom, bedragFrom);
                updateRekening(accountto, bedragTo);
                insertTransaction(accountfrom, accountto, amount);
                con.commit();
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception ex) {
            try {
                con.rollback();
                System.out.println(ex.getMessage());
                ex.getMessage();
            }
            catch (SQLException sqx) {
                System.out.println(sqx.getMessage());
            }
        }
        return false;
    }
   
/****************************** DATABASE METHODES ******************************/
    
    public void updateRekening(int accountnr, double amount) throws SQLException{
        Connection con = getDBConnection();
        
        String updateRekening = "UPDATE rekening SET Bedrag = ? WHERE Rekeningnr = ?";
        PreparedStatement prepStmt = con.prepareStatement(updateRekening);
        
        prepStmt.setDouble(1, amount);
        prepStmt.setInt(2, accountnr);
        prepStmt.executeUpdate();
        prepStmt.close();        
    }
    
    public void insertTransaction(int accountfrom, int accountto, double amount) throws SQLException {
        Connection con = getDBConnection();
        
        String insertTransaction =  "INSERT INTO transacties (Rekeningnr_van, Rekeningnr_naar, Omschrijving, Bedrag, Datum) "
                                    + "VALUES (?, ?, 'Parkeren CityPark', ?, CURRENT_TIMESTAMP)";
        
        PreparedStatement prepStmt = con.prepareStatement(insertTransaction);
        
        prepStmt.setInt(1, accountfrom);
        prepStmt.setInt(2, accountto);
        prepStmt.setDouble(3, amount);
        prepStmt.executeUpdate();
        prepStmt.close();
    }
    
    public boolean checkPincode(int accountnr, int pin) throws SQLException {
        Connection con = getDBConnection();
        int result;
        
        String selectStatement =    "SELECT Pincode FROM rekening, bankpassen WHERE rekening.pasnr = bankpassen.pasnr AND rekening.rekeningnr = ?";
        
        try{
            con.setAutoCommit(false);
            PreparedStatement prepStmt = con.prepareStatement(selectStatement);
            prepStmt.setInt(1, accountnr);
            ResultSet rs = prepStmt.executeQuery();
            
            if(rs.next()){
                result = rs.getInt("Pincode");
                if(result == pin){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
            return false; 
        }                           
    }
}