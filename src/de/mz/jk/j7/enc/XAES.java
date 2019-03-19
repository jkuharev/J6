package de.mz.jk.j7.enc;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is basically for encryption and obfuscation of short text blocks
 * using AES/CBC/PKCS5Padding encryption algorithm, 
 * e.g. passwords in DB or properties files. 
 * 
 * originally taken from
 * 	https://stackoverflow.com/questions/1132567/encrypt-password-in-configuration-files/1133815#1133815
 * 
 * @author J.Kuharev
 */
public class XAES 
{
	private static int iterationCount = 40000, keyLength = 128;
	
	public static void main(String[] args) throws Exception
	{
		String pass = "password";
		String salt = "secure";
		String text = "very short text";
		String enc = encrypt(text, pass, salt);
		String dec = decrypt(text, pass, salt);
		System.out.println( enc );
		System.out.println( dec );
	}
	
	public static String encrypt(String plainText, String secret, String salt) throws Exception
	{
		SecretKeySpec key = createSecretKey(secret.toCharArray(), salt.getBytes(), iterationCount, keyLength);
		String result = encrypt(plainText, key);
		return result;
	}
	
	public static String decrypt(String encryptedText, String secret, String salt) throws Exception
	{
		SecretKeySpec key = createSecretKey(secret.toCharArray(), salt.getBytes(), iterationCount, keyLength);
		String result = decrypt(encryptedText, key);
		return result;
	}

    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
}