package io.azraein.ferret.system.gfx.model;

import java.util.ArrayList;
import java.util.List;

import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.system.gfx.mesh.Mesh;

public class Material implements Disposable {

	private List<Mesh> meshList;
	private String texturePath;

	public Material() {
		meshList = new ArrayList<>();
	}

	public List<Mesh> getMeshList() {
		return meshList;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	@Override
	public void onDispose() {
		meshList.forEach(Mesh::onDispose);
	}

}
