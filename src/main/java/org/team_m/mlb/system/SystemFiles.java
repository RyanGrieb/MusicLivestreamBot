package org.team_m.mlb.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

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

		for (File file : fileList) {
			if (file.isFile()) {
				fileNameList.add(file.getName());
			}
		}

		return fileNameList;
	}

	public static JSONObject getJSONFromFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(file);
		JSONTokener jsonTokener = new JSONTokener(fileInputStream);
		JSONObject jsonObject = new JSONObject(jsonTokener);

		return jsonObject;
	}

	public static void createJSONFile(String fileName, JSONObject jsonObject) {
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(jsonObject.toString());
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateJSONFile(String fileName, JSONObject jsonObject) throws FileNotFoundException {
		JSONObject fileJson = getJSONFromFile(fileName);
		// Merge the JSON objects here...
		mergeJSONObjects(fileJson, jsonObject);
		createJSONFile(fileName, fileJson);
	}

	private static void mergeJSONObjects(JSONObject originalJson, JSONObject newJson) {
		for (String key : newJson.keySet()) {
			originalJson.put(key, newJson.get(key));
		}
	}
}
