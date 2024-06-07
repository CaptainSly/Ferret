package io.azraein.ferret.system.screens;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.Collection;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import io.azraein.ferret.DebugGui;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.gfx.lights.SpotLight;
import io.azraein.ferret.system.gfx.mesh.Mesh;
import io.azraein.ferret.system.gfx.model.Material;
import io.azraein.ferret.system.gfx.model.Model;
import io.azraein.ferret.system.gfx.model.ModelEntity;
import io.azraein.ferret.system.gfx.model.ModelLoader;
import io.azraein.ferret.system.gfx.textures.Texture;

public class TestScreen extends FerretScreen {

	public TestScreen(Engine engine) {
		super(engine);
	}

	private ModelEntity cubeEntity;

	@Override
	public void onInit() {
		// Shader Stuff
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelMatrix");
		shaderProgram.createUniform("viewMatrix");
		shaderProgram.createUniform("textureSampler");
		shaderProgram.createUniform("material.diffuse");
		shaderProgram.createUniform("material.ambient");
		shaderProgram.createUniform("material.specular");
		shaderProgram.createUniform("material.reflectance");
		screenLights.createUniforms(shaderProgram);

		Model cubeModel = ModelLoader.loadModel("cube-model", "src/main/resources/models/cube/model.obj", textureCache);
		addModel(cubeModel);

		cubeEntity = new ModelEntity("cube-entity", cubeModel.getId());
		cubeEntity.setPosition(0, 0, -2);
		cubeEntity.setScale(0.01f);
		addEntity(cubeEntity);

		var spot = new SpotLight("flashlight", new Vector3f(1, 1, 1),
				new Vector3f(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z - 3f),
				new Vector3f(0, 0, -1), 0.3f, 0.45f);

		screenLights.addSpotLights(spot);

		screenLights.getAmbientLight().setIntensity(0.3f);
	}

	private float mouseSensitivity = 0.1f;
	private float movementSpeed = 2f;

	@Override
	public void onInput(float delta) {
		float move = movementSpeed * delta;

		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			movementSpeed = 4f;
		else
			movementSpeed = 2f;

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
		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			camera.moveUp(move);
		} else if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			camera.moveDown(move);
		}

		if (Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			Vector2f displVec = Ferret.input.getDisplayVector();
			camera.addRotation((float) Math.toRadians(-displVec.x * mouseSensitivity),
					(float) Math.toRadians(-displVec.y * mouseSensitivity));
		}
		
	}

	@Override
	public boolean handleGuiInput(Window window) {
		return DebugGui.handleInput(window);
	}

	float rotation = 0.0f;

	@Override
	public void onUpdate(float delta) {
		rotation += 0.6f;

		if (rotation > 360)
			rotation = 0;

		cubeEntity.setRotation(0, 1, 0, (float) Math.toRadians(rotation));

	}

	@Override
	public void onRender() {
		shaderProgram.bind();
		{
			// Set Projection View
			shaderProgram.setUniform("projectionMatrix", projection.getProjectionMatrix());
			shaderProgram.setUniform("viewMatrix", camera.getViewMatrix());

			// Set default TextureSampler = 0
			shaderProgram.setUniform("textureSampler", 0);

			// Update Lights
			screenLights.updateLights(shaderProgram, camera);

			// Draw Models
			Collection<Model> models = modelMap.values();
			for (Model model : models) {
				for (Material material : model.getMaterialList()) {
					shaderProgram.setUniform("material.ambient", material.getAmbientColor());
					shaderProgram.setUniform("material.diffuse", material.getDiffuseColor());
					shaderProgram.setUniform("material.specular", material.getSpecularColor());
					shaderProgram.setUniform("material.reflectance", material.getReflectance());
					Texture texture = textureCache.getTexture(material.getTexturePath());
					glActiveTexture(GL_TEXTURE0);
					texture.bind();

					for (Mesh mesh : material.getMeshList()) {

						for (ModelEntity entity : model.getEntitiesList()) {
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
		DebugGui.drawGui(this, engine);
	}

}
