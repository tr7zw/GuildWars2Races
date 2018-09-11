package de.tr7zw.gw2racing.gui.system;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.gui.elements.ImageButton;
import de.tr7zw.gw2racing.gui.elements.PagedList;

public class TestScreen extends Screen {

    @Override
    public void init() {
	getGui().changeSize(400, 400);
	addCloseButton();
	registerElement(new ImageButton(1, 1, 32, 32, "img/x.png", "img/x_hover.png", () -> {
	    Gw2Racing.getInstance().getGui().openScreen(new GuiOverlay());
	}));
	PagedList list = new PagedList(30, 50);
	registerElement(list);
    }

}
