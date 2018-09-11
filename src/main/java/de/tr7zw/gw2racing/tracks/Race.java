package de.tr7zw.gw2racing.tracks;

import de.tr7zw.gw2racing.util.math.Vector3;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Race {

    @Getter
    private final Track track;
    @Getter
    private Long startTime = null;
    @Getter
    private Long endTime = null;
    @Getter
    private int round = 1;
    @Getter
    private int targetRounds = 1;
    @Getter
    private int atCheckpoint = 0;

    public long getTime() {
	if (startTime == null)
	    return 0;
	if (endTime != null)
	    return endTime - startTime;
	return System.currentTimeMillis() - startTime;
    }

    public void sendUpdate(@NonNull Vector3 to, Vector3 from) {
	if (endTime != null)
	    return;
	if (startTime == null) {
	    if (track.getStart().passed(to, from)) {
		startTime = System.currentTimeMillis();
		atCheckpoint++;
	    }
	} else {
	    if (track.getAllCheckpoints().get(atCheckpoint).passed(to, from)) {
		atCheckpoint++;
		if (track.getAllCheckpoints().size() <= atCheckpoint) {
		    endTime = System.currentTimeMillis();
		}
	    }
	}
    }

}
