
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.swing.JOptionPane;


/**
 * Class with some cool methods that might be useful..
 * 
 * @author Burn3diC3
 * */
public class Utils {
	
	/**
	 * Get the main class from specified jar (JarInputStream)
	 * 
	 * @param jis - the JarInputStream to get the main class from
	 * 
	 * @return the main class from jis, or null if not found
	 * */
	public static String getMainClassFromJar(JarInputStream jis) {
		try {
			Manifest manifest = jis.getManifest(); //get manifest from jar
			if(manifest == null && jis.getNextJarEntry() == null) { //not valid jar file
				JOptionPane
				.showMessageDialog(
						GUI.instance,
						"Error! The input file is not a valid .jar file.",
						"Nope!", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			return manifest.getMainAttributes().getValue(
					Attributes.Name.MAIN_CLASS); //get main class from Manifest
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							GUI.instance,
							"Error! Could not find main-class of aplication! You'll have to specify it manually",
							"Nope!", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	/**
	 * Get all classes from a .jar (JarInputStream)
	 * 
	 * @param jis - the JarInputStream
	 * 
	 * @return a list of all classes found in jis
	 * */
	public static List<String> getClasses(JarInputStream jis) {
		try {
			List<String> list = new ArrayList<String>();
			JarEntry e;
			while ((e = jis.getNextJarEntry()) != null) {
				if (e.getName().toLowerCase().endsWith(".class"))
					list.add(e.getName().substring(0, e.getName().length() - 6)); //remove .class
			}
			return list;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(GUI.instance, "Error: " + e.getMessage(),
					"Nope!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generate a random name (only characters) from the specified length
	 * 
	 * @param len - the length of the name to generate
	 * 
	 * @return the generated name
	 * */
	public static String randomName(int len) {
		char[] a = new char[len];
		
		Random r = new Random();

		for (int i = 0; i < len; i++) {
			a[i] = ((char) ('a' + r.nextInt(26)));
		}
		return String.copyValueOf(a);
	}

	/**
	 * Generate an encryption key from the specified length
	 * 
	 * @param len - the length 
	 * 
	 * @return the generated key
	 * */
	public static String generateEncryptionKey(int len) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!\"#¤%&/()=\\?[]"
				.toCharArray();
		char[] ret = new char[len];
		
		SecureRandom sr = new SecureRandom();
		for (int i = 0; i < len; i++) {
			ret[i] = chars[sr.nextInt(chars.length)];
		}

		return String.copyValueOf(ret);
	}
}
