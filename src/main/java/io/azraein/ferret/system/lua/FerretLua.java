package io.azraein.ferret.system.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.*;

public class FerretLua {
	
	private Globals globals = stdFerretGlobals();

	public void runScript(String filename) {
		LuaValue chunk = globals.loadfile(filename);
		chunk.call();
	}
	
	public static Globals stdFerretGlobals() {
		Globals globals = new Globals();
		globals.load(new JseBaseLib());
		globals.load(new PackageLib());
		globals.load(new Bit32Lib());
		globals.load(new TableLib());
		globals.load(new StringLib());
		globals.load(new CoroutineLib());
		globals.load(new JseMathLib());
		globals.load(new JseIoLib());
		globals.load(new JseOsLib());
		globals.load(new LuajavaLib());
		globals.load(new FerretLib());		
		LoadState.install(globals);
		LuaC.install(globals);
		return globals;		
	}

}
