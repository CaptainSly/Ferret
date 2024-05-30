package io.azraein.ferret.interfaces;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Window;

public interface GuiInstance {

	default void onRenderUi() {
		ImGui.newFrame();
		ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
		ImGui.showDemoWindow();
		ImGui.endFrame();
		ImGui.render();
	}

	default boolean handleGuiInput(Window window) {
		ImGuiIO imGuiIO = ImGui.getIO();
		Vector2f mousePos = Ferret.input.getCurrentMousePos();
		imGuiIO.setMousePos(mousePos.x, mousePos.y);
		imGuiIO.setMouseDown(0, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_1));
		imGuiIO.setMouseDown(1, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_2));

		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}

}
