package de.tr7zw.gw2racing.gui.elements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.function.Supplier;

public class Label implements Drawable {

    private int x;
    private int y;
    private double size;
    private Supplier<String> text;
    private Color color;

    public Label(int x, int y, double size, Color color, Supplier<String> text) {
	this.x = x;
	this.y = y;
	this.size = size;
	this.text = text;
	this.color = color;
    }

    @Override
    public void draw(Graphics2D g2d, Point mouse) {
	g2d.scale(size, size);
	g2d.setColor(color);
	g2d.translate(-x * (size - 1) / size, -y * (size - 1) / size);
	g2d.drawString(text.get(), x, y);
    }

}
