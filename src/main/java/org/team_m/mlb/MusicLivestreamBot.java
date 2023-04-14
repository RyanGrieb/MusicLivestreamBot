package org.team_m.mlb;

import java.util.ArrayList;

public class MusicLivestreamBot {

	private static Thread streamThread;

	public static void main(String[] args) {
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
}
