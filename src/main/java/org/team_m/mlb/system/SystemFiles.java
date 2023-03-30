package org.team_m.mlb.system;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SystemFiles {
	/**
	 * Selects a random file from a specified directory
	 * 
	 * @param path        Path of the directory.
	 * @param repeatLimit Prevent getting the same random file in a specified limit.
	 * @return
	 */
	public static String getRandomFile(String path, int repeatLimit) {
		// TODO: Implement repeatLimit
		Random rnd = new Random();
		File directory = new File(path);
		File[] files = directory.listFiles();

		return files[rnd.nextInt(files.length)].getAbsolutePath();
	}

	public static ArrayList<String> getFileNameList(String directory) {
		File dir = new File(directory);
		File[] fileList = dir.listFiles();
		ArrayList<String> fileNameList = new ArrayList<String>();
		
		for(File file : fileList) {
			if(file.isFile()) {
				fileNameList.add(file.getName());
			}
		}
		
		return fileNameList;
	}
}
