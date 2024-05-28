package io.azraein.ferret.system.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static String fileToString(String filename) throws IOException {
		File f = new File(filename);
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String script = "";
		
		String line = "";
		
		while ((line = br.readLine()) != null) {
			script += line + "\n";
		}
		
		br.close();
		return script;
	}
	
}
