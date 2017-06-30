package com.homomorphic.key;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElGamalKey extends Key {
	
	BigInteger p;
	BigInteger g;
	BigInteger pPrime;
	BigInteger x;
	BigInteger h;
	List<BigInteger> sk;
	List<BigInteger> pk;
	
	
	public List<List<BigInteger>> getPkSk(){
		return new ArrayList<>(Arrays.asList(pk, sk));
	}
	
	public BigInteger getX() {
		return x;
	}
	public void setX(BigInteger x) {
		this.x = x;
	}
	public BigInteger getH() {
		return h;
	}
	public void setH(BigInteger h) {
		this.h = h;
	}
	public List<BigInteger> getSk() {
		return sk;
	}
	public void setSk(List<BigInteger> sk) {
		this.sk = sk;
	}
	public List<BigInteger> getPk() {
		return pk;
	}
	public void setPk(List<BigInteger> pk) {
		this.pk = pk;
	}
	public BigInteger getP() {
		return p;
	}
	public void setP(BigInteger p) {
		this.p = p;
	}
	public BigInteger getG() {
		return g;
	}
	public void setG(BigInteger g) {
		this.g = g;
	}
	public BigInteger getpPrime() {
		return pPrime;
	}
	public void setpPrime(BigInteger pPrime) {
		this.pPrime = pPrime;
	}
	
	
	

}
