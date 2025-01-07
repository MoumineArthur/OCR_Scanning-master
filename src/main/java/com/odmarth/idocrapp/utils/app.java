package com.odmarth.idocrapp.utils;
import java.awt.image.*;
import java.awt.*;
import java.net.*;

public class app extends java.applet.Applet {
	Image GifOriginalWithWithBlueBackground;
	Image GifModifiedWithTransparentBackground;

	public void init() {
		setBackground(new Color(0).white);  

		MediaTracker media = new MediaTracker(this);
		// image of our friend, Gumby with a blue background
		GifOriginalWithWithBlueBackground = 
				getImage(getDocumentBase(),"gumbyblu.gif");
		media.addImage(GifOriginalWithWithBlueBackground,0);
		try {
			media.waitForID(0);
			GifModifiedWithTransparentBackground = 
					Transparency.makeColorTransparent
					(GifOriginalWithWithBlueBackground, new Color(0).blue);
		} 
		catch(InterruptedException e) {}
	}

	public void paint(Graphics g) {
		g.drawImage(GifOriginalWithWithBlueBackground, 10,10,this);
		g.drawImage(GifModifiedWithTransparentBackground,10, 80,this);
	}


}