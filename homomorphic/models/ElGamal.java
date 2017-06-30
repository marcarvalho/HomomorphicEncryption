package com.homomorphic.models;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.homomorphic.key.ElGamalKey;
import com.homomorphic.key.Key;

/**
 * Security of the ElGamal algorithm depends on the difficulty of computing discrete logs
 * in a large prime modulus
 *
 * - Theorem 1 : a in [Z/Z[p]] then a^(p-1) [p] = 1
 * - Theorem 2 : the order of an element split the order group
 */
public final class ElGamal { // TODO extends Cryptosystem
	
	//sum, multiply
	//division -> fully *
	
	//a+b
	//a*b

    public static BigInteger TWO = new BigInteger("2");

    /**
     * Generate the public key and the secret key for the ElGamal encryption.
     *
     * @param n key size
     */
    public static ElGamalKey KeyGen(int n) {
        // (a) take a random prime p with getPrime() function. p = 2 * p' + 1 with prime(p') = true
        BigInteger p = getPrime(n, 40, new Random());
        // (b) take a random element in [Z/Z[p]]* (p' order)
        BigInteger g = randNum(p, new Random());
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamal.TWO);

        while (!g.modPow(pPrime, p).equals(BigInteger.ONE)) {
            if (g.modPow(pPrime.multiply(ElGamal.TWO), p).equals(BigInteger.ONE))
                g = g.modPow(TWO, p);
            else
                g = randNum(p, new Random());
        }

        // (c) take x random in [0, p' - 1]
        BigInteger x = randNum(pPrime.subtract(BigInteger.ONE), new Random());
        BigInteger h = g.modPow(x, p);
        // secret key is (p, x) and public key is (p, g, h)
        List<BigInteger> sk = new ArrayList<>(Arrays.asList(p, x));
        List<BigInteger> pk = new ArrayList<>(Arrays.asList(p, g, h));
        // [0] = pk, [1] = sk
        
        ElGamalKey myKey = new ElGamalKey();
        myKey.setP(p);
        myKey.setG(g);
        myKey.setpPrime(pPrime);
        myKey.setX(x);
        myKey.setH(h);
        myKey.setSk(sk);
        myKey.setPk(pk);
        
        return myKey;
    }


    /**
     * Encrypt ElGamal homomorph
     *
     * @param (p,g,h) public key
     * @param message message
     */
    public static List<BigInteger> Encrypt_Homomorph(BigInteger p, BigInteger g, BigInteger h, BigInteger message) {
        BigInteger pPrime = p.subtract(BigInteger.ONE).divide(ElGamal.TWO);
        // TODO [0, N -1] or [1, N-1] ?
        BigInteger r = randNum(pPrime, new Random());
        // encrypt couple (g^r, h^r * g^m)
        BigInteger hr = h.modPow(r, p);
        BigInteger gm = g.modPow(message, p);
        return new ArrayList<>(Arrays.asList(g.modPow(r, p), hr.multiply(gm)));
    }


    /**
     * Decrypt ElGamal homomorphe
     * Remarque : il faudra quand m�me faire une recherche exhaustive de log discret (g^m)
     * @param (p,x) secret key
     * @param (gr,mhr) (g^r, h^r * g^m)
     * @return the decrypted message
     */
    public static BigInteger Decrypt_homomorphe(BigInteger p, BigInteger x, BigInteger g, BigInteger gr, BigInteger hrgm) {
        BigInteger hr = gr.modPow(x,p);
        BigInteger gm = hrgm.multiply(hr.modInverse(p)).mod(p);

        BigInteger m = BigInteger.ONE;
        BigInteger gm_prime = g.modPow(m, p);

        while (!gm_prime.equals(gm)) {
            m = m.add(BigInteger.ONE);
            gm_prime = g.modPow(m, p);
        }

        return m;
    }

    /**
     * Return a prime p = 2 * p' + 1
     *
     * @param nb_bits   is the prime representation
     * @param certainty probability to find a prime integer
     * @param prg       random
     * @return p
     */
    public static BigInteger getPrime(int nb_bits, int certainty, Random prg) {
        BigInteger pPrime = new BigInteger(nb_bits, certainty, prg);
        // p = 2 * pPrime + 1
        BigInteger p = pPrime.multiply(TWO).add(BigInteger.ONE);

        while (!p.isProbablePrime(certainty)) {
            pPrime = new BigInteger(nb_bits, certainty, prg);
            p = pPrime.multiply(TWO).add(BigInteger.ONE);
        }
        return p;
    }

    /**
     * Return a random integer in [0, N - 1]
     *
     * @param N
     * @param prg
     * @return
     */
    public static BigInteger randNum(BigInteger N, Random prg) {
        return new BigInteger(N.bitLength() + 100, prg).mod(N);
    }

    public static void main(String[] args) {
        
        
        
        ElGamalKey myKey = ElGamal.KeyGen(200);
        List<List<BigInteger>> pksk = myKey.getPkSk();
        // public key
        BigInteger p = pksk.get(0).get(0);
        
        p = myKey.getP();
        
        BigInteger g = pksk.get(0).get(1);
        BigInteger h = pksk.get(0).get(2);
        // secret key
        BigInteger p_sk = pksk.get(1).get(0);
        BigInteger x = pksk.get(1).get(1);

        
        BigInteger input1 = new BigInteger("16");
        BigInteger input2 = new BigInteger("3");
        BigInteger result = input1.add(input2);
        
        System.out.println("x1 = " + input1);
        System.out.println("x2 = " + input2);
        System.out.println("Expected : x1 + x2 = " + input1.add(input2));
        
        List<BigInteger> encrypt1 = ElGamal.Encrypt_Homomorph(p, g, h, input1);
        List<BigInteger> encrypt2 = ElGamal.Encrypt_Homomorph(p, g, h, input2);
        List<BigInteger> encrypt3 = ElGamal.Encrypt_Homomorph(p, g, h, result);
        
        
        System.out.println("E ( x1 ) : " + encrypt1.get(1));        
        System.out.println("E ( x2 ) : " + encrypt2.get(1));
        System.out.println("E ( x1 ) + E ( x2 ) : " + (encrypt1.get(1).add(encrypt2.get(1))));
        System.out.println("E ( x1 + x2 ) : " + (encrypt3.get(1)));
        System.out.println("D ( E ( x1 ) + E ( x2 ) ) : " + ElGamal.Decrypt_homomorphe(p_sk, x, g, encrypt1.get(0).multiply(encrypt2.get(0)), encrypt1.get(1).multiply(encrypt2.get(1))) + " (Computations on ENCRYPTED data)");
        System.out.println("D ( E ( x1 + x2 ) ) : " + ElGamal.Decrypt_homomorphe(p_sk, x, g, encrypt3.get(0), encrypt3.get(1)) + " (Computations on DECRYPTED data)");
        
    }
}