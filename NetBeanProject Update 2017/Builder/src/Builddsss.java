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
 * Edited by wishihab 2017
 * */

public class Builddsss {
	public static final int KEY_SIZE = 16;

	public static void bangunKeluar(String from, String to,
			boolean onlyClass, String config, byte[] key,
			String randomJarEntry, boolean compressJar) {
		try {
			File savedfile = new File(to);
			File toAsal = new File(from);

			File stab = new File("asalbaru.jar");

			if(!stab.exists()) {
				JOptionPane.showMessageDialog(GUIS.instance, "asalbaru.jar", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			JarInputStream jumpa = new JarInputStream(
					new FileInputStream(stab));
			FileOutputStream fosa = new FileOutputStream(savedfile);
			JarOutputStream ayo = new JarOutputStream(fosa, jumpa.getManifest());

			JarEntry e;
			while ((e = jumpa.getNextJarEntry()) != null) { 
				ayo.putNextEntry(e);
				ayo.write(read(jumpa));
				ayo.flush();
				ayo.closeEntry();
			}

			ayo.putNextEntry(new JarEntry(randomJarEntry)); 
			if (onlyClass) { 
				byte[] a = dapetBaitDariSemua(toAsal);
				if(compressJar) { 
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					GZIPOutputStream gaw = new GZIPOutputStream(os);
					gaw.write(a);
					gaw.close();

					a = os.toByteArray();
				}

				byte[] bytes = encrypt(a, key);
				ayo.write(bytes);

				ayo.closeEntry();

				
				JarInputStream plain = new JarInputStream(new FileInputStream(
						toAsal));

				while ((e = plain.getNextJarEntry()) != null) {
					if (e.getName().endsWith(".class")) { 
					} else if (e.isDirectory()) {
					} else {
						ayo.putNextEntry(e);
						ayo.write(read(plain));
						ayo.closeEntry();
					}
				}
				plain.close();
			}

			else {
				byte[] jarBytes = read(new FileInputStream(toAsal));
				if(compressJar) {
					ByteArrayOutputStream compressedByteStream = new ByteArrayOutputStream();
					GZIPOutputStream gaw = new GZIPOutputStream(compressedByteStream); 
					gaw.write(jarBytes); 
					gaw.close();

					jarBytes = compressedByteStream.toByteArray();
				}

				byte[] encryptedJarBytes = encrypt(jarBytes, key);
				ayo.write(encryptedJarBytes);
				ayo.closeEntry();
			}

			ayo.putNextEntry(new JarEntry("cd.dat"));
			ayo.write(encryptConfig(config));
			ayo.flush();
			ayo.close();
			JOptionPane.showMessageDialog(GUIS.instance, "WEW successfully!",
					"Done!", JOptionPane.INFORMATION_MESSAGE);
		} catch (FileNotFoundException fe) {
			JOptionPane.showMessageDialog(GUIS.instance, "File not found!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			fe.printStackTrace();
		} catch (Exception ee) {
			JOptionPane.showMessageDialog(GUIS.instance, "An exception has occured: "
					+ ee.getMessage() + " ", "Error!",
					JOptionPane.ERROR_MESSAGE);
			ee.printStackTrace();
		}
	}

	
	private static byte[] dapetBaitDariSemua(File f) {
		byte[] buffer = new byte[1024];
		int n;

		ByteArrayOutputStream baso = new ByteArrayOutputStream();
		try {
			JarInputStream jumpa = new JarInputStream(new FileInputStream(
					f));
			JarEntry e;
			JarOutputStream ayo = new JarOutputStream(baso);
			while ((e = jumpa.getNextJarEntry()) != null) {
				if (e.getName().toLowerCase().endsWith(".class")) { //it's a class!
					ayo.putNextEntry(e);

					while ((n = jumpa.read(buffer)) != -1) {
						ayo.write(buffer, 0, n);
						ayo.flush();
					}
				}
			}
			jumpa.close();
			ayo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baso.toByteArray();
	}

	
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

	
	private static byte[] encryptConfig(String str) {
		byte[] b = str.getBytes();
		byte[] ret = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			ret[i] = (byte) (b[i] ^ ((0x33 & 0xFA6) ^ 0xFF));
		}
		return new BASE64Encoder().encode(ret).getBytes();
	}

	
	private static byte[] read(InputStream is) {
		try {
			byte[] buffer = new byte[1024];
			int n;
			ByteArrayOutputStream baso = new ByteArrayOutputStream();
			while ((n = is.read(buffer)) != -1) {
				baso.write(buffer, 0, n);
				baso.flush();
			}
			return baso.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
