package io.azraein.ferret.system.gfx;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

public class Window {

	private long windowPointer;

	private String windowTitle;
	private int windowWidth, windowHeight;

	public Window(String windowTitle, int windowWidth, int windowHeight) {
		this.windowTitle = windowTitle;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		windowPointer = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
		if (windowPointer == NULL)
			throw new RuntimeException("Failed to create the GLFW Window");

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(windowPointer, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode glfwVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(windowPointer, (glfwVidMode.width() - pWidth.get(0)) / 2,
					(glfwVidMode.height() - pHeight.get(0)) / 2);
		}

	}

	public void resize(int width, int height) {
		this.windowWidth = width;
		this.windowHeight = height;
	}

	public long getWindowPointer() {
		return windowPointer;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public int getWindowHeight() {
		return windowHeight;
	}

}
