package de.tr7zw.gw2racing.util.math;

import de.tr7zw.gw2racing.Gw2DataProvider;
import de.tr7zw.gw2racing.Gw2Racing;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScreenUtils {

    /** the projection matrix **/
    private final Matrix4 projectionPitch = new Matrix4();
    /** the view matrix **/
    private final Matrix4 viewPitch = new Matrix4();
    /** the projection matrix **/
    private final Matrix4 projectionRot = new Matrix4();
    /** the view matrix **/
    private final Matrix4 viewRot = new Matrix4();
    /** the combined projection and view matrix **/
    private final Matrix4 combinedRot = new Matrix4();
    /** the combined projection and view matrix **/
    private final Matrix4 combinedPitch = new Matrix4();
    /** the unit length up vector of the camera **/
    private final Vector3 up = new Vector3(0, 1, 0);
    /** the position of the camera **/
    private final Vector3 position = new Vector3();
    /** the unit length direction vector of the camera **/
    private final Vector3 direction = new Vector3(0, 0, -1);
    final Vector3 tmp = new Vector3();

    public static double getRotation(double x, double z) {
	Gw2DataProvider data = Gw2Racing.getInstance().getDataProvider();
	double deltaX = x - data.getX();
	double deltaZ = z - data.getZ();
	double rad = Math.atan2(deltaZ, deltaX);
	rad += Math.atan2(data.getCameraRotation()[2], data.getCameraRotation()[0]);
	return rad;
    }

    /**
     * Projects the {@link Vector3} given in world space to screen coordinates. It's
     * the same as GLU gluProject with one small deviation: The viewport is assumed
     * to span the whole screen. The screen coordinate system has its origin in the
     * <b>bottom</b> left, with the y-axis pointing <b>upwards</b> and the x-axis
     * pointing to the right. This makes it easily useable in conjunction with
     * {@link Batch} and similar classes.
     * 
     * @return the mutated and projected worldCoords {@link Vector3}
     */
    public static Vector3 project(Vector3 worldCoords) {
	project(worldCoords, 0, 0, 1920, 1080);
	return worldCoords;
    }

    /**
     * Projects the {@link Vector3} given in world space to screen coordinates. It's
     * the same as GLU gluProject with one small deviation: The viewport is assumed
     * to span the whole screen. The screen coordinate system has its origin in the
     * <b>bottom</b> left, with the y-axis pointing <b>upwards</b> and the x-axis
     * pointing to the right. This makes it easily useable in conjunction with
     * {@link Batch} and similar classes. This method allows you to specify the
     * viewport position and dimensions in the coordinate system expected by
     * {@link GL20#glViewport(int, int, int, int)}, with the origin in the bottom
     * left corner of the screen.
     * 
     * @param viewportX      the coordinate of the bottom left corner of the
     *                       viewport in glViewport coordinates.
     * @param viewportY      the coordinate of the bottom left corner of the
     *                       viewport in glViewport coordinates.
     * @param viewportWidth  the width of the viewport in pixels
     * @param viewportHeight the height of the viewport in pixels
     * @return the mutated and projected worldCoords {@link Vector3}
     */
    public static Vector3 project(Vector3 worldCoords, float viewportX, float viewportY, float viewportWidth,
	    float viewportHeight) {
	if (Math.abs(getRotation(worldCoords.x, worldCoords.z)) % 5 > 1.5)// remove the second one 180° on the other
									  // side
	    return new Vector3(-1000, -1000, 0);
	Gw2DataProvider data = Gw2Racing.getInstance().getDataProvider();

	worldCoords.y /= 1;
	Vector3 targetPos = new Vector3(worldCoords);
	worldCoords.sub((float) data.getCameraPosition()[0], 0, (float) data.getCameraPosition()[2]);
	worldCoords.y = (float) -(data.getCameraPosition()[1] / 25 - worldCoords.y / 25);
	worldCoords.z *= -1;
	Vector3 clone = new Vector3(worldCoords);
	clone.prj(combinedPitch);
	worldCoords.prj(combinedRot);
	worldCoords.x = viewportWidth * (worldCoords.x + 1) / 2 + viewportX;
	worldCoords.y = viewportHeight * (clone.y + 1) / 2 + viewportY;

	worldCoords.z = Math.max(0,
		80 - new Vector3((float) data.getX(), (float) data.getY() / 100, (float) data.getZ()).dst(targetPos));
	worldCoords.y = 1080 - worldCoords.y;
	worldCoords.x = 1920 - worldCoords.x;
	return worldCoords;
    }

    public static void updateMatrix() {
	float aspect = 1920 / 1080;
	Gw2DataProvider data = Gw2Racing.getInstance().getDataProvider();
	// System.out.println(data.getCameraPosition()[0] * (1 / .0254) + " " +
	// data.getX());
	position.set(0, 0, 0);
	direction.set((float) (data.getCameraRotation()[0]), (float) (data.getCameraRotation()[1]),
		(float) (data.getCameraRotation()[2]));
	direction.nor();

	projectionPitch.setToProjection(Math.abs(0.01f), Math.abs(100), (float) (60 * data.getFov()), aspect);
	projectionRot.setToProjection(Math.abs(0.01f), Math.abs(100), (float) (85 * data.getFov()), aspect); // 84
	viewPitch.setToLookAt(position, tmp.set(position).add(direction), up);
	viewRot.setToLookAt(position, tmp.set(position).add(direction), up);
	combinedPitch.set(projectionPitch);
	combinedRot.set(projectionRot);
	Matrix4.mul(combinedPitch.val, viewPitch.val);
	Matrix4.mul(combinedRot.val, viewRot.val);
    }

}
