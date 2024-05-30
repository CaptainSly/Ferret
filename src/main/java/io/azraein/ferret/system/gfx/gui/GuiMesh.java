package io.azraein.ferret.system.gfx.gui;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import imgui.ImDrawData;
import io.azraein.ferret.interfaces.Disposable;

public class GuiMesh implements Disposable {

	private int indicesVBO;
	private int vaoId;
	private int verticesVBO;

	public GuiMesh() {
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		// Single VBO
		verticesVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, verticesVBO);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.SIZEOF_IM_DRAW_VERT, 8);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.SIZEOF_IM_DRAW_VERT, 16);

		indicesVBO = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void onDispose() {
		glDeleteBuffers(indicesVBO);
		glDeleteBuffers(verticesVBO);
		glDeleteVertexArrays(vaoId);
	}

	public int getIndicesVBO() {
		return indicesVBO;
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVerticesVBO() {
		return verticesVBO;
	}

}
