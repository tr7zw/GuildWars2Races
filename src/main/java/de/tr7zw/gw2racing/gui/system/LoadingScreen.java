package de.tr7zw.gw2racing.gui.system;

import java.awt.Color;
import java.awt.event.MouseEvent;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.gui.elements.Label;

public class LoadingScreen extends Screen {

    @Override
    public void init() {
	setBackground("img/loading_background.png");
	Gw2Racing.getInstance().getGui().changeSize(800, 800);
	registerElement(
		new Label(366, 463, 5, Color.WHITE, () -> "Loading" + ((System.currentTimeMillis() / 500 % 3) + "")
			.replace("0", ".").replace("1", "..").replace("2", "...")));
	registerElement(new Label(58, 146, 3, Color.WHITE, () -> Gw2Racing.getInstance().getLoadingState()));
    }

    @Override
    public void onClick(MouseEvent event) {
	System.out.println(event.getX() + " " + event.getY());
	super.onClick(event);
    }

}
