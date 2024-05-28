package io.azraein.ferret.system;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.tinylog.Logger;

import io.azraein.ferret.TestScreen;
import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.system.calendar.Calendar;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.input.Input;
import io.azraein.ferret.system.lua.FerretLua;
import io.azraein.ferret.system.utilities.Utils;

public class Engine implements Disposable {

	private FerretLua luaEngine;
	private Window window;

	// Current Game Screen | TODO: Create an array of them and switch to when
	// needed.
	private FerretScreen currentGameScreen;

	// Game Loop Variables
	private boolean isRunning = false;
	private float targetUps = 30;
	private float targetFps = 60;

	public void run() {
		// Startup
		Ferret.FERRET_VERSION += " -GLFW Backend";

		Logger.info("LWJGL Version: " + Version.getVersion());
		Logger.info("Ferret Version: " + Ferret.FERRET_VERSION);

		init();
		loop();
		onDispose();
	}

	private void init() {
		// Setup an error callback. The default implementation will print the error
		// message in System.err
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		window = new Window("Ferret Engine: " + Ferret.FERRET_VERSION, 1280, 720);

		glfwMakeContextCurrent(window.getWindowPointer());
		glfwSwapInterval(1);

		glfwShowWindow(window.getWindowPointer());

		GL.createCapabilities();

		// ALL ACTUAL GAME LOADING GOES DOWN HERE
		Ferret.gameCalendar = new Calendar();
		Ferret.input = new Input();
		Ferret.registry = new Registry();

		// Create the Game Screens
		currentGameScreen = new TestScreen(this);

		// Setup GLFW Callbacks

		// Input Callbacks
		glfwSetKeyCallback(window.getWindowPointer(), Ferret.input::key_callback);
		glfwSetCursorPosCallback(window.getWindowPointer(), Ferret.input::cursorPos_callback);
		glfwSetCursorEnterCallback(window.getWindowPointer(), Ferret.input::cursorEnter_callback);
		glfwSetScrollCallback(window.getWindowPointer(), Ferret.input::cursorScroll_callback);
		glfwSetMouseButtonCallback(window.getWindowPointer(), Ferret.input::mouseButton_callback);
		glfwSetJoystickCallback(Ferret.input::joystick_callback);

		// Resize Callback
		glfwSetFramebufferSizeCallback(window.getWindowPointer(), (window, width, height) -> onResize(width, height));

		// Create Ferret Lua at the end of initialization but before screen
		// initialization.
		luaEngine = new FerretLua();
		currentGameScreen.onInit();
	}

	private void loop() {
		isRunning = true;

		// Setup GameLoop
		long initialTime = System.currentTimeMillis();
		float timeUpdate = 1000.f / targetUps;
		float timeRender = targetFps > 0 ? 1000.f / targetFps : 0;

		float deltaUpdate = 0;
		float deltaFps = 0;

		long updateTime = initialTime;
		while (isRunning) {
			glfwPollEvents();

			long now = System.currentTimeMillis();
			deltaUpdate += (now - initialTime) / timeUpdate;
			deltaFps += (now - initialTime) / timeRender;

			// Window Should Close
			if (glfwWindowShouldClose(window.getWindowPointer()))
				isRunning = false;

			if (targetFps <= 0 || deltaFps >= 1) {
				float delta = now - initialTime;
				input(delta);
			}

			if (deltaUpdate >= 1) {
				float delta = now - updateTime;
				update(delta);
				updateTime = now;
				deltaUpdate--;
			}

			if (targetFps <= 0 || deltaFps >= 1) {
				glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
				render();
				deltaFps--;
				glfwSwapBuffers(window.getWindowPointer());
			}

			initialTime = now;
		}
	}

	public void onResize(int width, int height) {
		window.resize(width, height);
		currentGameScreen.onResize(width, height);
	}

	private float lastCalendarUpdateTime = 0.0f;

	private void update(float delta) {

		lastCalendarUpdateTime += delta;
		if (lastCalendarUpdateTime >= Utils.getCalendarUpdateInterval(32)) {
			Ferret.gameCalendar.update();
			lastCalendarUpdateTime = 0;
		}

		currentGameScreen.updateScreen(delta);
	}

	private void render() {
		currentGameScreen.render();
		currentGameScreen.renderUi();
	}

	private void input(float delta) {
		Ferret.input.update();
		currentGameScreen.onInput(delta);
	}

	public Window getWindow() {
		return window;
	}

	public FerretLua getLuaEngine() {
		return luaEngine;
	}

	@Override
	public void onDispose() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.getWindowPointer());
		glfwDestroyWindow(window.getWindowPointer());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

}
