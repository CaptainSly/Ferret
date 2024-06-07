package io.azraein.ferret.system.gfx.lights;

import org.joml.Vector3f;

public class SpotLight {

	private String id;

	private Vector3f coneDirection;
	private float cutOff;
	private float cutOffAngle;
	private PointLight pointLight;

	public SpotLight(String id, Vector3f color, Vector3f position, Vector3f coneDirection, float intensity,
			float cutOffAngle) {
		this.pointLight = new PointLight(id + "-pointLight", color, position, intensity);
		this.coneDirection = coneDirection;
		this.cutOffAngle = cutOffAngle;
		setCutOffAngle(cutOffAngle);
	}

	public Vector3f getConeDirection() {
		return coneDirection;
	}

	public float getCutOff() {
		return cutOff;
	}

	public float getCutOffAngle() {
		return cutOffAngle;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public String getId() {
		return id;
	}

	public void setConeDirection(float x, float y, float z) {
		coneDirection.set(x, y, z);
	}

	public void setConeDirection(Vector3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public final void setCutOffAngle(float cutOffAngle) {
		this.cutOffAngle = cutOffAngle;
		cutOff = (float) Math.cos(Math.toRadians(cutOffAngle));
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}
}