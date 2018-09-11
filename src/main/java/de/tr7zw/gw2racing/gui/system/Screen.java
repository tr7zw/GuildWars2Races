package de.tr7zw.gw2racing.gui.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.gui.OverlayInterface;
import de.tr7zw.gw2racing.gui.elements.Clickable;
import de.tr7zw.gw2racing.gui.elements.Drawable;
import de.tr7zw.gw2racing.gui.elements.GuiElement;
import de.tr7zw.gw2racing.gui.elements.ImageButton;

public abstract class Screen {

    private static OverlayInterface inter = Gw2Racing.getInstance().getGui();
    private HashSet<GuiElement> elements = new HashSet<>();
    private HashSet<Drawable> drawable = new HashSet<>();
    private HashSet<Clickable> clickable = new HashSet<>();
    private BufferedImage background = setBackground("img/background.png");

    public Screen() {
	init();
    }

    public BufferedImage setBackground(String name) {
	if (name == null) {
	    background = null;
	    return null;
	}
	try {
	    background = ImageIO.read(ClassLoader.getSystemResource(name));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return background;
    }

    public abstract void init();

    public void addCloseButton() {
	registerElement(new ImageButton(Gw2Racing.getInstance().getGui().getWidth() - 40, 5, 32, 32, "img/x.png",
		"img/x_hover.png", () -> {
		    Gw2Racing.getInstance().shouldShutdown = true;
		}));
    }

    public void registerElement(GuiElement element) {
	elements.add(element);
	if (element instanceof Drawable)
	    drawable.add((Drawable) element);
	if (element instanceof Clickable)
	    clickable.add((Clickable) element);
    }

    public void onClick(MouseEvent event) {
	for (Clickable c : clickable)
	    c.onClick(event);
    }

    public void draw(Graphics2D g2d, Point mouse) {
	g2d.setColor(new Color(128, 128, 128, 0));
	g2d.fillRect(0, 0, inter.getWidth(), inter.getHeight());
	if (background != null)
	    g2d.drawImage(background, 0, 0, inter.getWidth(), inter.getHeight(), null);
	g2d.setColor(new Color(128, 128, 128, 250));
	AffineTransform transform = g2d.getTransform();
	for (Drawable d : drawable) {
	    d.draw(g2d, mouse);
	    g2d.setTransform(transform);
	}
    }

    public OverlayInterface getGui() {
	return inter;
    }

}
