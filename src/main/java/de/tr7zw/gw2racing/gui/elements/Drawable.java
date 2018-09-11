package de.tr7zw.gw2racing.gui.elements;

import java.awt.Graphics2D;
import java.awt.Point;

public interface Drawable extends GuiElement {

    public void draw(Graphics2D g2d, Point mouse);

}
