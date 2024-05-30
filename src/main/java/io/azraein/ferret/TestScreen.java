package io.azraein.ferret;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Camera;
import io.azraein.ferret.system.gfx.FerretScreen;
import io.azraein.ferret.system.gfx.mesh.Entity;
import io.azraein.ferret.system.gfx.mesh.Mesh;
import io.azraein.ferret.system.gfx.model.Material;
import io.azraein.ferret.system.gfx.model.Model;
import io.azraein.ferret.system.gfx.model.ModelLoader;
import io.azraein.ferret.system.gfx.shader.Projection;
import io.azraein.ferret.system.gfx.shader.ShaderProgram;
import io.azraein.ferret.system.gfx.textures.Texture;
import io.azraein.ferret.system.utilities.FileUtils;

public class TestScreen extends FerretScreen {

	private ShaderProgram shaderProgram;
	private Projection projection;
	private Camera camera;

	public TestScreen(Engine engine) {
		super(engine);
	}

	private Entity cubeEntity;

	@Override
	public void onInit() {
		projection = new Projection((int) engine.getWindow().getWindowWidth(),
				(int) engine.getWindow().getWindowHeight());

		String vertexShader = "src/main/resources/shaders/default/default_vertex.glsl";
		String fragmentShader = "src/main/resources/shaders/default/default_fragment.glsl";

		try {
			shaderProgram = new ShaderProgram(FileUtils.fileToString(vertexShader),
					FileUtils.fileToString(fragmentShader));
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("modelMatrix");
			shaderProgram.createUniform("viewMatrix");
			shaderProgram.createUniform("textureSampler");
			shaderProgram.createUniform("material.diffuse");
		} catch (IOException e) {
			e.printStackTrace();
		}

		camera = new Camera();

		Model cubeModel = ModelLoader.loadModel("cube-model", "src/main/resources/models/cube/model.obj", textureCache);
		addModel(cubeModel);

		cubeEntity = new Entity("cube-entity", cubeModel.getId());
		cubeEntity.setPosition(0, 0, -2);
		cubeEntity.setScale(0.005f);
		addEntity(cubeEntity);
	}

	private float mouseSensitivity = 0.1f;
	private float movementSpeed = 2f;

	@Override
	public void onInput(float delta) {
		float move = movementSpeed * delta;

		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_W)) {
			camera.moveForward(move);
		} else if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_S)) {
			camera.moveBackwards(move);
		}

		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_A)) {
			camera.moveLeft(move);
		} else if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_D)) {
			camera.moveRight(move);
		}
		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			camera.moveUp(move);
		} else if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			camera.moveDown(move);
		}

		if (Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_2)) {
			Vector2f displVec = Ferret.input.getDisplayVector();
			camera.addRotation((float) Math.toRadians(-displVec.x * mouseSensitivity),
					(float) Math.toRadians(-displVec.y * mouseSensitivity));
		}

	}

	float r = 0.32f;
	float g = 0.32f;
	float b = 0.32f;

	float rotation = 0.0f;

	@Override
	public void onUpdate(float delta) {

		rotation += 0.6f;

		if (rotation > 360)
			rotation = 0;

		cubeEntity.setRotation(0, 1, 0, (float) Math.toRadians(rotation));

		clearColor.set(r, g, b);
	}

	@Override
	public void onRender() {
		shaderProgram.bind();
		{
			shaderProgram.setUniform("projectionMatrix", projection.getProjectionMatrix());
			shaderProgram.setUniform("viewMatrix", camera.getViewMatrix());
			shaderProgram.setUniform("textureSampler", 0);

			Collection<Model> models = modelMap.values();
			for (Model model : models) {
				List<Entity> modelEntities = model.getEntitiesList();
				List<Material> modelMaterials = model.getMaterialList();

				for (Material material : modelMaterials) {
					shaderProgram.setUniform("material.diffuse", material.getDiffuseColor());
					Texture texture = textureCache.getTexture(material.getTexturePath());
					glActiveTexture(GL_TEXTURE0);
					texture.bind();

					List<Mesh> materialMeshes = material.getMeshList();
					for (Mesh mesh : materialMeshes) {
						for (Entity entity : modelEntities) {
							shaderProgram.setUniform("modelMatrix", entity.getModelMatrix());
							mesh.draw();
						}
					}

					texture.unbind();
				}

			}
		}
		shaderProgram.unbind();
	}

	@Override
	public void onResize(int width, int height) {
		projection.updateProjectionMatrix(width, height);
	}

	@Override
	public void onImGuiRender() {
		ImGui.begin("Debug Panel");
		ImGui.text("FPS: " + engine.getFps());
		ImGui.text("Current Time: " + Ferret.gameCalendar.getTimeAsString(false));
		ImGui.text("Current Date: " + Ferret.gameCalendar.getDateAsString());
		ImGui.end();
	}

}
