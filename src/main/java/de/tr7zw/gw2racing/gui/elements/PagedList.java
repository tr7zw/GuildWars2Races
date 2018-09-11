package de.tr7zw.gw2racing.gui.elements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import de.tr7zw.gw2racing.Gw2DataProvider;
import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.tracks.Checkpoint;
import de.tr7zw.gw2racing.util.math.Vector3;

public class PagedList implements Drawable, Clickable {

    private BufferedImage bg_texture;
    private int x;
    private int y;
    private int elementsPerPage = 10;
    private int hightPerElement = 30;
    private int elementWidth = 209;
    private int page = 0;
    private ImageButton prev;
    private ImageButton next;
    private ImageButton add;
    private ArrayList<Checkpoint> lines = new ArrayList<>();
    private ArrayList<Label> labels = new ArrayList<>();
    private de.tr7zw.gw2racing.region.Point firstPoint;

    public PagedList(int x, int y) {
	this.x = x;
	this.y = y;
	try {
	    this.bg_texture = ImageIO.read(ClassLoader.getSystemResource("img/group_selection_background.png"));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	prev = new ImageButton(x, y + (elementsPerPage * hightPerElement), 32, 32, "img/left.png", "img/left_hover.png",
		() -> {
		    if (page > 0)
			page--;
		});
	next = new ImageButton(x + elementWidth - 30, y + (elementsPerPage * hightPerElement), 32, 32, "img/right.png",
		"img/right_hover.png", () -> {
		    if (page < 10)
			page++;
		});
	add = new ImageButton(x + (elementWidth / 2) - 30, y + (elementsPerPage * hightPerElement), 32, 32, "img/+.png",
		"img/+_hover.png", () -> {
		    de.tr7zw.gw2racing.region.Point p = new de.tr7zw.gw2racing.region.Point(
			    Gw2Racing.getInstance().getDataProvider().getX(),
			    Gw2Racing.getInstance().getDataProvider().getZ());
		    if (firstPoint == null) {
			firstPoint = p;
			return;
		    }
		    // addLine(new Checkpoint((float) firstPoint.x, firstPoint.x, (float)
		    // firstPoint.y, (float) p.x, p.y, (float) p.y));
		    firstPoint = null;
		});
	for (int i = 0; i < elementsPerPage; i++) {
	    final int fi = i;
	    labels.add(new Label(x + 10, y + 25 + (i * (hightPerElement - 1)), 2, Color.WHITE, () -> getText(fi)));
	}
    }

    public String getText(int id) {
	Gw2DataProvider pro = Gw2Racing.getInstance().getDataProvider();
	id += page * elementsPerPage;
	if (id < lines.size()) {
	    return lines.get(id).avg_dist(new Vector3(pro.getX(), pro.getY(), pro.getZ())) + "m";
	}
	return id + ".";
    }

    public void addLine(Checkpoint line) {
	lines.add(line);
    }

    @Override
    public void onClick(MouseEvent event) {
	prev.onClick(event);
	next.onClick(event);
	add.onClick(event);
    }

    @Override
    public void draw(Graphics2D g2d, Point mouse) {
	g2d.drawImage(bg_texture, x, y, elementWidth, elementsPerPage * hightPerElement, null);
	prev.draw(g2d, mouse);
	next.draw(g2d, mouse);
	add.draw(g2d, mouse);
	AffineTransform tran = g2d.getTransform();
	for (Label l : labels) {
	    l.draw(g2d, mouse);
	    g2d.setTransform(tran);
	}
    }

}
