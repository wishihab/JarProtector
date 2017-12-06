import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
/**
 * @author Burn3diC3
 * Edited by wishihab
 * */

public class widefenddef {

	
	public static void fill(widefendload loader, byte[] jarBytes, HashMap<String, Class<?>> classes,
			HashMap<String, InputStream> resources) {
		try {
			JarInputStream jus = new JarInputStream(new ByteArrayInputStream(
					jarBytes));
			JarEntry e;
			while ((e = jus.getNextJarEntry()) != null) {
				String name = e.getName();
				byte[] bytes = read(jus);
				
				if (bytes != null) {
					if (name.equals(widefendcon.entryName)) {
						fillEntry(loader, bytes, classes, resources, true, widefendcon.iniKompressjar);
					}
					else if (name.endsWith(".jar"))
						fillEntry(loader, bytes, classes, resources, false, false);
					else if (name.endsWith(".class")); 
					else
						resources.put(name, new ByteArrayInputStream(bytes));
				}
			}
			jus.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static void fillEntry(widefendload loader, byte[] jarBytes, HashMap<String, Class<?>> classes,
			HashMap<String, InputStream> resources, boolean iniKena, boolean iniKomp) {
		try {
			if (iniKena) 
				jarBytes = decrypt(jarBytes, widefendcon.kunci); //decrypt it
			
			JarInputStream jus;
			if(iniKomp) { 
				jus = new JarInputStream(new GZIPInputStream(new ByteArrayInputStream(jarBytes)));
			} else {
				jus = new JarInputStream(new ByteArrayInputStream(jarBytes)); 
			}
			JarEntry e;
			while ((e = jus.getNextJarEntry()) != null) {
				byte[] entryBytes = read(jus);
				String name = e.getName();

				if (entryBytes != null) {
					if (name.toLowerCase().endsWith(".class")) {
						String nameWithoutClass = name.substring(0, name.length() - 6)
								.replace("/", ".");
						classes.put(nameWithoutClass, loader.bytesToClass(nameWithoutClass, entryBytes, 0, entryBytes.length));
					} else if (name.endsWith(".jar")) { 
						fillEntry(loader, entryBytes, classes, resources, false, false);
					} else {
						resources.put(name, new ByteArrayInputStream(entryBytes));
					}
				}
			}

			jus.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static byte[] decrypt(byte[] enc, byte[] keybytes)  {
		byte[] decrypted = enc.clone();
		for(int i = 0, keyIndex = 0; i < decrypted.length; i++) {
			decrypted[i] ^= keybytes[keyIndex++];
			if(keyIndex == keybytes.length) {
				keyIndex = 0;
			}
		}
		return decrypted;
	}

	
	public static byte[] read(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			int n;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((n = is.read(buffer)) != -1) { //read from stream until end
				baos.write(buffer, 0, n);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
