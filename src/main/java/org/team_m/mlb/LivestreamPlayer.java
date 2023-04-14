package org.team_m.mlb;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.team_m.mlb.system.SystemFiles;
import org.team_m.mlb.system.SystemInfo;

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

	public static LivestreamPlayer getInstance() {
		return instance;
	}

	public void run() {
		running.set(true);

		while (running.get()) {
			String songFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/songs", 0);
			String imageFile = SystemFiles.getRandomFile(System.getProperty("user.dir") + "/images", 0);
			String songName = "";
			if (SystemInfo.osType().equals("Windows")) {
				songName = songFile.substring(songFile.lastIndexOf("songs\\") + 6, songFile.lastIndexOf("."));
			} else { // If linux..
				songName = songFile.substring(songFile.lastIndexOf("songs/") + 6, songFile.lastIndexOf("."));
			}

			if (onSongChangedCallback != null) {
				onSongChangedCallback.accept(songName);
			}

			livestreamVideo(songFile, imageFile, songName);
		}
	}

	public void livestreamVideo(String soundSource, String imageSource, String songName) {
		String ffmpegCommand = null;
		String fontSource = null;

		switch (SystemInfo.osType()) {
		case "Windows":
			ffmpegCommand = System.getProperty("user.dir") + "\\scripts\\ffmpeg.exe";
			fontSource = System.getProperty("user.dir") + "\\fonts\\times.ttf";
			break;
		case "Linux":
			ffmpegCommand = "ffmpeg"; // TODO: Display error msg box if not installed
			fontSource = System.getProperty("user.dir") + "/fonts/times.ttf";
			break;
		}

		commandRunner = new CommandRunner(ffmpegCommand);
		commandRunner.addArg("-y"); // Override output files (For real-time livestream)
		commandRunner.addArg("-re"); // Use native framerate (For real-time livestream) (Prevent the music loop from
										// skipping ahread to new song)
		commandRunner.addArg("-loop 1 -i \"" + imageSource + "\""); // Display static image
		commandRunner.addArg(String.format("-i \"%s\"", soundSource));
		commandRunner.addArg("-c:v libx264 -preset veryfast -b:v 500k -maxrate 500k -bufsize 1000k");
		commandRunner.addArg("-framerate 25 -g 50 -keyint_min 25 -shortest");
		commandRunner.addArg("-c:a aac -b:a 128k -ar 44100");
		commandRunner.addArg(String.format(
				"-vf \"format=yuv420p,pad=ceil(iw/2)*2:ceil(ih/2)*2,drawtext=text='%s':fontfile='%s':fontsize=24:fontcolor=white:x=10:y=20\"",
				songName, fontSource));
		commandRunner.addArg(String.format("-f flv %s/%s", streamURL, streamKey));
		commandRunner.addArg("-threads " + SystemInfo.threadCount());
		commandRunner.run();
	}

	public void stop() {
		running.set(false);

		if (commandRunner != null) {
			commandRunner.sendStopSignal();
		}
	}

	public void setOption(String option, String value) {
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
			streamKey = value;
			break;
		}
	}

	/**
	 * Uses ffmpeg to convert a jpg and mp3 to a mp4 file
	 * 
	 * @param imagePath
	 * @param mp3Path
	 */
	public void songImageToMp4(String imagePath, String mp3Path) {
		// ffmpeg -loop 1 -i image.jpg -i one.mp3 -shortest -acodec copy -vcodec mjpeg
		// result.mkv
	}

	public boolean isLive() {
		return running.get();
	}

	public void onSongChanged(Consumer<String> callback) {
		this.onSongChangedCallback = callback;
	}
}
