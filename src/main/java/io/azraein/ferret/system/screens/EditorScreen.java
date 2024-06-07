package io.azraein.ferret.system.screens;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.utilities.Utils;

public class EditorScreen extends FerretScreen {

	public EditorScreen(Engine engine) {
		super(engine);
	}

	@Override
	public boolean handleGuiInput(Window window) {
		ImGuiIO imGuiIO = ImGui.getIO();
		Vector2f mousePos = Ferret.input.getCurrentMousePos();
		imGuiIO.setMousePos(mousePos.x, mousePos.y);
		imGuiIO.setMouseDown(0, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_1));
		imGuiIO.setMouseDown(1, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_2));

		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}

	@Override
	public void onInit() {
	}

	@Override
	public void onInput(float delta) {
	}

	@Override
	public void onUpdate(float delta) {
	}

	@Override
	public void onRender() {
	}

	@Override
	public void onImGuiRender() {
		ImGui.setNextWindowPos(0, 0);
		ImGui.setNextWindowSize(engine.getWindow().getWindowWidth(), engine.getWindow().getWindowHeight(),
				ImGuiCond.Always);
		ImGui.begin("Ferret Editor: FrameTime - " + Utils.formatToDecimalPlace(4, (float) engine.getFrameTime()),
				ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar);
		{
			ImGui.beginMenuBar();
			{
				if (ImGui.menuItem("TestScreen"))
					engine.changeScreen("testScreen");
			}
			ImGui.endMenuBar();

		}
		ImGui.end();
	}

	@Override
	public void onResize(int width, int height) {
	}

}
