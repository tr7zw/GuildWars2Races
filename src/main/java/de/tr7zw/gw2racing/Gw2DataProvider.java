package de.tr7zw.gw2racing;

import lombok.Getter;
import lombok.Setter;

public class Gw2DataProvider {

    @Getter
    @Setter
    private float x = 0;

    @Getter
    @Setter
    private float y = 0;

    @Getter
    @Setter
    private float z = 0;

    @Getter
    @Setter
    private float[] cameraPosition = new float[] { 0, 0, 0 };

    @Getter
    @Setter
    private float[] cameraRotation = new float[] { 0, 0, 0 };

    @Getter
    @Setter
    private double speed = 0;

    @Getter
    @Setter
    private int map_id = 0;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String mapName;

    @Getter
    @Setter
    private double fov = 0;

}
