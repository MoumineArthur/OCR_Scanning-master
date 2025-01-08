package com.odmarth.idocrapp.services;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;

@Component
public class OCRScanning {
	private final static Logger LOG = LoggerFactory.getLogger(OCRScanning.class);
	
	public static String  readTextFromImage(File imageToRead) throws Exception {
		LOG.info("INITIALISATION OCRScanning.....");
		Tesseract tesseract = new  Tesseract();
		tesseract = new  Tesseract();		
		File tessDataFolder = LoadLibs.extractTessResources("tessdata"); 
		tesseract.setDatapath(tessDataFolder.getPath());
		tesseract.setLanguage("eng");
		
		//tesseract.setPageSegMode(1);
		tesseract.setOcrEngineMode(1);
		return tesseract.doOCR(processImageResolution(imageToRead));
	}
	
	private  static BufferedImage processImageResolution(File orignalImage) throws IOException {
		BufferedImage image = ImageIO.read(orignalImage);
        
        int targetDPI = 500; 
        double scalingFactor = targetDPI / 72.0; 
       
        int newWidth = (int) (image.getWidth() * scalingFactor);
        int newHeight = (int) (image.getHeight() * scalingFactor);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        
        g2d.dispose();
        
     //   File outputImageFile = new File("g:\\resized_image.jpg");
      //  ImageIO.write(resizedImage, "jpg", outputImageFile);
        
        return resizedImage;
	}
}
