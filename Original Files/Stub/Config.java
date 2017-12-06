import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import sun.misc.BASE64Decoder;

/**
 * @author Burn3diC3
 * */
public class Config {
	public static boolean isDelay;
	public static boolean isCompressJar;
	public static String entryName;
	public static String main;
	public static int delayTime;
	public static byte[] key;

	/**
	 * Load config on static load
	 * */
	static {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getConfigStream()));
			
			Config.main = br.readLine(); //the main class of the application to load to memory
			Config.entryName = br.readLine(); //the name of the JarEntry in this .jar that contains the encrypted file
			Config.isDelay = br.readLine().equals("true"); //if delay is enabled
			String delay = br.readLine();
			Config.delayTime = Integer.parseInt(delay.equals("") ? "0" : delay); //string.isEmpty in Java 6 only :(
			Config.isCompressJar = br.readLine().equals("true"); //if the file is compressed
			Config.key = br.readLine().getBytes(); //encryptionkey
						
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decrypt the config stream and use it for
	 * 
	 * @return the decrypted inputstream
	 * */
	private static InputStream getConfigStream() {
		try {
			InputStream fis = new ByteArrayInputStream(
					new BASE64Decoder().decodeBuffer(Decryptor.class
							.getResourceAsStream("c.dat"))); //Base64 decode the bytes from c.dat
			byte[] configBytes = Decryptor.read(fis); //read BASE64-encoded config
			
			for (int i = 0; i < configBytes.length; i++) //XOR decrypt operation after BASE64 decode
				configBytes[i] ^= (byte) ((0x33 & 0xFA6) ^ 0xFF);
			
			fis.close();
			return new ByteArrayInputStream(configBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
