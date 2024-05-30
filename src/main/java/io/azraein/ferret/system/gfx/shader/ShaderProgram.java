package io.azraein.ferret.system.gfx.shader;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import io.azraein.ferret.interfaces.Disposable;

public class ShaderProgram implements Disposable {

	private final int programId;

	private final Map<String, Integer> uniformMap = new HashMap<>();

	public ShaderProgram(String vertexShader, String fragmentShader) {
		programId = glCreateProgram();

		if (programId == 0)
			throw new RuntimeException("Could not create shader");

		int vertexProgram = createShader(vertexShader, GL_VERTEX_SHADER);
		int fragmentProgram = createShader(fragmentShader, GL_FRAGMENT_SHADER);

		link();

		glDeleteShader(vertexProgram);
		glDeleteShader(fragmentProgram);
	}

	public int createShader(String shaderSource, int shaderType) {
		int shaderId = glCreateShader(shaderType);

		if (shaderId == 0)
			throw new RuntimeException("Error creating shader. Type: " + shaderType);

		glShaderSource(shaderId, shaderSource);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
			throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));

		glAttachShader(programId, shaderId);

		return shaderId;
	}

	private void link() {
		glLinkProgram(programId);

		if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
			throw new RuntimeException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
	}

	public void createUniform(String uniform) {
		int uniformLocation = glGetUniformLocation(programId, uniform);
		if (uniformLocation < 0)
			throw new RuntimeException(
					"Could not find uniform [" + uniform + "] in shader program [" + programId + "]");

		uniformMap.put(uniform, uniformLocation);
	}

	private int getUniform(String uniformName) {
		Integer location = uniformMap.get(uniformName);
		if (location == null)
			throw new RuntimeException("Could not find uniform: " + uniformName);

		return location.intValue();
	}

	public void setUniform(String uniformName, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glUniformMatrix4fv(getUniform(uniformName), false, value.get(stack.mallocFloat(16)));
		}
	}

	public void setUniform(String uniformName, int value) {
		glUniform1i(getUniform(uniformName), value);
	}

	public void setUniform(String uniformName, Vector2f value) {
		glUniform2f(getUniform(uniformName), value.x, value.y);
	}

	public void setUniform(String uniformName, Vector4f value) {
		glUniform4f(getUniform(uniformName), value.x, value.y, value.z, value.w);
	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	@Override
	public void onDispose() {
		unbind();
		if (programId != 0)
			glDeleteProgram(programId);
	}

}
