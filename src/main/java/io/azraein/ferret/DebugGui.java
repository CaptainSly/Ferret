package io.azraein.ferret;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiWindowFlags;
import io.azraein.ferret.system.Engine;
import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.Window;
import io.azraein.ferret.system.screens.FerretScreen;
import io.azraein.ferret.system.utilities.Utils;

// USE THIS FOR ANY DEBUGGING SHIZ

public class DebugGui {

	public static void drawGui(FerretScreen screen, Engine engine) {
		ImGui.setNextWindowPos(0, 0);
		ImGui.begin("Ferret Debug Panel", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.MenuBar
				| ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove);
		{
			// FDP - MenuBar
			ImGui.beginMenuBar();
			{
				// Here if we need a menu bar, not entirely sure as of yet.
				if (ImGui.menuItem("Editor"))
					engine.changeScreen("editorScreen");
			}
			ImGui.endMenuBar();
			ImGui.separator();
			// Header
			ImGui.text(Ferret.FERRET_VERSION);
			ImGui.text("Frame Time: " + Utils.formatToDecimalPlace(4, (float) engine.getFrameTime()));
			ImGui.separator();

			// Calendar Quick Labels
			ImGui.text("Calendar Time: " + Ferret.gameCalendar.getTimeAsString(false));
			ImGui.text("Calendar Date: " + Ferret.gameCalendar.getDateAsString());
			ImGui.text("Calendar Normalized Hour: " + Ferret.gameCalendar.getNormalizedHourValue());
			ImGui.separator();

		}
		ImGui.end();
	}

	public static boolean handleInput(Window window) {
		ImGuiIO imGuiIO = ImGui.getIO();
		Vector2f mousePos = Ferret.input.getCurrentMousePos();
		imGuiIO.setMousePos(mousePos.x, mousePos.y);
		imGuiIO.setMouseDown(0, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_1));
		imGuiIO.setMouseDown(1, Ferret.input.isMouseBtnDown(GLFW.GLFW_MOUSE_BUTTON_2));

		return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
	}
}
