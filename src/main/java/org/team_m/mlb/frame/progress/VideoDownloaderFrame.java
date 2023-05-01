package org.team_m.mlb.frame.progress;

public class VideoDownloaderFrame extends ProgressFrame {

	public VideoDownloaderFrame() {
		super("Converting Song", "Fetching video information...");
	}

	public void setSongName(String songName) {
		this.setDisplayText("Downloading: " + songName);
	}

}
