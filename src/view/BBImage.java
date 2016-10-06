package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
//import javax.swing.ImageIcon;
import javax.xml.crypto.dsig.Transform;

import main.Main;


/**
 * This class is for image handling and representation. 
 * @author Jacob Dinesen & Mads Hansen
 *
 */
public class BBImage{

	static final ClassLoader loader = Main.class.getClassLoader();
	
	/**
	 * The image-object of the class.
	 */
	BufferedImage image;
	//ImageIcon image;
	
	/**
	 * The default constructor - if this is used, the method setImage() 
	 * must be called in order to assign an image to the object.
	 */
	public BBImage(){
		
	}
	
	/**
	 * Constructor that takes another JRucImage as parameter.
	 * @param jrucimage	The image of this object is set as the new image
	 */
	public BBImage(BBImage spaceimage){	
		this.image = spaceimage.image;	
	}
	
	public BBImage(BufferedImage image){	
		this.image = image;	
	}
	/**
	 * Constructor that takes a String as parameter - the String must contain either 
	 * JPG, jpg, PNG, pgn, GIF, gif, BMP, bmp, BNM, or bnm in order for
	 * file to get loaded, and the image must be placed in a folder named "pictures"
	 * in the same place as your class-files.
	 * @param fileName The name of the image-file.
	 */
	public BBImage(String fileName){
		if(fileName.endsWith(".JPG") || fileName.endsWith(".PNG") || fileName.endsWith(".GIF") || fileName.endsWith(".BMP") || fileName.endsWith(".BNM") ||
		   fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".bnm")){	
				//String path = getClass().getClassLoader().getResource(".").getPath();		
			try {

				
				//System.out.println(new File("media/pictures/")
			    //.getCanonicalPath());
						
						image = ImageIO.read(new File("./media/pictures/"+fileName));
						
//						java.net.URL url = getClass().getResource("/pictures/"+fileName);
//						image = ImageIO.read(url);
//						image = new ImageIcon(url);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				
				}else{throw new RuntimeException("wrong image file format "+ this);}
	}

	/**
	 * Method that assigns an image to the J2DGImage-object - The input-String must end with either 
	 * JPG, jpg, PNG, pgn, GIF, gif, BMP, bmp, BNM, or bnm in order for
	 * file to get loaded, and the image must be placed in a folder named "pictures" in your project-folder.
	 * @param fileName The name of the image-file.
	 */
	public void setImage(String fileName){
		if(fileName.endsWith(".JPG") || fileName.endsWith(".PNG") || fileName.endsWith(".GIF") || fileName.endsWith(".BMP") || fileName.endsWith(".BNM") ||
		   fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".bnm")){	
			try {
				image = ImageIO.read(new File("./media/pictures/"+fileName));
				//	java.net.URL url = getClass().getResource("/pictures/"+fileName);
				//	image = ImageIO.read(url);
//					image = new ImageIcon(url);
				} catch (IOException e) {
					e.printStackTrace();
				}

		//		System.out.println("Image folder = "+getClass().getResource("media/pictures/"+fileName));	
		
		}else{throw new RuntimeException("wrong image file format "+ this);}
	}
	
	public void setImage(BBImage image){
		this.image = image.image;
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
	}
	/**
	 * Returns the image of this J2DGImage.
	 * @return image.
	 */
	public BufferedImage getBufferedImage(){
		//System.out.println("image returned");
		return image;
	}
	
	public BufferedImage getImage(){
		//System.out.println("image returned");
		return image;
	}
	/**
	 * Returns this J2DGImage-object.
	 * @return this J2DGImage-object.
	 */
	public BBImage getSpaceImage(){
		return this;
	}
	
	/**
	 * Returns the width of this J2DG-objects BufferedImage in pixels.
	 * @return the width of this J2DG-objects BufferedImage in pixels.
	 */
	public int getWidth(){
		return image.getWidth();
	}
	
	/**
	 * Returns the height of this J2DG-objects BufferedImage in pixels.
	 * @return the height of this J2DG-objects BufferedImage in pixels.
	 */
	public int getHeight(){
		return image.getHeight();
	}
	
	/**
	 * This method draws an image on top of another image.
	 * @param image The image that should be added to the existing picture.
	 * @param x The x-position of the new image.
	 * @param y The y-position of the new image.
	 */
	public void drawImage(BBImage image, int x, int y){
		Graphics2D g = null;
		g.setBackground(Color.BLACK);
		g.setColor(Color.WHITE);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		g.drawImage(image.getImage(), x, y, null);
		g.dispose();
	}
	
	/**
	 * Scales the size of an image.
	 * @param width The new width.
	 * @param height The new height.
	 */
	public void scale(int width, int height){
		if(width==image.getTileWidth() && height==image.getHeight())
			return;
		BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		Graphics2D g = null;
		g.setComposite(AlphaComposite.Src);
		g.drawImage(getImage(), 0, 0, width, height, null);
		g.dispose();
		image = scaled;
	}
	
	public BBImage rotate(double r){
		double rr = Math.toRadians(r);
		AffineTransform at = new AffineTransform();
		at.rotate(rr, image.getWidth() / 2, image.getHeight() / 2);
		
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
		newImage.createGraphics().drawImage(image, at, null);
		
		BBImage rotatedImage = new BBImage();
		rotatedImage.setImage(newImage);

		return rotatedImage;
	}
}