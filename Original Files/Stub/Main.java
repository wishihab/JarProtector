/**
 * @author Burn3diC3
 * */
public class Main {
	public static void main(String[] args) throws InterruptedException  {
		if(Config.isDelay) //delay loading, sleeeepppppppp
			Thread.sleep(Config.delayTime * 1000); //delay time is in seconds, sleep() is in milliseconds
		new MemoryLoader(args).decryptAndLoad();
	}
}
