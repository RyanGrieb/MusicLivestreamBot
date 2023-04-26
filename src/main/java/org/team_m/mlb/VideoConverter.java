package org.team_m.mlb;

import java.util.function.Consumer;

public class VideoConverter {

	private static VideoConverter instance = new VideoConverter();

	private CommandRunner commandRunner;
	private Consumer<String> onConvertCommandOutput;

	public static VideoConverter getInstance() {
		return instance;
	}

	public void onConvertCommadnOutput(Consumer<String> callback) {
		this.onConvertCommandOutput = callback;
	}

	public void videoURLToMp3(String videoURL) {

		String ytDLPCommand = System.getProperty("user.dir") + "\\scripts\\yt-dlp.exe";

		commandRunner = new CommandRunner(ytDLPCommand);
		commandRunner.addArg("--output " + System.getProperty("user.dir") + "\\songs\\%(title)s.%(ext)s");
		// commandRunner.addArg("--output " + System.getProperty("user.dir") +
		// "\\songs.%(ext)s");
		commandRunner.addArg("--extract-audio");
		commandRunner.addArg("--audio-format");
		commandRunner.addArg("mp3 " + videoURL);

		if (onConvertCommandOutput != null) {
			commandRunner.addOutputCallback(onConvertCommandOutput);
		}

		commandRunner.run(false);
	}

	public void cancelConversion() {
		commandRunner.sendStopSignal("yt-dlp");
	}
}
