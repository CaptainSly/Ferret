package io.azraein.ferret.system.lua;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import io.azraein.ferret.system.Ferret;
import io.azraein.ferret.system.gfx.BasicShapes;

public class FerretLib extends TwoArgFunction {

	@Override
	public LuaValue call(LuaValue module, LuaValue env) {
		LuaTable ferret = new LuaTable();
		ferret.set("_VERSION", Ferret.FERRET_VERSION);
		ferret.set("calendar", CoerceJavaToLua.coerce(Ferret.gameCalendar));
		ferret.set("registry", CoerceJavaToLua.coerce(Ferret.registry));

		// Ferret Graphics Module
		LuaTable gfx = new LuaTable();
		gfx.set("shapes", CoerceJavaToLua.coerce(BasicShapes.class));
		
		// Ferret Registry Module
		LuaTable registries = new LuaTable();
		
		// Ferret Player Module
		LuaTable player = new LuaTable();
		
		// Add Ferret Modules
		ferret.set("gfx", gfx);
		ferret.set("player", player);
		ferret.set("registries", registries);
		
		// Set Globals accordingly
		LuaUtils.setGlobal(env, "ferret", ferret);

		// Set as a Universal Global for the Simple Class Script (Converted into Java) ^_^
		LuaUtils.setGlobal(env, "class", new LuaClassFunction());
		return ferret;
	}

}
