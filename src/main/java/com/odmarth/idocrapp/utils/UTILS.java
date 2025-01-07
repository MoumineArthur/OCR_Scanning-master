package com.odmarth.idocrapp.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class UTILS {
	private static final Logger LOG = LoggerFactory.getLogger(UTILS.class);




	public static File binarizeImage(File textImage) throws IOException {
		BufferedImage img = null;

		try {
			//Read in new image file
			img = ImageIO.read(textImage);    
		} 
		catch (IOException e){
			System.out.println("Error: "+e);
		}
		int h=img.getHeight();
		int w=img.getWidth();


		BufferedImage bufferedImage = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
		if (img == null) {
			System.out.println("No image loaded");
		} 
		else {
			for(int i=0;i<w;i++)
			{
				for(int j=0;j<h;j++)
				{


					//Get RGB Value
					int val = img.getRGB(i, j);
					//Convert to three separate channels
					int r = (0x00ff0000 & val) >> 16;
				int g = (0x0000ff00 & val) >> 8;
		int b = (0x000000ff & val);
		int m=(r+g+b);
		//(255+255+255)/2 =283 middle of dark and light
		if(m>=383)
		{
			// for light color it set white
			bufferedImage.setRGB(i, j, Color.WHITE.getRGB()); 
		}
		else{  
			// for dark color it will set black
			bufferedImage.setRGB(i, j, 0);
		}
				}  
			}
		}


		File file = new File("binarezed.jpg");
		ImageIO.write(bufferedImage, "jpg", file);
		return file;

	}

	public static File binarizeFile(File inputImage) throws Exception {
		try { 
			//File inputImage = new File("image.jpg"); 
			BufferedImage image = ImageIO.read(inputImage);

			for(int i=0; i<image.getHeight(); i++) { 
				for(int j=0; j<image.getWidth(); j++) { 
					Color color = new Color(image.getRGB(j, i));
					int red = (int)(color.getRed() * 0.299); 
					int green = (int)(color.getGreen() * 0.587); 
					int blue = (int)(color.getBlue() * 0.114); 
					Color newColor = new Color(red+green+blue, red+green+blue,red+green+blue);
					image.setRGB(j, i, newColor.getRGB());
				}
			} 

			File output = new File("newImage.jpg"); 
			ImageIO.write(image, "jpg", output); 
			return output;
		} 
		catch (Exception e) {
			throw new Exception("Error when binarize file ", e);
		} 

	}
}
