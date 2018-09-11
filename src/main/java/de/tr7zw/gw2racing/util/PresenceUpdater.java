package de.tr7zw.gw2racing.util;

import java.util.ArrayList;

import com.google.gson.JsonObject;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.region.Point;
import de.tr7zw.gw2racing.region.PointOfInterest;
import de.tr7zw.gw2racing.region.RegionLocationInfo;
import de.tr7zw.gw2racing.util.math.ScreenUtils;
import de.tr7zw.gw2racing.util.math.Vector3;

public class PresenceUpdater extends Thread {

    private static final long UPDATE_RATE = 50;
    private static final double UNIT_MULTIPLIER = 1 / .0254;

    final Gw2Racing main;

    boolean active = true;

    Point playerLocation;
    Point cameraLocation;
    double y;
    Point lastplayerLocation;
    RegionLocationInfo locationInfo;
    PointOfInterest closestPoi;
    int locationUpdateCounter = 100;
    long speed_update_time = 0;
    double lastSpeed = 0;
    ArrayList<Double> lastdists = new ArrayList<>();
    Vector3 lastLocation = null;

    boolean ingame = false;

    public PresenceUpdater(Gw2Racing main) {
	this.main = main;
    }

    @Override
    public void run() {
	try {
	    while (active) {
		updatePresence();

		Thread.sleep(UPDATE_RATE);
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void kill() {
	this.active = false;
    }

    void updatePresence() {
	if (!main.isProcessRunning()) {
	    return;
	}
	if (main.getMumbleLink() == null) {
	    return;
	}

	JsonObject identity = main.getMumbleLink().getIdentityJson();
	if (identity == null)
	    return;
	if (!ingame) {
	    main.doneLoading();
	    ingame = true;
	}
	JsonObject mapData = identity != null ? main.getApiHelper().getMapData(identity.get("map_id").getAsInt())
		: null;

	Point[] continentRect = mapData != null
		? new Point[] { Point.fromJson(mapData.get("continent_rect").getAsJsonArray().get(0).getAsJsonArray()),
			Point.fromJson(mapData.get("continent_rect").getAsJsonArray().get(1).getAsJsonArray()) }
		: null;
	Point[] mapRect = mapData != null
		? new Point[] { Point.fromJson(mapData.get("map_rect").getAsJsonArray().get(0).getAsJsonArray()),
			Point.fromJson(mapData.get("map_rect").getAsJsonArray().get(1).getAsJsonArray()) }
		: null;

	playerLocation = mapData != null ? Point.fromMapCoords(continentRect, mapRect,
		new Point(main.getMumbleLink().fAvatarPosition[0] * UNIT_MULTIPLIER,
			main.getMumbleLink().fAvatarPosition[2] * UNIT_MULTIPLIER))
		: null;
	cameraLocation = mapData != null ? Point.fromMapCoords(continentRect, mapRect,
		new Point(main.getMumbleLink().fCameraPosition[0] * UNIT_MULTIPLIER,
			main.getMumbleLink().fCameraPosition[2] * UNIT_MULTIPLIER))
		: null;

	if (mapData != null)
	    y = main.getMumbleLink().fAvatarPosition[1] * UNIT_MULTIPLIER;
	if (lastplayerLocation == null)
	    lastplayerLocation = playerLocation;
	if (playerLocation != null && speed_update_time != System.currentTimeMillis() / 100) {
	    speed_update_time = System.currentTimeMillis() / 100;
	    lastdists.add(playerLocation.distance(lastplayerLocation));
	    if (lastdists.size() > 10)
		lastdists.remove(0);
	    lastSpeed = 0;
	    for (Double d : lastdists)
		lastSpeed += d;
	    lastSpeed /= lastdists.size();

	    if (main.getRace() != null) {
		Vector3 pos = new Vector3((float) playerLocation.x, (float) y, (float) playerLocation.y);
		main.getRace().sendUpdate(pos, lastLocation);
		lastLocation = pos;
	    }

	    lastplayerLocation = playerLocation;
	}

	if (!main.shouldShutdown) {
	    main.getDataProvider().setSpeed(lastSpeed);
	    if (identity != null) {
		main.getDataProvider().setMap_id(identity.get("map_id").getAsInt());
		main.getDataProvider().setName(identity.get("name").getAsString());
		main.getDataProvider().setFov(identity.get("fov").getAsDouble());
	    }
	    if (cameraLocation != null) {
		main.getDataProvider()
			.setCameraPosition(new float[] { (float) cameraLocation.x,
				(float) (main.getMumbleLink().fCameraPosition[1] * UNIT_MULTIPLIER),
				(float) cameraLocation.y });
		main.getDataProvider().setCameraRotation(main.getMumbleLink().fCameraFront);
	    }
	    if (mapData != null) {
		main.getDataProvider().setMapName(mapData.get("name").getAsString());
	    }
	    if (playerLocation != null) {
		main.getDataProvider().setX((float) playerLocation.x);
		main.getDataProvider().setY((float) y);
		main.getDataProvider().setZ((float) playerLocation.y);
	    }
	    if (playerLocation != null && cameraLocation != null) {
		ScreenUtils.updateMatrix();
	    }
	}

    }

}
