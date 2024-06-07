package io.azraein.ferret.interfaces;

import io.azraein.ferret.system.gfx.Window;

public interface GuiInstance {

	void onRenderUi();

	boolean handleGuiInput(Window window);
}
