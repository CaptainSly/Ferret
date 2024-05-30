package io.azraein.ferret.system.gfx;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.interfaces.GuiInstance;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.gfx.mesh.Entity;
import io.azraein.ferret.system.gfx.model.Model;
import io.azraein.ferret.system.gfx.textures.TextureCache;

public abstract class FerretScreen implements Disposable, GuiInstance {

	// Clear Color
	protected Vector3f clearColor = new Vector3f(1, 1, 1);

	// Screen main members
	protected Engine engine;
	protected TextureCache textureCache;
	protected Map<String, Model> modelMap;

	public FerretScreen(Engine engine) {
		this.engine = engine;

		modelMap = new HashMap<>();
		textureCache = new TextureCache();
	}

	public abstract void onInit();

	public abstract void onInput(float delta);

	public abstract void onUpdate(float delta);

	public abstract void onRender();

	public abstract void onImGuiRender();

	public abstract void onResize(int width, int height);

	@Override
	public void onRenderUi() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
		onImGuiRender();
		ImGui.endFrame();
		ImGui.render();
	}

	public void render() {
		glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BACK);
		onRender();
	}

	public void updateScreen(float delta) {

		Collection<Model> models = modelMap.values();
		for (Model model : models) {
			List<Entity> entities = model.getEntitiesList();
			for (Entity entity : entities) {
				entity.updateModelMatrix();
			}
		}

		onUpdate(delta);
	}

	public void input(float delta) {
		onInput(delta);
	}

	public void resize(int width, int height) {
		onResize(width, height);
	}

	protected void addModel(Model model) {
		modelMap.put(model.getId(), model);
	}

	protected void addEntity(Entity entity) {
		String modelId = entity.getModelId();
		Model model = modelMap.get(modelId);
		if (model == null)
			throw new RuntimeException("Could not find model [" + modelId + "]");

		model.getEntitiesList().add(entity);
	}

	@Override
	public void onDispose() {
		modelMap.values().forEach(Model::onDispose);
	}
}