package com.homomorphic;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	byte[] key;
	String algorithm;
	public AES(byte[] key) {
		algorithm = "AES";
		// {75, 110, -45, -61, 101, -103, -26, -25, 55, -70, 19, 51, 66, -91, -35, 19}; //128 bit key
		this.key = key;
	}
	
	public byte[] encrypt(double input, byte[] key) throws Exception{
		byte[] content = new byte[8];
		ByteBuffer.wrap(content).putDouble(input);
		Cipher encryption = Cipher.getInstance(algorithm);
		encryption.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, 0, key.length, algorithm));
		byte[] encryptedContent = encryption.doFinal(content);
		
		return encryptedContent;
		
	}
	
	public byte[] decrypt(byte[] input, byte[] key) throws Exception {

		byte[] encryptedContent = input;
		//System.out.println(new String(encryptedContent));
		
		Cipher decryption = Cipher.getInstance(algorithm);
		decryption.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, 0, key.length, algorithm));
		byte[] decryptedContent = decryption.doFinal(encryptedContent);
		//System.out.println(new String(decryptedContent));
		
		//return ByteBuffer.wrap(decryptedContent).getDouble();
		return decryptedContent;
	}
}
