
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
 * @author Burn3diC3
 * Edited by wishihab 2017
 * */


public class Utils {
	
	
	public static String getKelasUtama(JarInputStream jumpa) {
		try {
			Manifest manifest = jumpa.getManifest(); 
			if(manifest == null && jumpa.getNextJarEntry() == null) { 
				JOptionPane
				.showMessageDialog(
						GUIS.instance,
						"Harus Jar Atuh.",
						"Nope!", JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			return manifest.getMainAttributes().getValue(
					Attributes.Name.MAIN_CLASS); //get main class from Manifest
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							GUIS.instance,
							"Gk Nemu MainClassnya",
							"Nope!", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static List<String> dapetKelas(JarInputStream jumpa) {
		try {
			List<String> list = new ArrayList<String>();
			JarEntry e;
			while ((e = jumpa.getNextJarEntry()) != null) {
				if (e.getName().toLowerCase().endsWith(".class"))
					list.add(e.getName().substring(0, e.getName().length() - 6)); //remove .class
			}
			return list;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(GUIS.instance, "Error: " + e.getMessage(),
					"Nope!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}

	
	public static String randomName(int len) {
		char[] a = new char[len];
		
		Random r = new Random();

		for (int i = 0; i < len; i++) {
			a[i] = ((char) ('a' + r.nextInt(26)));
		}
		return String.copyValueOf(a);
	}

	
	public static String generateEncryptionKey(int len) {
		char[] chars = "Wif3NDeRCr3ptErSWif3Cr3ptErSWif3NDeRCr3ptErNDeRCr3ptErSWif3NDeRCr3ptErSWif3NDeRCr3ptErS"
				.toCharArray();
		char[] ret = new char[len];
		
		SecureRandom sr = new SecureRandom();
		for (int i = 0; i < len; i++) {
			ret[i] = chars[sr.nextInt(chars.length)];
		}

		return String.copyValueOf(ret);
	}
}
