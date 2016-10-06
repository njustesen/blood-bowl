package sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URL;

public class BBSound {
	
	private URL url;

	private AudioClip ac;
	
	public BBSound (String filename){
		setSound(filename);
	}
	
	public void setSound (String fileName){
		
		if(fileName.endsWith(".wav") || fileName.endsWith(".au") || fileName.endsWith(".mid") ||
		   fileName.endsWith(".WAV") || fileName.endsWith(".AU") || fileName.endsWith(".MID")){	 //file-type test.
			try {	
				url = new File("media/sounds/"+fileName).toURI().toURL();					
				ac = Applet.newAudioClip(url);
			} catch (Exception e) {e.printStackTrace();}
		}else{throw new RuntimeException("wrong sound file format "+ this);}
	}
	
	public void start(){
			ac.play();
	}

	public void stop() {
		ac.stop();
	}
	
	public void loop() {
		ac.loop();
	}
}
