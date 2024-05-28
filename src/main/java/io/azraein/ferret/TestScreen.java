package io.azraein.ferret;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import io.azraein.ferret.system.Camera;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.FerretScreen;
import io.azraein.ferret.system.gfx.mesh.Entity;
import io.azraein.ferret.system.gfx.mesh.Mesh;
import io.azraein.ferret.system.gfx.model.Material;
import io.azraein.ferret.system.gfx.model.Model;
import io.azraein.ferret.system.gfx.shader.Projection;
import io.azraein.ferret.system.gfx.shader.ShaderProgram;
import io.azraein.ferret.system.gfx.textures.Texture;
import io.azraein.ferret.system.gfx.textures.TextureCache;
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

		try {
			shaderProgram = new ShaderProgram(FileUtils.fileToString("src/main/resources/vertex.glsl"),
					FileUtils.fileToString("src/main/resources/fragment.glsl"));
			shaderProgram.createUniform("projectionMatrix");
			shaderProgram.createUniform("modelMatrix");
			shaderProgram.createUniform("viewMatrix");
			shaderProgram.createUniform("textureSampler");
		} catch (IOException e) {
			e.printStackTrace();
		}

		camera = new Camera();

		float[] positions = new float[] {
				// V0
				-0.5f, 0.5f, 0.5f,
				// V1
				-0.5f, -0.5f, 0.5f,
				// V2
				0.5f, -0.5f, 0.5f,
				// V3
				0.5f, 0.5f, 0.5f,
				// V4
				-0.5f, 0.5f, -0.5f,
				// V5
				0.5f, 0.5f, -0.5f,
				// V6
				-0.5f, -0.5f, -0.5f,
				// V7
				0.5f, -0.5f, -0.5f,

				// For text coords in top face
				// V8: V4 repeated
				-0.5f, 0.5f, -0.5f,
				// V9: V5 repeated
				0.5f, 0.5f, -0.5f,
				// V10: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V11: V3 repeated
				0.5f, 0.5f, 0.5f,

				// For text coords in right face
				// V12: V3 repeated
				0.5f, 0.5f, 0.5f,
				// V13: V2 repeated
				0.5f, -0.5f, 0.5f,

				// For text coords in left face
				// V14: V0 repeated
				-0.5f, 0.5f, 0.5f,
				// V15: V1 repeated
				-0.5f, -0.5f, 0.5f,

				// For text coords in bottom face
				// V16: V6 repeated
				-0.5f, -0.5f, -0.5f,
				// V17: V7 repeated
				0.5f, -0.5f, -0.5f,
				// V18: V1 repeated
				-0.5f, -0.5f, 0.5f,
				// V19: V2 repeated
				0.5f, -0.5f, 0.5f, };
		float[] textCoords = new float[] { 0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f,

				0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f,

				// For text coords in top face
				0.0f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f,

				// For text coords in right face
				0.0f, 0.0f, 0.0f, 0.5f,

				// For text coords in left face
				0.5f, 0.0f, 0.5f, 0.5f,

				// For text coords in bottom face
				0.5f, 0.0f, 1.0f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, };
		int[] indices = new int[] {
				// Front face
				0, 1, 3, 3, 1, 2,
				// Top Face
				8, 10, 11, 9, 8, 11,
				// Right face
				12, 13, 7, 5, 12, 7,
				// Left face
				14, 15, 6, 4, 14, 6,
				// Bottom face
				16, 18, 19, 17, 16, 19,
				// Back face
				4, 6, 7, 5, 4, 7, };

		Texture texture = textureCache.getTexture(TextureCache.DEFAULT_TEXTURE);
		Material material = new Material();
		material.setTexturePath(texture.getTexturePath());
		List<Material> materialList = new ArrayList<>();
		materialList.add(material);

		Mesh mesh = new Mesh(positions, textCoords, indices);
		material.getMeshList().add(mesh);
		Model cubeModel = new Model("cube-model", materialList);
		addModel(cubeModel);

		cubeEntity = new Entity("cube-entity", cubeModel.getId());
		cubeEntity.setPosition(0, 0, -2);
		addEntity(cubeEntity);
	}

	private float mouseSensitivity = 0.1f;
	private float movementSpeed = 0.005f;

	@Override
	public void onInput(float delta) {
		float move = delta * movementSpeed;

		if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_W)) {
			camera.moveForward(move);
		} else if (Ferret.input.isKeyDown(GLFW.GLFW_KEY_S))
			camera.moveBackwards(move);

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

	float rotation = 0;

	@Override
	public void onUpdate(float delta) {

		clearColor.set(0.32f, 0.37f, 0.42f);

		rotation += 1.5;
		if (rotation > 360)
			rotation = 0;

		cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
	}

	@Override
	public void onUiRender() {
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

}
