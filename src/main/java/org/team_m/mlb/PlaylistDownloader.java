package org.team_m.mlb;

public class PlaylistDownloader {

	// String bip = "./songs/Chris Stapleton - Tennessee Whiskey (Official
	// Audio).mp3";
	// Media hit = new Media(new File(bip).toURI().toString());
	// MediaPlayer mediaPlayer = new MediaPlayer(hit);
	// mediaPlayer.play();

	// PlaylistDownloader.downloadPlaylist(
	// "https://www.youtube.com/watch?v=4zAThXFOy2c&list=PL3oW2tjiIxvQW6c-4Iry8Bpp3QId40S5S");

	// System.out.println(LivestreamPlayer.getRandomFile(System.getProperty("user.dir")
	// + "/songs", 0));
	
	/**
	 * Downloads YouTube playlist via yt-dlp: https://github.com/yt-dlp/yt-dlp
	 * 
	 * @param url YouTube URL
	 */
	public static void downloadPlaylist(String url) {

		// TODO:
		// Check operating system, run specific yt-dlp file based on type of OS.
		// Check if python3 is installed on machine if on linux.
		// Use portable python3 executable to run this script.

		System.out.println("Downloading: " + url);
		String command = "python3 ./src/main/resources/scripts/yt-dlp --extract-audio --audio-format mp3 -o songs/%(title)s.%(ext)s "
				+ url;
		
		CommandRunner commandRunner = new CommandRunner(command);
		commandRunner.run();
	}

}
