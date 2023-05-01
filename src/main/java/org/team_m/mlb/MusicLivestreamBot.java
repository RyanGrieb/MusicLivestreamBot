package org.team_m.mlb;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.team_m.mlb.command.CommandRunner;
import org.team_m.mlb.frame.PlayerFrame;
import org.team_m.mlb.frame.progress.DependencyDownloaderFrame;
import org.team_m.mlb.system.SystemFiles;

/**
 * Main class for the program. Initializes the JFrame, and starts the
 * LivestreamPlayer through our callback methods.
 *
 */
public class MusicLivestreamBot {

	private static Thread streamThread;

	public static void main(String[] args) {

		checkDependencies();

		PlayerFrame frame = new PlayerFrame();

		LivestreamPlayer.getInstance().onSongChanged((songName) -> {
			frame.setPlayingSong(songName);
		});

		frame.onGoLiveButtonClicked((Void) -> {
			String streamKey = frame.getStreamKey();
			String platform = frame.getSelectedPlatform().toLowerCase();
			ArrayList<String> songs = frame.getSongPlaylist();

			boolean streamState = frame.getStreamState();

			if (streamState) {
				LivestreamPlayer.getInstance().setOption("livestream", platform);
				LivestreamPlayer.getInstance().setOption("stream_key", streamKey);
				LivestreamPlayer.getInstance().setOption("playlist", songs);

				streamThread = new Thread(LivestreamPlayer.getInstance());
				streamThread.start();
			} else {
				LivestreamPlayer.getInstance().stop();
				streamThread = null;
				frame.setPlayingSong("N/A");
			}
		});
	}

	private static void checkDependencies() {
		// Prompt the user if our external dependencies are missing (ffmpeg, yt-dlt)
		if (!SystemFiles.fileExists("./scripts/ffmpeg.exe") || !hasValidExternalDependency("ffmpeg")) {
			downloadDependency("https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/ffmpeg.exe",
					"ffmpeg");
		}

		if (!SystemFiles.fileExists("./scripts/yt-dlp.exe") || !hasValidExternalDependency("yt-dlp")) {
			downloadDependency("https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/yt-dlp.exe",
					"yt-dlp");
		}

		// Check the hash again to validate we downloaded the proper executable.
		if (!hasValidExternalDependency("ffmpeg") || !hasValidExternalDependency("yt-dlp")) {
			JOptionPane.showMessageDialog(null,
					"Error: Could not validate dependency hash.\n\nDo you have an outdated version?", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private static boolean hasValidExternalDependency(String dependencyName) {
		switch (dependencyName) {
		case "ffmpeg":
			return SystemFiles.getFileHash(CommandRunner.FFMPEG_COMMAND).equals(CommandRunner.FFMPEG_HASH);
		case "yt-dlp":
			return SystemFiles.getFileHash(CommandRunner.YT_DLP_COMMAND).equals(CommandRunner.YT_DLP_HASH);
		}
		return false;
	}

	private static void downloadDependency(String url, String dependencyName) {
		try {
			System.out.println("Downloading dependency: " + dependencyName + "\nFrom: " + url);

			DependencyDownloaderFrame frame = new DependencyDownloaderFrame(dependencyName);

			SystemFiles.downloadFromURL(url, dependencyName + ".exe", "./scripts", (precentDone) -> {
				frame.setPrecentDone(precentDone);
				if (precentDone >= 100) {
					frame.setVisible(false);
					frame.dispose();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
