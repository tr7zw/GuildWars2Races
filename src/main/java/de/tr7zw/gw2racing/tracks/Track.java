package de.tr7zw.gw2racing.tracks;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Track {

    @Getter
    @Setter
    private int mapId = 0;
    private ArrayList<Checkpoint> checkpoints = new ArrayList<>();

    public Track(int mapId) {
	this.mapId = mapId;
    }

    public void addCheckpoint(Checkpoint point) {
	checkpoints.add(point);
    }

    public Checkpoint getStart() {
	if (checkpoints != null && checkpoints.size() > 0)
	    return checkpoints.get(0);
	return null;
    }

    public Checkpoint getGoal() {
	if (checkpoints != null && checkpoints.size() > 1)
	    return checkpoints.get(checkpoints.size() - 1);
	return null;
    }

    public ArrayList<Checkpoint> getAllCheckpoints() {
	return checkpoints;
    }

    public ArrayList<Checkpoint> getCheckpoints() {
	if (checkpoints.size() < 2)
	    return new ArrayList<>();
	ArrayList<Checkpoint> tmp = new ArrayList<>(checkpoints);
	tmp.remove(0);
	tmp.remove(tmp.size() - 1);
	return tmp;
    }

}
