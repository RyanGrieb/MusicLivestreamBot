package org.team_m.mlb;

import java.util.ArrayList;

import org.team_m.mlb.frame.DependencyDownloaderFrame;
import org.team_m.mlb.frame.PlayerFrame;
import org.team_m.mlb.system.SystemFiles;

/**
 * Main class for the program. Initializes the JFrame, and starts the
 * LivestreamPlayer through our callback methods.
 *
 */
public class MusicLivestreamBot {

	private static Thread streamThread;

	public static void main(String[] args) {
		PlayerFrame frame = new PlayerFrame();

		try {
			// Prompt the user if our external dependencies are missing (ffmpeg, yt-dlt)
			if (!SystemFiles.fileExists("./scripts/ffmpeg.exe")) {

				System.out.println("Missing ffmpeg dependency, downloading...");
				DependencyDownloaderFrame depDownloadFrame = new DependencyDownloaderFrame();
				depDownloadFrame.setDependencyName("ffmpeg");

				SystemFiles.downloadFromURL(
						"https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/ffmpeg.exe",
						"ffmpeg.exe", "./scripts", (precentDone) -> {
							depDownloadFrame.setPrecentDone(precentDone + "");
							if (precentDone >= 100) {
								depDownloadFrame.setVisible(false);
								depDownloadFrame.dispose();
							}
						});
			}

			if (!SystemFiles.fileExists("./scripts/yt-dlp.exe")) {
				System.out.println("Missing yt-dlp dependency, downloading...");

				DependencyDownloaderFrame depDownloadFrame = new DependencyDownloaderFrame();
				depDownloadFrame.setDependencyName("yt-dlp");

				SystemFiles.downloadFromURL(
						"https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/yt-dlp.exe",
						"yt-dlp.exe", "./scripts", (precentDone) -> {
							depDownloadFrame.setPrecentDone(precentDone + "");
							if (precentDone >= 100) {
								depDownloadFrame.setVisible(false);
								depDownloadFrame.dispose();
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
}
