package io.azraein.ferret.system.gfx.model;

import java.util.ArrayList;
import java.util.List;

import io.azraein.ferret.interfaces.Disposable;

public class Model implements Disposable {

	private final String id;
	private List<ModelEntity> entitiesList;
	private List<Material> materialList;

	public Model(String id, List<Material> materialList) {
		this.id = id;
		this.materialList = materialList;
		entitiesList = new ArrayList<>();
	}

	public String getId() {
		return id;
	}

	public List<ModelEntity> getEntitiesList() {
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
