package org.team_m.mlb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.team_m.mlb.command.CommandRunner;
import org.team_m.mlb.command.ExternalCommand;
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

	private static boolean hasValidExternalDependency(ExternalCommand command, String argument, String validOutput) {
		return false;
	}

	private static boolean hasValidExternalDependency1(String dependencyName) {
		CountDownLatch latch = new CountDownLatch(2);

		CommandRunner runner = new CommandRunner(CommandRunner.FFMPEG_COMMAND, "-version");
		AtomicBoolean isFFmpegValid = new AtomicBoolean(false);

		runner.addOutputCallback((output) -> {
			boolean validVersion = output.contains("ffmpeg version N-");
			if (validVersion) {
				isFFmpegValid.set(true);
			}
			latch.countDown();
		});
		runner.run(true);

		runner = new CommandRunner(CommandRunner.YT_DLP_COMMAND, "--version");
		AtomicBoolean isYtDlpValid = new AtomicBoolean(false);

		runner.addOutputCallback((output) -> {
			boolean validVersion = output.contains("2023.");
			if (validVersion) {
				isYtDlpValid.set(true);
			}
			latch.countDown();
		});
		runner.run(true);

		// Check if both our commands have valid versions, if not execute the code below
		// to re-download them.
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static void downloadDependency(String url, String dependencyName) throws IOException {
		System.out.println("Missing ffmpeg dependency, downloading...");
		DependencyDownloaderFrame frame = new DependencyDownloaderFrame(dependencyName);

		SystemFiles.downloadFromURL(url, dependencyName + ".exe", "./scripts", (precentDone) -> {
			frame.setPrecentDone(precentDone);
			if (precentDone >= 100) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
	}

	public static void main(String[] args) {
		PlayerFrame frame = new PlayerFrame();

		try {
			// Prompt the user if our external dependencies are missing (ffmpeg, yt-dlt)
			if (!SystemFiles.fileExists("./scripts/ffmpeg.exe") || !hasValidExternalDependency("ffmpeg")) {

				System.out.println("Missing ffmpeg dependency, downloading...");
				downloadDependency(
						"https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/ffmpeg.exe",
						"ffmpeg");
			}

			if (!SystemFiles.fileExists("./scripts/yt-dlp.exe") || !hasValidExternalDependency("yt-dlp")) {
				System.out.println("Missing yt-dlp dependency, downloading...");

				downloadDependency(
						"https://github.com/RyanGrieb/MusicLivestreamBot/releases/download/v1.0.0/yt-dlp.exe",
						"yt-dlp");
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
