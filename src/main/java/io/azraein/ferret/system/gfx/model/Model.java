package io.azraein.ferret.system.gfx.model;

import java.util.ArrayList;
import java.util.List;

import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.system.gfx.mesh.Entity;

public class Model implements Disposable {

	private final String id;
	private List<Entity> entitiesList;
	private List<Material> materialList;

	public Model(String id, List<Material> materialList) {
		this.id = id;
		this.materialList = materialList;
		entitiesList = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public List<Entity> getEntitiesList() {
		return entitiesList;
	}

	public List<Material> getMaterialList() {
		return materialList;
	}

	@Override
	public void onDispose() {
		materialList.forEach(Material::onDispose);
	}

}
