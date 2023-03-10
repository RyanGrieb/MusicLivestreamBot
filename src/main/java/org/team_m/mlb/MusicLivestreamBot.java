package org.team_m.mlb;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Application;
import javafx.stage.Stage;

public class MusicLivestreamBot extends Application {

	// see - https://github.com/obs-websocket-community-projects/obs-websocket-java

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		// https://developers.google.com/youtube/v3/live/guides/rtmps-ingestion

		// String bip = "./songs/Chris Stapleton - Tennessee Whiskey (Official
		// Audio).mp3";
		// Media hit = new Media(new File(bip).toURI().toString());
		// MediaPlayer mediaPlayer = new MediaPlayer(hit);
		// mediaPlayer.play();

		// PlaylistDownloader.downloadPlaylist(
		// "https://www.youtube.com/watch?v=4zAThXFOy2c&list=PL3oW2tjiIxvQW6c-4Iry8Bpp3QId40S5S");

		// System.out.println(LivestreamPlayer.getRandomFile(System.getProperty("user.dir")
		// + "/songs", 0));
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
				"** For information on getting the stream key, see section 2.xx from the installation document **");
		System.out.println("Provide stream key for streaming on " + livestreamOption + ": ");

		livestreamKey = scanner.next();

		System.out.println("To start livestream press ENTER");

		try {
			System.in.read();
		} catch (Exception e) {
		}

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
			String songFile = LivestreamPlayer.getRandomFile(System.getProperty("user.dir") + "/songs", 0);
			String imageFile = LivestreamPlayer.getRandomFile(System.getProperty("user.dir") + "/images", 0);
			String songName = "";
			if(SystemInfo.osType().equals("Windows")) {
				songName = songFile.substring(songFile.lastIndexOf("songs\\") + 6, songFile.lastIndexOf("."));
			}else { // If linux..
				songName = songFile.substring(songFile.lastIndexOf("songs/") + 6, songFile.lastIndexOf("."));
			}

			LivestreamPlayer.livestreamVideo(songFile, imageFile, songName);
		}

		/*
		 * stage.setTitle("Music Livestream Bot"); var javaVersion =
		 * SystemInfo.javaVersion(); var javafxVersion = SystemInfo.javafxVersion();
		 * 
		 * var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java "
		 * + javaVersion + "."); var scene = new Scene(new StackPane(label), 640, 480);
		 * stage.setScene(scene); stage.show();
		 */
	}

}
