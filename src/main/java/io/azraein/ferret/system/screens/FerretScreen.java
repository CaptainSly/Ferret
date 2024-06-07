package io.azraein.ferret.system.screens;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import imgui.ImGui;
import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.interfaces.GuiInstance;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.gfx.Camera;
import io.azraein.ferret.system.gfx.model.ModelEntity;
import io.azraein.ferret.system.gfx.model.Model;
import io.azraein.ferret.system.gfx.shader.Projection;
import io.azraein.ferret.system.gfx.shader.ShaderProgram;
import io.azraein.ferret.system.gfx.textures.TextureCache;
import io.azraein.ferret.system.utilities.FileUtils;

public abstract class FerretScreen implements Disposable, GuiInstance {

	// Clear Color
	protected Vector3f clearColor = new Vector3f(0.32f, 0.32f, 0.32f);

	// Screen main members
	protected Engine engine;
	protected TextureCache textureCache;
	protected Map<String, Model> modelMap;
	protected ShaderProgram shaderProgram;
	protected Projection projection;
	protected Camera camera;
	protected ScreenLights screenLights;

	public FerretScreen(Engine engine) {
		this.engine = engine;

		modelMap = new HashMap<>();
		textureCache = new TextureCache();

		// Create Lights
		screenLights = new ScreenLights();

		camera = new Camera();
		camera.setPosition(0, 0, 5);

		projection = new Projection((int) engine.getWindow().getWindowWidth(),
				(int) engine.getWindow().getWindowHeight());

		String vertexShader = "src/main/resources/shaders/default/default_vertex.glsl";
		String fragmentShader = "src/main/resources/shaders/default/default_fragment.glsl";

		try {
			shaderProgram = new ShaderProgram(FileUtils.fileToString(vertexShader),
					FileUtils.fileToString(fragmentShader));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			List<ModelEntity> entities = model.getEntitiesList();
			for (ModelEntity entity : entities) {
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

	protected void addEntity(ModelEntity entity) {
		String modelId = entity.getModelId();
		Model model = modelMap.get(modelId);
		if (model == null)
			throw new RuntimeException("Could not find model [" + modelId + "]");

		model.getEntitiesList().add(entity);
	}

	public void setScreenLights(ScreenLights screenLights) {
		this.screenLights = screenLights;
	}

	public ScreenLights getScreenLights() {
		return screenLights;
	}

	@Override
	public void onDispose() {
		modelMap.values().forEach(Model::onDispose);
	}
}
