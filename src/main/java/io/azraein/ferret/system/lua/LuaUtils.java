package io.azraein.ferret.system.lua;

import org.luaj.vm2.LuaValue;

public class LuaUtils {

	public static void setGlobal(LuaValue env, String globalName, LuaValue globalValue) {
		env.set(globalName, globalValue);
		env.get("package").get("loaded").set(globalName, globalValue);
	}
	
}
