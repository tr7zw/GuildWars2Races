package de.tr7zw.gw2racing.tracks;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import de.tr7zw.gw2racing.region.Point;
import de.tr7zw.gw2racing.util.math.Vector3;
import lombok.Getter;
import lombok.NonNull;

public class Checkpoint {

    @Getter
    private CheckpointType type;
    private float[] points;

    public Checkpoint(float x1, float y1, float z1, float x2, float y2, float z2) {
	type = CheckpointType.LINE;
	points = new float[] { x1, y1, z1, x2, y2, z2 };
    }

    public Vector3[] getFlags() {
	if (type == CheckpointType.LINE) {
	    return new Vector3[] { new Vector3(points[0], points[1], points[2]),
		    new Vector3(points[3], points[4], points[5]) };
	}
	return new Vector3[0];
    }

    public boolean passed(@NonNull Vector3 to, Vector3 from) {
	if (type == CheckpointType.LINE && from != null) {
	    Line2D movement = new Line2D.Double(new Point2D.Double(to.x, to.z), new Point2D.Double(from.x, from.z));
	    return movement.intersectsLine(new Line2D.Float(points[0], points[2], points[3], points[5]));
	}
	return false;
    }

    public Vector3 getPointer() {
	if (type == CheckpointType.LINE) {
	    return new Vector3((points[0] + points[3]) / 2, 0, (points[2] + points[5]) / 2);
	}
	return null;
    }

    public double avg_dist(Vector3 position) {
	if (type == CheckpointType.LINE)
	    return new Point((points[0] + points[3]) / 2, (points[2] + points[5]) / 2)
		    .distance(new Point(position.x, position.z));

	return 0;
    }

}
