package com.odmarth.idocrapp;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.odmarth.idocrapp.models.IDCardRectoBean;
import com.odmarth.idocrapp.models.IDCardVersoBean;
import com.odmarth.idocrapp.services.OCRDataExtractor;
import com.odmarth.idocrapp.services.OCRScanning;
import com.odmarth.idocrapp.services.OCRValueExtractor;

import net.sourceforge.tess4j.TesseractException;

@Configuration
@ComponentScan("com.odmarth.idocrapp")
public class ODMArthOcrScanning {
	private final static Logger LOG = LoggerFactory.getLogger(ODMArthOcrScanning.class);
	
	
	@Bean
	public ODMArthOcrScanning config() {
		return new ODMArthOcrScanning();
	}
	
	public static void main(String[] args) throws Exception {
		//File recto = new File("/opt/recto.jpg");
		//File verso = new File("/opt/verso.jpg");
		
		File recto = new File("c:\\recto.jpg");
		File verso = new File("c:\\verso.jpg");
		
		
		System.out.println("Starting syst ocr app...");
		
		try {
			String rectoText = OCRScanning.readTextFromImage(recto);
			System.out.println("RECTO OCR TEXT ==================\n"+ rectoText);
			
			IDCardRectoBean rectoBean = OCRDataExtractor.extractRectoBean(rectoText);
			
			System.out.println("RECTO bean \n" + rectoBean.getFirstName());
			
			System.out.println("======================================================");
//			
			String versoText = OCRScanning.readTextFromImage(verso);
//			
			System.out.println("VERSO OCR TEXT ==================\n"+ versoText);
			IDCardVersoBean versoBean = OCRDataExtractor.extractVersoBean(versoText);
//			
			System.out.println("VERSO bean \n" + versoBean.toString());
			
		} catch (TesseractException e) {
			e.printStackTrace();
		}
		
		LOG.info("Starting ocr app...");
	}
	
}
