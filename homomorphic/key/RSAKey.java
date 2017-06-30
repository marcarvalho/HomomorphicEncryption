package com.homomorphic.key;

import java.math.BigInteger;

public class RSAKey extends Key {

	BigInteger p;
	BigInteger q;
	BigInteger phi;
	public BigInteger getP() {
		return p;
	}
	public void setP(BigInteger p) {
		this.p = p;
	}
	public BigInteger getQ() {
		return q;
	}
	public void setQ(BigInteger q) {
		this.q = q;
	}
	public BigInteger getPhi() {
		return phi;
	}
	public void setPhi(BigInteger phi) {
		this.phi = phi;
	}
	
	
	
}
