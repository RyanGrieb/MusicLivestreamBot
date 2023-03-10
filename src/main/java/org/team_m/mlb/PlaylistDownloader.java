package org.team_m.mlb;

public class PlaylistDownloader {

	/**
	 * Downloads YouTube playlist via yt-dlp: https://github.com/yt-dlp/yt-dlp
	 * 
	 * @param url YouTube URL
	 */
	public static void downloadPlaylist(String url) {

		// TODO:
		// Check operating system, run specific yt-dlp file based on type of OS.
		// Check if python3 is installed on machine if on linux.

		System.out.println("Downloading: " + url);
		String command = "python3 ./src/main/resources/scripts/yt-dlp --extract-audio --audio-format mp3 -o songs/%(title)s.%(ext)s "
				+ url;
		
		CommandRunner commandRunner = new CommandRunner(command);
		commandRunner.run();
	}

}
