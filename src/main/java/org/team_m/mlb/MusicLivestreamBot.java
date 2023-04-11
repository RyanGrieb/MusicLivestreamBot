package org.team_m.mlb;

import java.util.ArrayList;

import org.team_m.mlb.system.SystemFiles;

public class MusicLivestreamBot {

	public static void main(String[] args) {
		String songsDirectory = System.getProperty("user.dir") + "/songs";
		String imagesDirectory = System.getProperty("user.dir") + "/images";
		ArrayList<String> songNames = SystemFiles.getFileNameList(songsDirectory);
		ArrayList<String> imageNames = SystemFiles.getFileNameList(imagesDirectory);

		PlayerFrame frame = new PlayerFrame();
		frame.setAvailableSongsList(songNames);
		frame.setAvailableImages(imageNames);

		frame.onGoLiveButtonClicked((Void) -> {
			String streamKey = frame.getStreamKey();
			String platform = frame.getSelectedPlatform().toLowerCase();

			LivestreamPlayer.getInstance().setOption("livestream", platform);
			LivestreamPlayer.getInstance().setOption("stream_key", streamKey);

			Thread thread = new Thread(LivestreamPlayer.getInstance());
			thread.start();
		});
	}
}
