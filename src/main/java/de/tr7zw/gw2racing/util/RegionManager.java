package de.tr7zw.gw2racing.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.tr7zw.gw2racing.Gw2Racing;
import de.tr7zw.gw2racing.region.Map;
import de.tr7zw.gw2racing.region.Point;
import de.tr7zw.gw2racing.region.PointOfInterest;
import de.tr7zw.gw2racing.region.Region;
import de.tr7zw.gw2racing.region.RegionLocationInfo;
import de.tr7zw.gw2racing.region.Sector;

public class RegionManager {

    final Gw2Racing main;

    File floorDir;
    Set<Integer> floorIds = new HashSet<>();
    java.util.Map<Integer, java.util.Map<Integer, Region>> floorRegionMap = new HashMap<>();

    public RegionManager(Gw2Racing main) {
	this.main = main;
	this.floorDir = new File("data/floors");
	if (!this.floorDir.exists()) {
	    this.floorDir.mkdir();

	    System.out.println("Downloading Region Data...");
	    main.setLoadingState("Downloading Region Data...");
	    JsonArray floors = main.getApiHelper().getFloors();
	    floors.forEach((JsonElement e) -> {
		System.out.println("Floor #" + e.getAsInt());
		main.setLoadingState("Floor #" + e.getAsInt());
		floorIds.add(e.getAsInt());

		File fDir = new File(this.floorDir, "floor-" + e.getAsInt());
		if (!fDir.exists()) {
		    fDir.mkdir();

		    java.util.Map<Integer, Region> regionMap = main.getApiHelper().getParsedRegionData(e.getAsInt());
		    this.floorRegionMap.put(e.getAsInt(), regionMap);

		    for (Region region : regionMap.values()) {
			File file = new File(fDir, "region-" + region.id + ".json");
			if (!file.exists()) {
			    try {
				file.createNewFile();
				try (Writer writer = new FileWriter(file)) {
				    Gson gson = new GsonBuilder().create();
				    gson.toJson(region, writer);
				}
			    } catch (IOException e1) {
				throw new RuntimeException(e1);
			    }
			}
		    }
		}
	    });
	} else {
	    System.out.println("Loading Region Data from File...");
	    main.setLoadingState("Loading Region Data from File...");
	    for (File dirEntry : floorDir.listFiles()) {
		if (dirEntry.isDirectory()) {
		    System.out.println(dirEntry.getName());
		    String floorIdString = dirEntry.getName().substring("floor-".length());
		    int floorId = Integer.parseInt(floorIdString);
		    System.out.println("Floor #" + floorId);
		    main.setLoadingState("Floor #" + floorId);
		    floorIds.add(floorId);

		    java.util.Map<Integer, Region> regionMap = new HashMap<>();
		    for (File fileEntry : dirEntry.listFiles()) {
			if (fileEntry.isFile()) {
			    System.out.println(fileEntry.getName());
			    String regionIdString = fileEntry.getName().substring("region-".length()).replace(".json",
				    "");
			    int regionId = Integer.parseInt(regionIdString);
			    System.out.println("Region #" + regionId);
			    main.setLoadingState("Region #" + regionId);

			    try (FileReader reader = new FileReader(fileEntry)) {
				Gson gson = new Gson();
				Region region = gson.fromJson(reader, Region.class);
				regionMap.put(regionId, region);
			    } catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    }

		    floorRegionMap.put(floorId, regionMap);
		}
	    }

	}

	main.setLoadingState("Done loading Region Data!");
	// System.out.println(this.floorRegionMap);
	// System.out.println(floorIds);
	// this.regionMap = main.apiHelper.getParsedRegionData();
    }

    public PointOfInterest findClosestPoi(Set<Integer> floors, RegionLocationInfo info, Point point) {
	if (info == null) {
	    return null;
	}
	double smallestDistance = Integer.MAX_VALUE;
	PointOfInterest closestPoi = null;
	for (Integer floor : floors) {
	    java.util.Map<Integer, Region> regionMap = this.floorRegionMap.get(floor);
	    if (regionMap == null) {
		continue;
	    }
	    Region region = regionMap.get(info.region.id);
	    Map map = region.maps.get(info.map.id);
	    for (Iterator<PointOfInterest> it = map.pois.values().iterator(); it.hasNext();) {
		PointOfInterest poi = it.next();
		if ("vista".equals(poi.type)) {
		    continue;// skip, since they don't have names
		}

		double distance = poi.coord.distance(point);
		if (distance < smallestDistance) {
		    closestPoi = poi;
		    smallestDistance = distance;
		}
	    }
	}
	return closestPoi;
    }

    public RegionLocationInfo getLocationInfoByCoords(Set<Integer> floors, int regionId, int mapId, Point coords) {
	for (Integer floor : floors) {
	    java.util.Map<Integer, Region> regionMap = this.floorRegionMap.get(floor);
	    System.out.println("regionMap: " + regionMap);
	    if (regionMap == null) {
		continue;
	    }
	    Region region = regionMap.get(regionId);
	    if (region == null) {
		return null;
	    }
	    Map map = region.maps.get(mapId);
	    if (map == null) {
		return null;
	    }
	    for (Iterator<Sector> it2 = map.sectors.values().iterator(); it2.hasNext();) {
		Sector sector = it2.next();
		if (sector.bounds.contains(coords)) {
		    return new RegionLocationInfo(region, map, sector);
		}
	    }
	}
	return null;
    }

    // public RegionLocationInfo getLocationInfoByCoords(Point coordsX) {
    // for (Iterator<Region> it = this.regionMap.values().iterator();
    // it.hasNext(); ) {
    // Region region = it.next();
    // for (Iterator<Map> it1 = region.maps.values().iterator(); it1.hasNext();
    // ) {
    // Map map = it1.next();
    // for (Iterator<Sector> it2 = map.sectors.values().iterator();
    // it2.hasNext(); ) {
    // Sector sector = it2.next();
    // if (sector.bounds.contains(coordsX)) {
    // return new RegionLocationInfo(region, map, sector);
    // }
    // }
    // }
    // }
    // return null;
    // }

}
