package com.homomorphic.models;


import java.math.BigInteger;
import java.security.SecureRandom;

import com.homomorphic.key.RSAKey;
   

public class RSA {
	
  //key random, byte[]
  //method
	
	
  private final static BigInteger one = new BigInteger("1");
  private final static SecureRandom random = new SecureRandom();

  private BigInteger privateKey;
  private BigInteger publicKey;
  private BigInteger modulus;
  private RSAKey myKey;
  
  //byte[] key = {0, 3, 5};

  // generate an N-bit (roughly) public and private key
  public RSA(int N) {
  BigInteger p = BigInteger.probablePrime(N/2, random);
  BigInteger q = BigInteger.probablePrime(N/2, random);
  BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
  
  RSAKey myKey = new RSAKey();
  myKey.setP(p);
  myKey.setPhi(phi);
  myKey.setQ(q);

  modulus = myKey.getP().multiply(myKey.getQ());  
  publicKey = new BigInteger("65537"); // common value in practice = 2^16 + 1
  privateKey = publicKey.modInverse(myKey.getPhi());
  }


  public BigInteger encrypt(BigInteger message) {
	  // public,modulus
  return message.modPow(publicKey, myKey.getP().multiply(myKey.getQ()));
  }

  public BigInteger decrypt(BigInteger encrypted) {
	  //private, modulus
  return encrypted.modPow(publicKey.modInverse(myKey.getPhi()), myKey.getP().multiply(myKey.getQ()));
  }

  
  public String toString() {
  String s = "";
  s += "public = " + publicKey + "\n";
  s += "private = " + privateKey + "\n";
  s += "modulus = " + modulus;
  return s;
  }
  
  /**
  public static void main(String[] args) {
 
  RSA key = new RSA(100);
  //System.out.println(key);
 
  BigInteger x1 = new BigInteger("100");
  BigInteger x2 = new BigInteger("2");
  BigInteger x3 = x1.multiply(x2);

  BigInteger enc_x1 = key.encrypt(x1);
  BigInteger enc_x2 = key.encrypt(x2);
  BigInteger enc_x3 = key.encrypt(x3);
  
  BigInteger dec_x1 = key.decrypt(enc_x1);
  BigInteger dec_x2 = key.decrypt(enc_x2);
  BigInteger dec_x3 = key.decrypt(enc_x3);
 
  BigInteger homomorphic = enc_x1.multiply(enc_x2);
  BigInteger dec_h = key.decrypt(homomorphic);
   
  System.out.println("x1 = " + x1);
  System.out.println("x2 = " + x2);
  System.out.println("Expected : x1 * x2 = " + (x1.multiply(x2)));
  
  System.out.println("E ( x1 ) = " + enc_x1);
  System.out.println("E ( x2 ) = " + enc_x2);
  System.out.println("E ( x1 ) * E ( x2 ) = " + homomorphic);
  System.out.println("E ( x1 * x2 ) = " + enc_x3);
  System.out.println("D ( E ( x1 ) * E ( x2 ) ) = " + dec_h + " (Computations on ENCRYPTED data)");
  System.out.println("D ( E ( x1 * x2 ) ) = " + dec_x3 + " (Computations on DECRYPTED data)");
  }**/
}
