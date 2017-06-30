package com.homomorphic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AccountActivity {
	
	List<String> names = new ArrayList<String>();
	List<Double> amounts = new ArrayList<Double>();
	
	public AccountActivity(String dataPath){
	       try {
	            BufferedReader in = new BufferedReader(new FileReader(dataPath));
	            String str;
	            str = in.readLine();
	            while ((str = in.readLine()) != null) {
	            	String[] ar=str.split(",");
	            	names.add(ar[0]);
	            	amounts.add(Double.parseDouble(ar[1]));
	            }
	            in.close();
	        } catch (IOException e) {
	            System.out.println("File Read Error");
	        }
	}
	
	public void getAllReports() throws Exceptions{
		getLocalReport();
		getHomomorphicReport();
		try {
			getAESReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getLocalReport(){
		
	       long startTime;
	       long stopTime;
	       long elapsedTime;
		       
	       startTime = System.currentTimeMillis();       
		       /**********************************************************************/
		       System.out.println("         LOCAL ");
		       System.out.println("Name             Amount");
		       System.out.println("-----------------------");
		       double total = 0;
		       for (int i = 0; i < names.size(); i++){
		    	   System.out.print(names.get(i));
		    	   for (int k = 0; k < ( 23 - (names.get(i).length() + amounts.get(i).toString().length()) ); k++){
		    		   System.out.print(" ");
		    	   }
		    	   System.out.print(amounts.get(i) + "\n");
		    	   total += amounts.get(i);
		       }
		       System.out.println("-----------------------");
		       System.out.println("Total             " + total);
		       /**********************************************************************/       
		       stopTime = System.currentTimeMillis();
		       elapsedTime = stopTime - startTime;
		       System.out.println("----------------------------------------------\n This operation took " +
		       elapsedTime + "ms (~ " + (elapsedTime/1000) + " sn) \n\n");      
	}
	
	public void getHomomorphicReport() throws Exceptions{
		
	       long startTime;
	       long stopTime;
	       long elapsedTime;
	       
	       Homomorphic he = new Homomorphic("paillier");
	       
	       startTime = System.currentTimeMillis();       
	       System.out.println("\n\n         CLOUD ");
	       System.out.println("Name             Amount");
	       System.out.println("-----------------------");
	       List<BigInteger> addResultEncrypted = he.encrypt(1);
	       for (int i = 0; i < names.size(); i++){
	    	   System.out.print(names.get(i));
	    	   for (int k = 0; k < ( 23 - (names.get(i).length() + amounts.get(i).toString().length()) ); k++){
	    		   System.out.print(" ");
	    	   }
	    	   List<BigInteger> amount_e = he.encrypt(amounts.get(i));
	    	   addResultEncrypted = he.add(amount_e, addResultEncrypted);
	    	   System.out.print(amount_e + "\n");
	       }
	       System.out.println("-----------------------");
	       System.out.println("Total             " + addResultEncrypted);
	       System.out.println("Decrypt(Total)             " + (he.decrypt(addResultEncrypted)-1));      
	       stopTime = System.currentTimeMillis();
	       elapsedTime = stopTime - startTime;
	       System.out.println("----------------------------------------------\n This operation took " +
	       elapsedTime + "ms (~ " + (elapsedTime/1000) + " sn) \n\n");      
	   			
		
	}
	
	public void getAESReport() throws Exception{
		
	       long startTime;
	       long stopTime;
	       long elapsedTime;
	       
	       startTime = System.currentTimeMillis();       
			byte[] key = {75, 110, -45, -61, 101, -103, -26, -25, 55, -70, 19, 51, 66, -91, -35, 19}; //128 bit key
			AES aes = new AES(key);
	       
	       List<byte[]> aesEnc = new ArrayList<byte[]>();
	       double aesTotal = 0;
	       
	       System.out.println("\n\n         CLOUD - AES ");
	       System.out.println("Name               Amount                      Decrypted");
	       System.out.println("--------------------------------------------------------");
	       for (int i = 0; i < names.size(); i++){
	    	   System.out.print(names.get(i));
	    	   for (int k = 0; k < ( 23 - (names.get(i).length() + amounts.get(i).toString().length()) ); k++){
	    		   System.out.print(" ");
	    	   }
	    	  
	    	   
	    	   
	    	   byte[] amount_e = aes.encrypt(amounts.get(i),key);
	    	   aesEnc.add(amount_e);
	    	   double decrypted = ByteBuffer.wrap(aes.decrypt(amount_e, key)).getDouble();
	    	   aesTotal += decrypted;
	    	   System.out.print(Base64.getEncoder().encodeToString(amount_e) + "    " + decrypted + "\n");
	       }
	       System.out.println("--------------------------------------------------------");
	       System.out.print("Total              " + Base64.getEncoder().encodeToString(aes.encrypt(aesTotal,key)) + "    " + aesTotal + "\n");
	       
   
	       stopTime = System.currentTimeMillis();
	       elapsedTime = stopTime - startTime;
	       System.out.println("----------------------------------------------\n This operation took " +
	       elapsedTime + "ms (~ " + (elapsedTime/1000) + " sn) \n\n"); 
		
	}
	
	public static void main (String[] args) throws Exceptions{
		
		AccountActivity customer = new AccountActivity("C:/Users/stj.tkayim/workspace/HomE/dataset/data.txt");
		
		customer.getAllReports();
		
		/**
		try {
			customer.getAESReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		**/
	}
	
}
