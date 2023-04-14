package org.team_m.mlb;

import java.util.ArrayList;

import org.team_m.mlb.system.SystemFiles;

public class MusicLivestreamBot {

	private static Thread streamThread;

	public static void main(String[] args) {
		String songsDirectory = System.getProperty("user.dir") + "/songs";
		String imagesDirectory = System.getProperty("user.dir") + "/images";
		ArrayList<String> songNames = SystemFiles.getFileNameList(songsDirectory);
		ArrayList<String> imageNames = SystemFiles.getFileNameList(imagesDirectory);

		PlayerFrame frame = new PlayerFrame();
		frame.setAvailableSongsList(songNames);
		frame.setAvailableImages(imageNames);

		LivestreamPlayer.getInstance().onSongChanged((songName) -> {
			System.out.println("Now playing " + songName);
			frame.setPlayingSong(songName);
		});

		frame.onGoLiveButtonClicked((Void) -> {
			String streamKey = frame.getStreamKey();
			String platform = frame.getSelectedPlatform().toLowerCase();
			boolean streamState = frame.getStreamState();

			if (streamState) {
				LivestreamPlayer.getInstance().setOption("livestream", platform);
				LivestreamPlayer.getInstance().setOption("stream_key", streamKey);

				streamThread = new Thread(LivestreamPlayer.getInstance());
				streamThread.start();
			} else {
				// Stop streamThread
				LivestreamPlayer.getInstance().stop();
				streamThread = null;
				frame.setPlayingSong("N/A");
			}
		});
	}
}
