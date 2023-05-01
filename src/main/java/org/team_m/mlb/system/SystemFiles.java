package org.team_m.mlb.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Collection of helper methods to manipulate the file system.
 *
 */
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

	public static ArrayList<String> getFileNameList(String directory, String extension) {
		ArrayList<String> files = getFileNameList(directory);

		for (String file : new ArrayList<>(files)) {
			if (!file.contains("." + extension)) {
				files.remove(file);
			}
		}

		return files;
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

	public static boolean folderExists(String folderPath) {
		File folder = new File(folderPath);
		return folder.exists() && folder.isDirectory();
	}

	public static boolean fileExists(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.isFile();
	}

	public static boolean createFolder(String folderPath) {
		File folder = new File(folderPath);
		return folder.mkdirs();
	}

	public static void copyFileToPath(String sourceFilePath, String targetDirectoryPath) throws IOException {
		File sourceFile = new File(sourceFilePath);
		Path targetDirectory = Paths.get(targetDirectoryPath);

		// Create the target directory if it doesn't exist
		if (!Files.exists(targetDirectory)) {
			Files.createDirectories(targetDirectory);
		}

		// Get the target file path by appending the source file name to the target
		// directory path
		Path targetFilePath = targetDirectory.resolve(sourceFile.getName());

		// Copy the file to the target directory
		Files.copy(sourceFile.toPath(), targetFilePath);
	}

	public static void removeFileByName(String directoryPath, String fileName) {
		File directory = new File(directoryPath);
		File file = new File(directory, fileName);

		if (file.exists()) {
			boolean result = file.delete();
			if (result) {
				System.out.println("File deleted successfully");
			} else {
				System.out.println("Failed to delete the file");
			}
		} else {
			System.out.println("File not found in the directory");
		}
	}

	public static void downloadFromURL(String fileURL, String fileName, String destination, Consumer<Long> progressCallback) throws IOException {
	    URL url = new URL(fileURL);
	    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	    long fileSize = httpConn.getContentLengthLong();

	    // Create directories if they don't exist
	    new File(destination).mkdirs();

	    FileOutputStream outputStream = new FileOutputStream(destination + "/" + fileName);

	    InputStream inputStream = httpConn.getInputStream();
	    byte[] buffer = new byte[1024];
	    int bytesRead;
	    long totalBytesRead = 0;
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
	        outputStream.write(buffer, 0, bytesRead);
	        totalBytesRead += bytesRead;
	        long percentDone = (totalBytesRead * 100) / fileSize;
	        System.out.printf("Downloaded %d%%\n", percentDone);
	        progressCallback.accept(percentDone);
	    }

	    // Close the streams
	    inputStream.close();
	    outputStream.close();
	    httpConn.disconnect();
	}


	private static void mergeJSONObjects(JSONObject originalJson, JSONObject newJson) {
		for (String key : newJson.keySet()) {
			originalJson.put(key, newJson.get(key));
		}
	}
}
