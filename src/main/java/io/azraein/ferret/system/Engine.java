package io.azraein.ferret.system;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.tinylog.Logger;

import io.azraein.ferret.interfaces.Disposable;
import io.azraein.ferret.system.calendar.Calendar;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.gfx.gui.FerretGui;
import io.azraein.ferret.system.input.Input;
import io.azraein.ferret.system.lua.FerretLua;
import io.azraein.ferret.system.screens.EditorScreen;
import io.azraein.ferret.system.screens.FerretScreen;
import io.azraein.ferret.system.screens.TestScreen;
import io.azraein.ferret.system.utilities.Utils;

public class Engine implements Disposable {

	private FerretLua luaEngine;
	private Window window;

	private Map<String, FerretScreen> ferretGameScreens;
	private FerretScreen currentGameScreen;
	private FerretGui ferretGui;

	// Game Loop Variables
	private boolean isRunning = false;
	private float targetFps = 60;

	private double frameTime = 0.0;

	public void run() {
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

		window = new Window("Ferret Engine", 1280, 720);

		glfwMakeContextCurrent(window.getWindowPointer());
		glfwSwapInterval(1);

		glfwShowWindow(window.getWindowPointer());

		GL.createCapabilities();

		ferretGameScreens = new HashMap<>();

		// ALL ACTUAL GAME LOADING GOES DOWN HERE
		Ferret.gameCalendar = new Calendar();
		Ferret.input = new Input();
		Ferret.registry = new Registry();

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

		// Create the Game Screens
		ferretGameScreens.put("testScreen", new TestScreen(this));
		ferretGameScreens.put("editorScreen", new EditorScreen(this));
		currentGameScreen = ferretGameScreens.get("testScreen");

		// Create Ferret Lua at the end of initialization but before screen
		// initialization.
		luaEngine = new FerretLua();
		ferretGui = new FerretGui(window);

		currentGameScreen.onInit();
	}

	private void loop() {
		isRunning = true;

		double lastTime = glfwGetTime();
		int nbFrames = 0;

		// Setup GameLoop
		long initialTime = System.currentTimeMillis();
		float timeUpdate = 1000.f / targetFps;
		float timeRender = 1000.f / targetFps;

		float deltaUpdate = 0;
		float deltaFps = 0;

		long updateTime = initialTime;
		while (isRunning) {
			long now = System.currentTimeMillis();
			deltaUpdate += (now - initialTime) / timeUpdate;
			deltaFps += (now - initialTime) / timeRender;

			glfwPollEvents();

			double cTime = glfwGetTime();
			nbFrames++;

			if (cTime - lastTime >= 1.0) {
				frameTime = (1000.0 / nbFrames);
				nbFrames = 0;
				lastTime += 1.0;
			}

			// Window Should Close
			if (glfwWindowShouldClose(window.getWindowPointer()))
				isRunning = false;

			if (targetFps <= 0 || deltaFps >= 1) {
				float delta = (now - initialTime) / 1000.f;
				onInput(delta);
			}

			if (deltaUpdate >= 1) {
				float delta = (now - updateTime) / 1000.f;
				onUpdate(delta);
				updateTime = now;
				deltaUpdate--;
			}

			if (targetFps <= 0 || deltaFps >= 1) {
				glViewport(0, 0, window.getWindowWidth(), window.getWindowHeight());
				onRender();
				glfwSwapBuffers(window.getWindowPointer());
				deltaFps--;
			}

			initialTime = now;
		}
	}

	public void onResize(int width, int height) {
		window.resize(width, height);
		currentGameScreen.resize(width, height);
		ferretGui.resize(width, height);
	}

	private float lastCalendarUpdateTime = 0.0f;

	private void onUpdate(float delta) {

		lastCalendarUpdateTime += delta;
		if (lastCalendarUpdateTime >= Utils.getCalendarUpdateInterval(32)) {
			Ferret.gameCalendar.update();
			lastCalendarUpdateTime = 0;
		}

		currentGameScreen.updateScreen(delta);
	}

	private void onRender() {
		currentGameScreen.render();
		ferretGui.render(currentGameScreen);
	}

	private void onInput(float delta) {
		Ferret.input.update();

		boolean inputConsumed = currentGameScreen.handleGuiInput(window);

		if (inputConsumed)
			return;
		else {
			currentGameScreen.input(delta);

			if (Ferret.input.isKeyDown(GLFW_KEY_ESCAPE))
				glfwSetWindowShouldClose(window.getWindowPointer(), true);

			if (Ferret.input.isKeyDown(GLFW_KEY_F11))
				if (Ferret.input.isMouseCaptured())
					Ferret.input.releaseMouse(window);
				else
					Ferret.input.captureMouse(window);

		}
	}

	public void changeScreen(String screenId) {
		currentGameScreen.onDispose();
		currentGameScreen = ferretGameScreens.get(screenId);
		currentGameScreen.onInit();
	}

	public double getFrameTime() {
		return frameTime;
	}

	public Window getWindow() {
		return window;
	}

	public FerretLua getLuaEngine() {
		return luaEngine;
	}

	@Override
	public void onDispose() {
		currentGameScreen.onDispose();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window.getWindowPointer());
		glfwDestroyWindow(window.getWindowPointer());

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

}
