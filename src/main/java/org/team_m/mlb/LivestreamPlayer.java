package org.team_m.mlb;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.team_m.mlb.command.CommandRunner;
import org.team_m.mlb.system.SystemFiles;
import org.team_m.mlb.system.SystemInfo;

/**
 * The LivestreamPlayer class is a Singleton Object that aggregates images and
 * songs into a single ffmpeg commmand. We keep track of songs with a playlist,
 * and iterate through each song one at a time.
 *
 */
public class LivestreamPlayer implements Runnable {

	private static final LivestreamPlayer instance = new LivestreamPlayer();
	private static final String BITRATE = "5k";
	private static final int FPS = 1;
	private static final String QUALITY = "medium";
	private static final String YOUTUBE_RTMP = "rtmp://a.rtmp.youtube.com/live2";
	private static final String TWITCH_RTMP = "rtmp://dfw.contribute.live-video.net/app";

	private String streamURL;
	private String streamKey;
	private CommandRunner commandRunner;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Consumer<String> onSongChangedCallback;
	private ArrayList<String> songPlaylist;
	private int currentSongIndex;
	private float audioVolume = 1;

	public static LivestreamPlayer getInstance() {
		return instance;
	}

	public void run() {
		running.set(true);
		currentSongIndex = 0;

		while (running.get()) {

			// If currentSongIndex, it was triggered by skipToPreviousSong().
			// B/c of this, we just loop back to the end of the playlist
			if (currentSongIndex < 0) {
				currentSongIndex = songPlaylist.size() - 1;
			}

			String imageFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/images", 0);
			String rawSongName = songPlaylist.get(currentSongIndex);
			String songFile = System.getProperty("user.dir") + "/songs/" + rawSongName;
			String songName = rawSongName.substring(0, rawSongName.lastIndexOf(".mp3"));

			if (onSongChangedCallback != null) {
				onSongChangedCallback.accept(songName);
			}

			livestreamVideo(songFile, imageFile, songName);
			// Song has finished, increase songIndex to loop through our playlist
			currentSongIndex++;
			if (currentSongIndex >= songPlaylist.size()) {
				currentSongIndex = 0;
			}
		}
	}

	public void livestreamVideo(String soundSource, String imageSource, String songName) {
		String fontSource = null;

		switch (SystemInfo.osType()) {
		case "Windows":
			//ffmpegCommand = System.getProperty("user.dir") + "\\scripts\\ffmpeg.exe";
			fontSource = System.getProperty("user.dir") + "\\fonts\\times.ttf";
			break;
		case "Linux":
			//ffmpegCommand = "ffmpeg"; // TODO: Display error msg box if not installed
			fontSource = System.getProperty("user.dir") + "/fonts/times.ttf";
			break;
		}

		commandRunner = new CommandRunner(CommandRunner.FFMPEG_COMMAND);
		commandRunner.addArg("-y"); // Override output files (For real-time livestream)
		commandRunner.addArg("-re"); // Use native framerate (For real-time livestream) (Prevent the music loop from
										// skipping ahread to new song)
		commandRunner.addArg("-loop 1 -i \"" + imageSource + "\""); // Display static image
		commandRunner.addArg(String.format("-i \"%s\"", soundSource)); // Play mp3 sound source
		commandRunner.addArg(String.format("-af \"volume=%s\"", audioVolume)); // Set a custom volume
		commandRunner.addArg("-c:v libx264 -preset veryfast -b:v 500k -maxrate 500k -bufsize 1000k"); // Define preset
																										// render
																										// options
		commandRunner.addArg("-framerate 25 -g 50 -keyint_min 25 -shortest");
		commandRunner.addArg("-c:a aac -b:a 128k -ar 44100");
		commandRunner.addArg(String.format(
				"-vf \"format=yuv420p,pad=ceil(iw/2)*2:ceil(ih/2)*2,drawtext=text='%s':fontfile='%s':fontsize=24:fontcolor=white:x=10:y=20\"",
				songName, fontSource)); // Display song name, top left corner.
		commandRunner.addArg(String.format("-f flv %s/%s", streamURL, streamKey)); // Pass stream url & key to
																					// livestream video
		commandRunner.addArg("-threads " + SystemInfo.threadCount()); // Utilize all system threads
		commandRunner.run(true);
	}

	public void skipToPreviousSong() {

		currentSongIndex -= 2;

		if (commandRunner != null) {
			commandRunner.sendStopSignal("ffmpeg");
		}

	}

	public void skipToNextSong() {
		if (commandRunner != null) {
			commandRunner.sendStopSignal("ffmpeg");
		}
	}

	public void stop() {
		running.set(false);

		if (commandRunner != null) {
			commandRunner.sendStopSignal("ffmpeg");
		}
	}

	public void setOption(String option, Object value) {
		switch (option) {
		case "livestream":
			if (value.equals("youtube")) {
				streamURL = YOUTUBE_RTMP;
			}
			if (value.equals("twitch")) {
				streamURL = TWITCH_RTMP;
			}

			break;
		case "stream_key":
			streamKey = (String) value;
			break;
		case "playlist":
			songPlaylist = (ArrayList<String>) value;
			break;
		case "volume":
			audioVolume = (float) value;
		}
	}

	public boolean isLive() {
		return running.get();
	}

	public void onSongChanged(Consumer<String> callback) {
		this.onSongChangedCallback = callback;
	}
}
