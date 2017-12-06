import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import sun.misc.BASE64Encoder;

/**
 * @author Burn3diC3
 * */
public class JarBuilder {
	public static final int KEY_SIZE = 16;

	/**
	 * Build output
	 * 
	 * @param from - the file to encrypt
	 * @param to - the output file
	 * @param onlyClass - true if only classes should be encrypted
	 * @param config - the config to write
	 * @param key - encryptionkey
	 * @param randomJarEntry - the random jar entry to write the encrypted data to
	 * @param compressJar - true if the jar should be compressed
	 * */
	public static void buildOutput(String from, String to,
			boolean onlyClass, String config, byte[] key,
			String randomJarEntry, boolean compressJar) {
		try {
			File savefile = new File(to);
			File toEncrypt = new File(from);

			File stub = new File("Stub.jar");

			if(!stub.exists()) {
				JOptionPane.showMessageDialog(GUI.instance, "Stub.jar must be in the current directory!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			JarInputStream jis = new JarInputStream(
					new FileInputStream(stub));
			FileOutputStream fos = new FileOutputStream(savefile);
			JarOutputStream jos = new JarOutputStream(fos, jis.getManifest());

			JarEntry e;
			while ((e = jis.getNextJarEntry()) != null) { ///copy the stub to output
				jos.putNextEntry(e);
				jos.write(read(jis));
				jos.flush();
				jos.closeEntry();
			}

			jos.putNextEntry(new JarEntry(randomJarEntry)); ///add encrypted .jar entry
			if (onlyClass) { //only encrypt classes
				byte[] a = getBytesFromAllClasses(toEncrypt);
				if(compressJar) { //compress jar before encrypting
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					GZIPOutputStream goz = new GZIPOutputStream(os);
					goz.write(a);
					goz.close();

					a = os.toByteArray();
				}

				byte[] bytes = encrypt(a, key);
				jos.write(bytes);

				jos.closeEntry();

				///copy the of the .jar to encrypt (only classes have been copied so far)
				JarInputStream plain = new JarInputStream(new FileInputStream(
						toEncrypt));

				while ((e = plain.getNextJarEntry()) != null) {
					if (e.getName().endsWith(".class")) { //classes have already been copied and encrypted
					} else if (e.isDirectory()) {
					} else {
						jos.putNextEntry(e);
						jos.write(read(plain));
						jos.closeEntry();
					}
				}
				plain.close();
			}

			else {
				byte[] jarBytes = read(new FileInputStream(toEncrypt));
				if(compressJar) {
					ByteArrayOutputStream compressedByteStream = new ByteArrayOutputStream();
					GZIPOutputStream goz = new GZIPOutputStream(compressedByteStream); //compressed stream
					goz.write(jarBytes); ///os now contained compressed bytes of the .jar
					goz.close();

					jarBytes = compressedByteStream.toByteArray();
				}

				byte[] encryptedJarBytes = encrypt(jarBytes, key);
				jos.write(encryptedJarBytes);
				jos.closeEntry();
			}

			jos.putNextEntry(new JarEntry("c.dat"));
			jos.write(encryptConfig(config));
			jos.flush();
			jos.close();
			JOptionPane.showMessageDialog(GUI.instance, "Jar protected successfully!",
					"Done!", JOptionPane.INFORMATION_MESSAGE);
		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(GUI.instance, "File not found!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			fe.printStackTrace();
		} catch (Exception ee) {
			JOptionPane.showMessageDialog(GUI.instance, "An exception has occured: "
					+ ee.getMessage() + " ", "Error!",
					JOptionPane.ERROR_MESSAGE);
			ee.printStackTrace();
		}
	}

	/**
	 * Get bytes from all classes in the jar file f
	 * 
	 * @param f - the jar file to get all classes and their bytes from
	 * 
	 * @return - a .jar (as byte array) containing only classes from the file f
	 * */
	private static byte[] getBytesFromAllClasses(File f) {
		byte[] buffer = new byte[1024];
		int n;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JarInputStream jis = new JarInputStream(new FileInputStream(
					f));
			JarEntry e;
			JarOutputStream jos = new JarOutputStream(baos);
			while ((e = jis.getNextJarEntry()) != null) {
				if (e.getName().toLowerCase().endsWith(".class")) { //it's a class!
					jos.putNextEntry(e);

					while ((n = jis.read(buffer)) != -1) {
						jos.write(buffer, 0, n);
						jos.flush();
					}
				}
			}
			jis.close();
			jos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}

	/**
	 * Encrypt the specified bytes with the specified key
	 * 
	 * @param bytes - the bytes to encrypt
	 * @param key - the encryptionkey
	 * 
	 * @return the encrypted bytes
	 * */
	private static byte[] encrypt(byte[] bytes, byte[] key) {
		byte[] encrypted = bytes.clone();
		for(int i = 0, keyIndex = 0; i < encrypted.length; i++) {
			encrypted[i] ^= key[keyIndex++];
			if(keyIndex == key.length) {
				keyIndex = 0;
			}
		}
		return encrypted;
	}

	/**
	 * Encrypt the specified config for writing
	 * 
	 * @param str - config
	 * 
	 * @return the encrypted config (in bytes)
	 * */
	private static byte[] encryptConfig(String str) {
		byte[] b = str.getBytes();
		byte[] ret = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			ret[i] = (byte) (b[i] ^ ((0x33 & 0xFA6) ^ 0xFF));
		}
		return new BASE64Encoder().encode(ret).getBytes();
	}

	/**
	 * Read all bytes from specified InputStream
	 * 
	 * @param is - the InputStream
	 * 
	 * @return all bytes read (null if error)
	 * */
	private static byte[] read(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			int n;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((n = is.read(buffer)) != -1) {
				baos.write(buffer, 0, n);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
