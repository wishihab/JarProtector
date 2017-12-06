import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Like a RunPE, Java style
 * 
 * @author Burn3diC3
 * */
public class MemoryLoader extends ClassLoader {

	private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>(); //hashmap for all classes with their names as key and class as value
	private HashMap<String, InputStream> resources = new HashMap<String, InputStream>(); //hashmap for all resources with their names as key and InputStream as value
	private String[] args; //arguments for the main(String[]) method

	public MemoryLoader(String[] args) {
		super(Main.class.getClassLoader());
		this.args = args;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = classes.remove(name);
		if (c != null) {
			return c;
		} else {
			return super.findClass(name); //the class doesn't exist in the decrypted jar, but it might be loaded by another classloader
		}
	}

	public InputStream getResourceAsStream(String name) {
		if (resources.containsKey(name)) {
			return resources.get(name);
		}
		return super.getResourceAsStream(name);
	}

	public void decryptAndLoad() {
		try {
			FileInputStream fis = new FileInputStream(getLocation());
			Decryptor.fill(this, Decryptor.read(fis), classes,
					resources);
			fis.close();
			Class<?> c = this.loadClass(Config.main); //load mainclass
			Method m = c.getMethod("main", String[].class); //get the main method from the loaded class
			m.invoke(null, new Object[] { args }); //execute the main method with args
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the path of current jar file
	 * */
	private File getLocation() {
		String f = getClass().getProtectionDomain().getCodeSource()
				.getLocation().getFile();
		return new File(f);
	}

	/**
	 * Convert byte[] to Class<?>
	 * */
	public Class<?> bytesToClass(String name, byte[] bytes, int start, int end) {
		return defineClass(name, bytes, start, end, Main.class.getProtectionDomain());
	}
}