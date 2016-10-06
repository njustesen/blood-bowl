package view;

import java.awt.image.BufferedImage;
import java.io.File;

public class BBAnimation implements Runnable{

	private BBImage[] animation;
	private String [] fileNames;
	private String folderPath;
	private BBImage currentImage;
	private boolean loop;
	private boolean running;
	private int updateRate;
	private int lastImage;
	private boolean animationPaused;
	private Thread aniThread;
	
	public BBAnimation (String folderName, boolean loop, int updateRate){
		
		this.updateRate = updateRate;
		this.loop = loop;
		folderPath = "media/pictures/"+folderName;
		File folder = new File(folderPath);
	    File[] listOfFiles = folder.listFiles();
	    System.out.println("listOfFiles.length = "+listOfFiles.length);
	    animation = new BBImage [listOfFiles.length];  
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  animation[i] = new BBImage(folderName +"/"+ listOfFiles[i].getName());
	    	  
	      } else if (listOfFiles[i].isDirectory()) {
	        
	      }
	    }
	   currentImage = animation[0];
	}
	
	public void setUpdateRate(int newUpdateRate){
		updateRate = newUpdateRate;
	}
	
	public int getUpdateRate(){
		return updateRate;
	}
	
	public void loopAnimation(){
		if(!running){
			System.out.println("new Thread created");
		Thread aniThread = new Thread(this);
		aniThread.start();
		}
	}
	
	@Override
	public void run() {
		running = true;
		System.out.println("RUN CALLED");
			for(int i = lastImage; i <= animation.length; i++){
				currentImage = animation[lastImage];
				try {
					Thread.sleep(1000/updateRate);
				} catch (InterruptedException e) {e.printStackTrace();}
				if(i >= animation.length-1)
					if(loop)
						i = -1;
				if(animationPaused){
					System.out.println("paused");
					i--;
				}else{
					lastImage = i+1;
				}
			}
		running = false;
	}
	
	public BBImage getBBImage(){
		return currentImage;	
	}
	
	public BufferedImage getBufferedImage(){
		return currentImage.getBufferedImage();	
	}
	
	public void pauseAnimation(){
		animationPaused = true;
	}
	
	public void unpauseAnimation(){
		animationPaused = false;
	}
	
}
