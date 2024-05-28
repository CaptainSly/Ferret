package io.azraein.ferret.system.input;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.tinylog.Logger;

public class Input {

	// Mouse Input
	private Vector2f currentMousePos;
	private Vector2f previousMousePos;
	private Vector2f displayVector;

	private boolean[] mouseBtns;

	private boolean inWindow;

	// Key Input
	private boolean[] keysDown;
	private boolean[] keysUp;

	// Controller Input

	public Input() {
		currentMousePos = new Vector2f();
		previousMousePos = new Vector2f();
		displayVector = new Vector2f();

		mouseBtns = new boolean[GLFW_MOUSE_BUTTON_LAST];

		keysDown = new boolean[GLFW_KEY_LAST];
		keysUp = new boolean[GLFW_KEY_LAST];		
	}

	public void key_callback(long window, int key, int scanCode, int action, int mods) {

		if (action == GLFW_PRESS) {
			keysDown[key] = true;
			keysUp[key] = false;
		} else if (action == GLFW_RELEASE) {
			keysDown[key] = false;
			keysUp[key] = true;
		}

	}

	public void cursorPos_callback(long window, double xPos, double yPos) {
		currentMousePos.x = (float) xPos;
		currentMousePos.y = (float) yPos;
	}

	public void cursorEnter_callback(long window, boolean entered) {
		inWindow = entered;
	}

	public void cursorScroll_callback(long window, double xOffset, double yOffset) {

	}

	public void mouseButton_callback(long window, int button, int action, int mods) {

		if (action == GLFW_PRESS)
			mouseBtns[button] = true;
		else if (action == GLFW_RELEASE)
			mouseBtns[button] = false;

	}

	public void joystick_callback(int joystickId, int event) {

		if (event == GLFW_CONNECTED) {
			Logger.debug("Connected Controller: " + glfwGetJoystickName(joystickId));
		} else if (event == GLFW_DISCONNECTED) {
			Logger.debug("Disconnected Controller: " + glfwGetJoystickName(joystickId));
		
		}

	}

	public void update() {
		displayVector.x = 0;
		displayVector.y = 0;
		if (previousMousePos.x > 0 && previousMousePos.y > 0 && inWindow) {
			double deltax = currentMousePos.x - previousMousePos.x;
			double deltay = currentMousePos.y - previousMousePos.y;
			boolean rotateX = deltax != 0;
			boolean rotateY = deltay != 0;
			if (rotateX) {
				displayVector.y = (float) deltax;
			}
			if (rotateY) {
				displayVector.x = (float) deltay;
			}
		}
		previousMousePos.x = currentMousePos.x;
		previousMousePos.y = currentMousePos.y;
	}

	public boolean isKeyDown(int keyCode) {
		return keysDown[keyCode];
	}

	public boolean isKeyUp(int keyCode) {
		return keysUp[keyCode];
	}

	public boolean isMouseBtnDown(int btn) {
		return mouseBtns[btn];
	}

	// Private Input Methods

	// Getters

	public Vector2f getCurrentMousePos() {
		return currentMousePos;
	}

	public Vector2f getPreviousMousePos() {
		return previousMousePos;
	}

	public Vector2f getDisplayVector() {
		return displayVector;
	}

	public boolean[] getMouseBtns() {
		return mouseBtns;
	}

	public boolean isInWindow() {
		return inWindow;
	}

	public boolean[] getKeysDown() {
		return keysDown;
	}

	public boolean[] getKeysUp() {
		return keysUp;
	}

}
