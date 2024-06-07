package io.azraein.ferret.system.gfx.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModelEntity {

	private final String id;
	private final String modelId;

	private Matrix4f modelMatrix;
	private Vector3f position;
	private Vector3f scale;
	private Quaternionf rotation;

	public ModelEntity(String id, String modelId) {
		this.id = id;
		this.modelId = modelId;

		modelMatrix = new Matrix4f();
		position = new Vector3f();
		scale = new Vector3f(1, 1, 1);
		rotation = new Quaternionf();
	}

	public String getId() {
		return id;
	}

	public String getModelId() {
		return modelId;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getScale() {
		return scale;
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public final void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public final void setScale(float scale) {
		this.scale.x = scale;
		this.scale.y = scale;
		this.scale.z = scale;
	}
	
	public final void setScale(float x, float y, float z) {
		scale.x = x;
		scale.y = y;
		scale.z = z;
	}

	public void setRotation(float x, float y, float z, float angle) {
		this.rotation.fromAxisAngleRad(x, y, z, angle);
	}

	public void updateModelMatrix() {
		modelMatrix.translationRotateScale(position, rotation, scale);
	}

}
