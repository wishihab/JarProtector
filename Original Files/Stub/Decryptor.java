import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Burn3diC3
 * */
public class Decryptor {

	/**
	 * Add all classes and resources from the specified jar file (in bytes) into maps
	 * @param loader - the loader that will convert byte[] to Class<?>
	 * @param jarBytes - the bytes from the current file
	 * @param classes - the map to fill classes
	 * @param resources - the map to fill resources
	 * */
	public static void fill(MemoryLoader loader, byte[] jarBytes, HashMap<String, Class<?>> classes,
			HashMap<String, InputStream> resources) {
		try {
			JarInputStream jis = new JarInputStream(new ByteArrayInputStream(
					jarBytes));
			JarEntry e;
			while ((e = jis.getNextJarEntry()) != null) {
				String name = e.getName();
				byte[] bytes = read(jis);
				
				if (bytes != null) {
					if (name.equals(Config.entryName)) {
						fillEntry(loader, bytes, classes, resources, true, Config.isCompressJar);
					}
					else if (name.endsWith(".jar"))
						fillEntry(loader, bytes, classes, resources, false, false);
					else if (name.endsWith(".class")); //do nothing, it's the stub's classes and these should not be loaded multiple times
					else
						resources.put(name, new ByteArrayInputStream(bytes));
				}
			}
			jis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a .jars class bytes, create a class from bytes, and add it into the classes (and resources)
	 * 
	 * @param loader - the classloader that will convert bytes to Class<?>
	 * @param jarBytes - the jarbytes
	 * @param classes - hashmap with class name as key and the class as value
	 * @param resources - hashmap with resource name as key and the inputstream as value
	 * @param isEncrypted - if true, then this function will decrypt the jarBytes
	 * @param isCompressed - if true, then this function will decompress the jarBytes after decrypting
	 * */
	private static void fillEntry(MemoryLoader loader, byte[] jarBytes, HashMap<String, Class<?>> classes,
			HashMap<String, InputStream> resources, boolean isEncrypted, boolean isCompressed) {
		try {
			if (isEncrypted) //jarBytes is encrypted
				jarBytes = decrypt(jarBytes, Config.key); //decrypt it
			
			JarInputStream jis;
			if(isCompressed) { //the .jar is compressed, compress it
				jis = new JarInputStream(new GZIPInputStream(new ByteArrayInputStream(jarBytes))); //compressed inputstream
			} else {
				jis = new JarInputStream(new ByteArrayInputStream(jarBytes)); //not compressed, use the plain bytes
			}
			JarEntry e;
			while ((e = jis.getNextJarEntry()) != null) {
				byte[] entryBytes = read(jis);
				String name = e.getName();

				if (entryBytes != null) {
					if (name.toLowerCase().endsWith(".class")) {
						String nameWithoutClass = name.substring(0, name.length() - 6)
								.replace("/", ".");
						classes.put(nameWithoutClass, loader.bytesToClass(nameWithoutClass, entryBytes, 0, entryBytes.length));
					} else if (name.endsWith(".jar")) { //new .jar, most likely a library
						fillEntry(loader, entryBytes, classes, resources, false, false);
					} else {
						resources.put(name, new ByteArrayInputStream(entryBytes));
					}
				}
			}

			jis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decrypt the specified bytes with the specified key (using XOR)
	 * 
	 * WARNING! Changing this will require the encryption method to be changed in the builder
	 * 
	 * @param enc - the bytes to decrypt
	 * @param keybytes - the key (in bytes)
	 * 
	 * @return the decrypted bytes
	 * */
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

	/**
	 * Read all bytes from an InputStream
	 * 
	 * @param is - the InputStream to read from
	 * 
	 * @return the bytes read from the InputStream
	 * */
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
