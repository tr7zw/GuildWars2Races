package de.tr7zw.gw2racing.gui.elements;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ImageButton implements Drawable, Clickable {

    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage texture;
    private BufferedImage mouseoverTexture;
    private Runnable onClick;

    public ImageButton(int x, int y, int width, int height, String texture, String mouseoverTexture, Runnable onClick) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.onClick = onClick;
	try {
	    this.texture = ImageIO.read(ClassLoader.getSystemResource(texture));
	    this.mouseoverTexture = ImageIO.read(ClassLoader.getSystemResource(mouseoverTexture));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Override
    public void draw(Graphics2D g2d, Point mouse) {
	boolean over = false;
	if (mouse != null) {
	    if (mouse.x > x && mouse.x < x + width && mouse.y > y && mouse.y < y + height) {
		over = true;
	    }
	}
	g2d.drawImage(over ? mouseoverTexture : texture, x, y, width, height, null);
    }

    @Override
    public void onClick(MouseEvent event) {
	Point mouse = event.getPoint();
	boolean over = false;
	if (mouse != null) {
	    if (mouse.x > x && mouse.x < x + width && mouse.y > y && mouse.y < y + height) {
		over = true;
	    }
	}
	if (over) {
	    onClick.run();
	}
    }

}
