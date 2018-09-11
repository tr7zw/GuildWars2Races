package de.tr7zw.gw2racing.gui;

import de.tr7zw.gw2racing.gui.system.Screen;

public interface OverlayInterface {

    public Screen currentScreen();

    public void openScreen(Screen screen);

    public void changeSize(int width, int height);

    public int getWidth();

    public int getHeight();

    public void setMovable(boolean movable);

}
