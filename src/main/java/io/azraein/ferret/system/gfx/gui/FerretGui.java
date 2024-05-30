package io.azraein.ferret.system.gfx.gui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWKeyCallback;

import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.interfaces.GuiInstance;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.gfx.shader.ShaderProgram;
import io.azraein.ferret.system.gfx.textures.Texture;
import io.azraein.ferret.system.utilities.FileUtils;

public class FerretGui implements Disposable {

	private GuiMesh guiMesh;
	private GLFWKeyCallback previousKeyCallback;
	private Vector2f scale;
	private ShaderProgram shaderProgram;
	private Texture fontTexture;

	public FerretGui(Window window) {
		String vShader = "src/main/resources/shaders/default/default_ui_vertex.glsl";
		String fShader = "src/main/resources/shaders/default/default_ui_fragment.glsl";

		scale = new Vector2f();

		try {
			shaderProgram = new ShaderProgram(FileUtils.fileToString(vShader), FileUtils.fileToString(fShader));
			shaderProgram.createUniform("scale");
		} catch (IOException e) {
			e.printStackTrace();
		}

		createUiResources(window);
		setupKeyCallback(window);
	}

	public void render(GuiInstance guiInstance) {
		if (guiInstance == null) {
			return;
		}
		guiInstance.onRenderUi();

		shaderProgram.bind();

		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		glBindVertexArray(guiMesh.getVaoId());

		glBindBuffer(GL_ARRAY_BUFFER, guiMesh.getVerticesVBO());
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, guiMesh.getIndicesVBO());

		ImGuiIO io = ImGui.getIO();
		scale.x = 2.0f / io.getDisplaySizeX();
		scale.y = -2.0f / io.getDisplaySizeY();
		shaderProgram.setUniform("scale", scale);

		ImDrawData drawData = ImGui.getDrawData();
		int numLists = drawData.getCmdListsCount();
		for (int i = 0; i < numLists; i++) {
			glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

			int numCmds = drawData.getCmdListCmdBufferSize(i);
			for (int j = 0; j < numCmds; j++) {
				final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
				final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
				final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

				fontTexture.bind();
				glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
			}
		}

		glDisable(GL_BLEND);
	}

	private void createUiResources(Window window) {
		ImGui.createContext();

		ImGuiIO imGuiIO = ImGui.getIO();
		imGuiIO.setIniFilename(null);
		imGuiIO.setDisplaySize(window.getWindowWidth(), window.getWindowHeight());

		ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
		ImInt width = new ImInt();
		ImInt height = new ImInt();
		ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
		fontTexture = new Texture(width.get(), height.get(), buf);
		guiMesh = new GuiMesh();
	}

	private void setupKeyCallback(Window window) {
		ImGuiIO io = ImGui.getIO();
		io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
		io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
		io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
		io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
		io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
		io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
		io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
		io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
		io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
		io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
		io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
		io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
		io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
		io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
		io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
		io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);

		previousKeyCallback = glfwSetKeyCallback(window.getWindowPointer(), (handle, key, scancode, action, mods) -> {
			Ferret.input.key_callback(handle, key, scancode, action, mods);
			if (!io.getWantCaptureKeyboard()) {
				if (previousKeyCallback != null) {
					previousKeyCallback.invoke(handle, key, scancode, action, mods);
				}
				return;
			}
			if (action == GLFW_PRESS) {
				io.setKeysDown(key, true);
			} else if (action == GLFW_RELEASE) {
				io.setKeysDown(key, false);
			}
			io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
		});

		glfwSetCharCallback(window.getWindowPointer(), (handle, c) -> {
			if (!io.getWantCaptureKeyboard()) {
				return;
			}
			io.addInputCharacter(c);
		});
	}

	public void resize(int width, int height) {
		ImGui.getIO().setDisplaySize(width, height);
	}

	public GuiMesh getGuiMesh() {
		return guiMesh;
	}

	public GLFWKeyCallback getPreviousKeyCallback() {
		return previousKeyCallback;
	}

	public Vector2f getScale() {
		return scale;
	}

	public ShaderProgram getShaderProgram() {
		return shaderProgram;
	}

	public Texture getFontTexture() {
		return fontTexture;
	}

	@Override
	public void onDispose() {
		shaderProgram.onDispose();
		fontTexture.onDispose();
		if (previousKeyCallback != null)
			previousKeyCallback.free();
	}

}
