package io.azraein.ferret.system.gfx.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.system.gfx.mesh.Mesh;

public class Material implements Disposable {

	public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

	private List<Mesh> meshList;
	private String texturePath;

	private Vector4f diffuseColor;

	public Material() {
		meshList = new ArrayList<>();
		diffuseColor = DEFAULT_COLOR;
	}

	public List<Mesh> getMeshList() {
		return meshList;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public Vector4f getDiffuseColor() {
		return diffuseColor;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public void setDiffuseColor(Vector4f diffuseColor) {
		this.diffuseColor = diffuseColor;
	}

	@Override
	public void onDispose() {
		meshList.forEach(Mesh::onDispose);
	}

}
