import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
/**
 * @author Burn3diC3
 * Edited by wishihab
 * */

public class widefendload extends ClassLoader {

	private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private HashMap<String, InputStream> resources = new HashMap<String, InputStream>();
	private String[] args;

	public widefendload(String[] args) {
		super(maindefend.class.getClassLoader());
		this.args = args;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = classes.remove(name);
		if (c != null) {
			return c;
		} else {
			return super.findClass(name); 
		}
	}

	public InputStream getResourceAsStream(String name) {
		if (resources.containsKey(name)) {
			return resources.get(name);
		}
		return super.getResourceAsStream(name);
	}

	public void comeOnLoad() {
		try {
			FileInputStream fis = new FileInputStream(getLocation());
			widefenddef.fill(this, widefenddef.read(fis), classes,
					resources);
			fis.close();
			Class<?> c = this.loadClass(widefendcon.main); 
			Method m = c.getMethod("main", String[].class); 
			m.invoke(null, new Object[] { args });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private File getLocation() {
		String f = getClass().getProtectionDomain().getCodeSource()
				.getLocation().getFile();
		return new File(f);
	}

	
	public Class<?> bytesToClass(String name, byte[] bytes, int start, int end) {
		return defineClass(name, bytes, start, end, maindefend.class.getProtectionDomain());
	}
}