package de.tr7zw.gw2racing.tracks;

public class TestTrackGenerator {

    public static Track getLATrack() {
	Track track = new Track(50);
	track.addCheckpoint(new Checkpoint(16311.031f, 900, 14500.553f, 16331.181f, 900, 14540.472f));
	track.addCheckpoint(new Checkpoint(16482.797f, 716, 14751.106f, 16451.156f, 716, 14758.037f));
	track.addCheckpoint(new Checkpoint(16276.897f, 705, 14931.015f, 16263.612f, 705, 14962.369f));
	track.addCheckpoint(new Checkpoint(16311.031f, 900, 14500.553f, 16331.181f, 900, 14540.472f));
	return track;
    }

}
