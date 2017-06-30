
package com.homomorphic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.homomorphic.models.*;

public class Homomorphic {

	private static final int ELGAMAL = 1;
	private static final int PAILLIER = 2;

	private int model;
	private Paillier modelPaillier;
	private static final int decimalEliminator = 1000;

	/*****************EL GAMAL VARIABLES*********************************/
	private List<List<BigInteger>> pksk = ElGamal.KeyGen(200).getPkSk();
	// public key
	private BigInteger p = pksk.get(0).get(0);
	private BigInteger g = pksk.get(0).get(1);
	private BigInteger h = pksk.get(0).get(2);
	// secret key
	private BigInteger p_sk = pksk.get(1).get(0);
	private BigInteger x = pksk.get(1).get(1);
	/*****************EL GAMAL VARIABLES*********************************/

	public Homomorphic(String system) throws Exceptions {

		if (system.equalsIgnoreCase("elgamal")) {
			model = ELGAMAL;
			ElGamal ElGamal = new ElGamal();
			pksk = ElGamal.KeyGen(200).getPkSk();
			// public key
			p = pksk.get(0).get(0);
			g = pksk.get(0).get(1);
			h = pksk.get(0).get(2);
			// secret key
			p_sk = pksk.get(1).get(0);
			x = pksk.get(1).get(1);
		} else if (system.equalsIgnoreCase("paillier")) {
			model = PAILLIER;
			modelPaillier = new Paillier();
		} else {
			throw new Exceptions("Illegal parameter for constructor!");
		}

	}

	public BigInteger preProcess(double x) {
		return new BigInteger("" + (int) (x * decimalEliminator) + "");
	}

	public double postProcess(BigInteger x) {
		return ((double) x.intValue()) / decimalEliminator;
	}

	// It is used to round decimal numbers to int
	private int encode(double recip) {
		return (int) (recip * decimalEliminator);
	}

	public List<BigInteger> encrypt(double message_double) throws Exceptions {

		BigInteger message = preProcess(message_double);

		switch (model) {
		case ELGAMAL:
			return ElGamal.Encrypt_Homomorph(p, g, h, message);
		case PAILLIER:
			return new ArrayList<>(Arrays.asList(modelPaillier.Encryption(message)));
		default:
			throw new Exceptions("Undefined Encryption Model");
		}

	}

	public double decrypt(List<BigInteger> cipher) throws Exceptions {

		switch (model) {
		case ELGAMAL:
			BigInteger result = ElGamal.Decrypt_homomorphe(p_sk, x, g, cipher.get(0), cipher.get(1));
			return postProcess(result);
		case PAILLIER:
			BigInteger resultPaillier = modelPaillier.Decryption(cipher.get(0));
			return postProcess(resultPaillier);
		default:
			throw new Exceptions("Undefined Encryption Model");
		}

	}

	public List<BigInteger> multiply(List<BigInteger> x1_enc, List<BigInteger> x2_enc) throws Exceptions {
		switch (model) {
		case ELGAMAL:
			List<BigInteger> result = encrypt(0);
			for (int i = 0; i < decrypt(x2_enc); i++) {
				result = add(result, x1_enc);
			}
			return result;
		case PAILLIER:
			return new ArrayList<>(Arrays.asList(x1_enc.get(0).modPow(
					new BigInteger("" + modelPaillier.Decryption(x2_enc.get(0)).intValue() / decimalEliminator + ""),
					modelPaillier.nsquare)));
		default:
			throw new Exceptions("Multiply Null Case!");
		}
	}

	public List<BigInteger> multiply(List<BigInteger> x1_enc, List<BigInteger> x2_enc, boolean dump) throws Exceptions {
		List<BigInteger> multiplyResultEncrypted = multiply(x1_enc, x2_enc);
		if (dump) {
			long startTime, stopTime, elapsedTime;
			startTime = System.currentTimeMillis();
			double multiplyResult = decrypt(multiplyResultEncrypted);
			System.out.print("\n\nmultiplyResultEncrypted : ");
			for (int i = 0; i < multiplyResultEncrypted.size(); i++){
				System.out.print(multiplyResultEncrypted.get(i));
				if (i+1 != multiplyResultEncrypted.size()){
					System.out.print(", ");
				}
			}
			System.out.println("\nmultiplyResult : " + multiplyResult);
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println("Elapsed time : " + elapsedTime + " ms\n");
		}
		return multiplyResultEncrypted;
	}

	public List<BigInteger> divide(List<BigInteger> x1_enc, List<BigInteger> x2_enc) throws Exceptions {
		double x1 = decrypt(x1_enc);
		double x2 = decrypt(x2_enc);
		double toBeEncoded = ((double) 1) / x2;
		int encoded = encode(toBeEncoded);
		List<BigInteger> encrypted = encrypt(encoded);
		List<BigInteger> bol_e = encrypt(x1);
		double result = ((decrypt(multiply(encrypted, bol_e)))) / decimalEliminator;
		return encrypt(result);
	}

	public List<BigInteger> divide(List<BigInteger> x1_enc, List<BigInteger> x2_enc, boolean dump) throws Exceptions {
		List<BigInteger> divideResultEncrypted = divide(x1_enc, x2_enc);
		if (dump) {
			long startTime, stopTime, elapsedTime;
			startTime = System.currentTimeMillis();
			double divideResult = decrypt(divideResultEncrypted);
			System.out.print("\n\ndivideResultEncrypted : ");
			for (int i = 0; i < divideResultEncrypted.size(); i++){
				System.out.print(divideResultEncrypted.get(i));
				if (i+1 != divideResultEncrypted.size()){
					System.out.print(", ");
				}
			}
			System.out.println("\ndivideResult : " + divideResult);
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println("Elapsed time : " + elapsedTime + " ms\n");
		}
		return divideResultEncrypted;
	}

	public List<BigInteger> add(List<BigInteger> x1_enc, List<BigInteger> x2_enc) throws Exceptions {
		if (model == ELGAMAL) {
			return new ArrayList<>(
					Arrays.asList(x1_enc.get(0).multiply(x2_enc.get(0)), x1_enc.get(1).multiply(x2_enc.get(1))));
		} else if (model == PAILLIER) {
			return new ArrayList<>(Arrays.asList(x1_enc.get(0).multiply(x2_enc.get(0)).mod(modelPaillier.nsquare)));
		} else {
			throw new Exceptions("Add operation is not supported with this model!");
		}
	}

	public List<BigInteger> add(List<BigInteger> x1_enc, List<BigInteger> x2_enc, boolean dump) throws Exceptions {
		List<BigInteger> addResultEncrypted = add(x1_enc, x2_enc);
		if (dump) {
			long startTime, stopTime, elapsedTime;
			startTime = System.currentTimeMillis();
			double addResult = decrypt(addResultEncrypted);
			System.out.print("\n\naddResultEncrypted : ");
			for (int i = 0; i < addResultEncrypted.size(); i++){
				System.out.print(addResultEncrypted.get(i));
				if (i+1 != addResultEncrypted.size()){
					System.out.print(", ");
				}
			}
			System.out.println("\naddResult : " + addResult);
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println("Elapsed time : " + elapsedTime + " ms\n");
		}
		return addResultEncrypted;
	}

	public List<BigInteger> subtract(List<BigInteger> x1_enc, List<BigInteger> x2_enc) throws Exceptions {
		// Basically, add(x1_enc, (-1) * x2_enc)
		double x2 = decrypt(x2_enc);
		x2 = -x2;
		List<BigInteger> x2_reEnc = encrypt(x2);
		return add(x1_enc, x2_reEnc);
	}

	public List<BigInteger> subtract(List<BigInteger> x1_enc, List<BigInteger> x2_enc, boolean dump) throws Exceptions {
		List<BigInteger> subtractResultEncrypted = subtract(x1_enc, x2_enc);
		if (dump) {
			long startTime, stopTime, elapsedTime;
			startTime = System.currentTimeMillis();
			double subtractResult = decrypt(subtractResultEncrypted);
			System.out.print("\n\nsubtractResultEncrypted : ");
			for (int i = 0; i < subtractResultEncrypted.size(); i++){
				System.out.print(subtractResultEncrypted.get(i));
				if (i+1 != subtractResultEncrypted.size()){
					System.out.print(", ");
				}
			}
			System.out.println("\nsubtractResult : " + subtractResult);
			stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			System.out.println("Elapsed time : " + elapsedTime + " ms\n");
		}
		return subtractResultEncrypted;
	}

	public double operateMod(double first, double second) {
		double r = first % second;
		if (r < 0) {
			r += second;
		}
		return r;
	}

	public static void main(String[] args) throws Exception {

		// Set algorithm
		String algorithm = "paillier";
		System.out.println("Algorithm : " + algorithm);
		Homomorphic he = new Homomorphic(algorithm);

		// Set variables
		double v1 = 14, v2 = 13;

		// Encrypt variables
		List<BigInteger> v1_e = he.encrypt(v1);
		List<BigInteger> v2_e = he.encrypt(v2);

		// Perform calculations
		// Usage : he.OPERATION(List<BigInteger>,List<BigInteger>,boolean)
		// Last parameter outputs dump if it is set to true
		List<BigInteger> addResultEncrypted = he.add(v1_e, v2_e,true);
		List<BigInteger> multiplyResultEncrypted = he.multiply(v1_e, v2_e,true);
		List<BigInteger> subtractResultEncrypted = he.subtract(v1_e, v2_e,true);
		List<BigInteger> divideResultEncrypted = he.divide(v1_e, v2_e,true);
	}

}
