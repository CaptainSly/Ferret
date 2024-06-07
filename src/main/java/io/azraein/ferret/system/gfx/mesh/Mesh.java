package io.azraein.ferret.system.gfx.mesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import io.azraein.ferret.interfaces.Disposable;

public class Mesh implements Disposable {

	private int numVertices;
	private int vaoId;

	private List<Integer> vboIdList;

	public Mesh(float[] positions, float[] normals, float[] texCoords, int[] indices) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			numVertices = indices.length;
			vboIdList = new ArrayList<>();

			Logger.debug("Positions Size: " + positions.length);
			Logger.debug("Normals Size: " + normals.length);
			Logger.debug("TextureCoords Size: " + texCoords.length);
			Logger.debug("Indices Size: " + indices.length);
			
			vaoId = glGenVertexArrays();
			glBindVertexArray(vaoId);

			// Positions VBO
			int vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
			positionsBuffer.put(0, positions);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			// Normals VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer normalsBuffer = stack.callocFloat(normals.length);
			normalsBuffer.put(0, normals);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

			// Texture Coordinates VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			FloatBuffer textCoordsBuffer = stack.callocFloat(texCoords.length);
			textCoordsBuffer.put(0, texCoords);
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);

			// Index VBO
			vboId = glGenBuffers();
			vboIdList.add(vboId);
			IntBuffer indicesBuffer = stack.callocInt(indices.length);
			indicesBuffer.put(0, indices);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);

		}
	}

	public void draw() {
		glBindVertexArray(getVaoId());
		glDrawElements(GL_TRIANGLES, getNumVertices(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getVaoId() {
		return vaoId;
	}

	public List<Integer> getVboIdList() {
		return vboIdList;
	}

	@Override
	public void onDispose() {
		vboIdList.forEach(GL30::glDeleteBuffers);
		glDeleteVertexArrays(vaoId);

	}

}
