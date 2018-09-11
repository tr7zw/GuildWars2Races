package de.tr7zw.gw2racing.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.gui.system.LoadingScreen;
import de.tr7zw.gw2racing.gui.system.Screen;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author tr7zw
 */
@SuppressWarnings("serial")
public class PaintGui extends javax.swing.JFrame implements OverlayInterface {

    public PaintGui INSTANCE;
    private Gw2Racing main;
    public Screen currentScreen;
    @Getter
    @Setter
    private boolean movable = true;

    public PaintGui(Gw2Racing main) {
	this.main = main;
	this.INSTANCE = this;
    }

    public void initComponents() {
	jPanel1 = new PaintPane();
	setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	setAlwaysOnTop(true);

	setTitle("Guild Wars 2 Open Races");
	setIconImage(Toolkit.getDefaultToolkit()
		.getImage(ClassLoader.getSystemResource("img/Guild_Wars_2_Dragon_logo.jpg")));
	try { // Removes the normal cursor TODO: This should be done better
	    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("null").getImage(), new Point(0, 0),
		    "custom cursor"));
	} catch (Exception ex) {
	}
	setUndecorated(true);
	setBackground(new java.awt.Color(51, 51, 51, 0));
	addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
	    public void mouseDragged(java.awt.event.MouseEvent evt) {
		if (movable)
		    formMouseDragged(evt);
	    }

	});
	addMouseListener(new java.awt.event.MouseAdapter() {
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
		jPanel1.onClick(evt);
	    }

	    public void mousePressed(java.awt.event.MouseEvent evt) {
		formMousePressed(evt);
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		showCursor = true;
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		showCursor = false;
	    }

	});

	javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	jPanel1.setLayout(jPanel1Layout);

	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	getContentPane().setLayout(layout);
	layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
		jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
		javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
	layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
		jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
		javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		main.shouldShutdown = true;
	    }
	});

	pack();
	currentScreen = new LoadingScreen();
    }

    boolean showCursor = true;

    public void changeSize(int x, int y) {
	INSTANCE.setSize(x, y);
	jPanel1.setSize(x, y);
    }

    private void formMouseDragged(java.awt.event.MouseEvent evt) {
	if (offsetX == -1 || offsetY == -1)
	    return;
	this.setLocation(evt.getXOnScreen() - offsetX, evt.getYOnScreen() - offsetY);
    }

    private int offsetX = 0;
    private int offsetY = 0;

    private void formMousePressed(java.awt.event.MouseEvent evt) {
	if (evt.getButton() == 1) {
	    offsetX = evt.getX();
	    offsetY = evt.getY();
	} else {
	    offsetX = -1;
	    offsetY = -1;
	}
    }

    public class PaintPane extends JPanel {

	private BufferedImage cursor;

	public PaintPane() {
	    setOpaque(false);
	    try {
		cursor = ImageIO.read(ClassLoader.getSystemResource("img/cursor.png"));
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }

	    Timer timer = new Timer(1000 / 50, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

		    repaint();
		}
	    });
	    timer.start();
	}

	public void onClick(MouseEvent event) {
	    currentScreen.onClick(event);
	}

	@Override
	public Dimension getPreferredSize() {
	    return new Dimension(400, 400);
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g.create();
	    Point p = getMousePosition();
	    currentScreen.draw(g2d, p);
	    if (p != null && showCursor)
		g2d.drawImage(cursor, p.x, p.y, this);
	    g2d.dispose();
	}
    }

    private PaintPane jPanel1;

    @Override
    public Screen currentScreen() {
	return currentScreen;
    }

    @Override
    public void openScreen(Screen screen) {
	currentScreen = screen;
    }
}
