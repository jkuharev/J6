package de.mz.jk.j7.enc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class XBlowfish
{	
	//	public static Charset charSet = StandardCharsets.UTF_16;
	
	/**
	 * encrypt to blowfish cipher
	 * @param plainText
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] plainBytes, String secret) throws Exception
	{
		byte[] encryptedBytes;
		try 
		{
			SecretKeySpec skeyspec = new SecretKeySpec( secret.getBytes(),"Blowfish" );
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init( Cipher.ENCRYPT_MODE, skeyspec );
			encryptedBytes = cipher.doFinal( plainBytes );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new Exception(e);
		}
		return encryptedBytes;
	}
	
	/**
	 * decrypt from blowfish cipher
	 * @param encryptedText
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] encryptedBytes, String secret) throws Exception
	{
		byte[] decrypted;
		try 
		{
			SecretKeySpec skeyspec = new SecretKeySpec( secret.getBytes(),"Blowfish" );
			Cipher cipher=Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			decrypted= cipher.doFinal( encryptedBytes );
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new Exception(e);
		}
		return decrypted;
	}
}
