package org.team_m.mlb;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.team_m.mlb.system.SystemFiles;
import org.team_m.mlb.system.SystemInfo;

import javafx.stage.Stage;

public class MusicLivestreamBot {

	public static void main(String[] args) {
		String songsDirectory = System.getProperty("user.dir") + "/songs";
		String imagesDirectory = System.getProperty("user.dir") + "/images";
		ArrayList<String> songNames = SystemFiles.getFileNameList(songsDirectory);
		ArrayList<String> imageNames = SystemFiles.getFileNameList(imagesDirectory);

		PlayerFrame frame = new PlayerFrame();
		frame.setAvailableSongsList(songNames);
		frame.setAvailableImages(imageNames);

		frame.onGoLiveButtonClicked((Void) -> {
			String streamKey = frame.getStreamKey();
			String platform = frame.getSelectedPlatform().toLowerCase();

			LivestreamPlayer.setOption("livestream", platform);
			LivestreamPlayer.setOption("stream_key", streamKey);
			
			AtomicBoolean shouldContinueRunning = new AtomicBoolean(true);

			// Add a shutdown hook to set the flag to false when the program exits
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					shouldContinueRunning.set(false);
					LivestreamPlayer.stop();
				}
			});

			while (shouldContinueRunning.get()) {
				String songFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/songs", 0);
				String imageFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/images", 0);
				String songName = "";
				if (SystemInfo.osType().equals("Windows")) {
					songName = songFile.substring(songFile.lastIndexOf("songs\\") + 6, songFile.lastIndexOf("."));
				} else { // If linux..
					songName = songFile.substring(songFile.lastIndexOf("songs/") + 6, songFile.lastIndexOf("."));
				}

				LivestreamPlayer.livestreamVideo(songFile, imageFile, songName);
			}
		});
	}

	@Deprecated
	public void start(Stage stage) throws Exception {
		boolean validNumber = false;

		// General input variable
		String input = "";

		// Parsed input variables
		int livestreamIndex = -1;
		String livestreamKey = "N/A";

		System.out.println("Livestream Options: ");
		System.out.println("YouTube - 0");
		System.out.println("Twitch - 1");
		System.out.println("Enter 0 or 1 to choose the livestream option:");

		Scanner scanner = new Scanner(System.in);
		while (!validNumber) {
			input = scanner.next();
			try {
				livestreamIndex = Integer.parseInt(input);
			} catch (Exception e) {
				System.out.println("Error: Invalid input, enter 0 or 1.");
				continue;
			}
			if (livestreamIndex < 0 || livestreamIndex > 1) {
				System.out.println("Error: Inavlid number, enter 0 or 1.");
				continue;
			}

			validNumber = true;
		}

		String[] livestreamOptions = { "youtube", "twitch" };
		String livestreamOption = livestreamOptions[livestreamIndex];

		System.out.println(
				"** For information on getting the stream key, see section 2.2 from the installation document **");
		System.out.println("Provide stream key for streaming on " + livestreamOption + ": ");

		livestreamKey = scanner.next();

		System.out.println("To start livestream press ENTER");

		try {
			System.in.read();
		} catch (Exception e) {
		}

		scanner.close();

		LivestreamPlayer.setOption("livestream", livestreamOption);
		LivestreamPlayer.setOption("stream_key", livestreamKey);

		AtomicBoolean shouldContinueRunning = new AtomicBoolean(true);

		// Add a shutdown hook to set the flag to false when the program exits
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				shouldContinueRunning.set(false);
				LivestreamPlayer.stop();
			}
		});

		while (shouldContinueRunning.get()) {
			String songFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/songs", 0);
			String imageFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/images", 0);
			String songName = "";
			if (SystemInfo.osType().equals("Windows")) {
				songName = songFile.substring(songFile.lastIndexOf("songs\\") + 6, songFile.lastIndexOf("."));
			} else { // If linux..
				songName = songFile.substring(songFile.lastIndexOf("songs/") + 6, songFile.lastIndexOf("."));
			}

			LivestreamPlayer.livestreamVideo(songFile, imageFile, songName);
		}
	}

}
