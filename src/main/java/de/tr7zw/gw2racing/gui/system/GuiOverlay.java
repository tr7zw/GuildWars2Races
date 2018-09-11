package de.tr7zw.gw2racing.gui.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.gui.elements.ImageButton;
import de.tr7zw.gw2racing.gui.elements.Label;
import de.tr7zw.gw2racing.tracks.Checkpoint;
import de.tr7zw.gw2racing.tracks.Race;
import de.tr7zw.gw2racing.tracks.TestTrackGenerator;
import de.tr7zw.gw2racing.util.math.ScreenUtils;
import de.tr7zw.gw2racing.util.math.Vector3;

public class GuiOverlay extends Screen {

    private BufferedImage arrow;
    private BufferedImage flag_start;
    private BufferedImage flag_checkpoint;
    private BufferedImage flag_end;
    private boolean debug = false;

    @Override
    public void init() {
	getGui().setMovable(false);
	if (debug) {
	    registerElement(new Label(70, 230, 2, Color.YELLOW,
		    () -> Gw2Racing.getInstance().getDataProvider().getSpeed() + "m/s "
			    + Gw2Racing.getInstance().getDataProvider().getX() + " "
			    + (int) Gw2Racing.getInstance().getDataProvider().getY() + " "
			    + Gw2Racing.getInstance().getDataProvider().getZ()));
	    registerElement(
		    new Label(70, 130, 2, Color.YELLOW, () -> Gw2Racing.getInstance().getDataProvider().getMapName()
			    + " " + Gw2Racing.getInstance().getDataProvider().getMap_id()));
	    registerElement(
		    new Label(70, 150, 2, Color.YELLOW, () -> Gw2Racing.getInstance().getDataProvider().getName()));
	}
	registerElement(new ImageButton(40, 165, 32, 32, "img/time.png", "img/time.png", () -> {
	    Gw2Racing.getInstance().setRace(new Race(TestTrackGenerator.getLATrack()));
	}));
	registerElement(new Label(75, 190, 2, Color.WHITE,
		() -> "Time : " + Gw2Racing.getInstance().getRace().getTime() / 1000d + "s"));
	registerElement(new ImageButton(10, 165, 32, 32, "img/x.png", "img/x_hover.png", () -> {
	    Gw2Racing.getInstance().shouldShutdown = true;
	}));
	this.getGui().changeSize(1920, 1080);
	setBackground(null);
	try {
	    this.arrow = ImageIO.read(ClassLoader.getSystemResource("img/bigArrow.png"));
	    this.flag_start = ImageIO.read(ClassLoader.getSystemResource("img/flag_start.png"));
	    this.flag_checkpoint = ImageIO.read(ClassLoader.getSystemResource("img/flag_checkpoint.png"));
	    this.flag_end = ImageIO.read(ClassLoader.getSystemResource("img/flag_end.png"));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    @Override
    public void draw(Graphics2D g2d, Point mouse) {
	super.draw(g2d, mouse);
	Race race = Gw2Racing.getInstance().getRace();
	if (race == null || race.getEndTime() != null)
	    return;
	if (race.getTrack().getMapId() != Gw2Racing.getInstance().getDataProvider().getMap_id())
	    return;
	drawCheckpoint(g2d, flag_start, race.getTrack().getStart());
	for (Checkpoint point : race.getTrack().getCheckpoints())
	    drawCheckpoint(g2d, flag_checkpoint, point);
	drawCheckpoint(g2d, flag_end, race.getTrack().getGoal());
	Checkpoint checkPoint = race.getTrack().getAllCheckpoints().get(race.getAtCheckpoint());
	Vector3 pointAt = checkPoint.getPointer();
	drawRotated(g2d, mouse, ScreenUtils.getRotation(pointAt.x, pointAt.z), 1920 / 2, 200, () -> {
	    g2d.drawImage(arrow, -64, -64, 128, 128, null);
	});
    }

    public void drawCheckpoint(Graphics2D g2d, BufferedImage texture, Checkpoint checkpoint) {
	Vector3 last = null;
	for (Vector3 flag : checkpoint.getFlags()) {
	    ScreenUtils.project(flag);
	    g2d.drawImage(texture, (int) flag.x, (int) flag.y, 32, 32, null);
	    // if (last != null)
	    // g2d.drawLine((int) flag.x, (int) flag.y, (int) last.x, (int) last.y);
	    if (flag.x < 5000)
		last = flag;
	}
    }

    public void drawRotated(final Graphics2D g2d, final Point mouse, double rotation, int posx, int posy,
	    Runnable draw) {
	AffineTransform transform = g2d.getTransform();
	g2d.translate(posx, posy);
	g2d.rotate(rotation);
	draw.run();
	g2d.setTransform(transform);
    }

}
