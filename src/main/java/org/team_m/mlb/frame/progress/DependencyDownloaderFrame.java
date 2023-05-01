package org.team_m.mlb.frame.progress;

public class DependencyDownloaderFrame extends ProgressFrame {

	public DependencyDownloaderFrame(String dependencyName) {
		super("Download Required Dependencies", "Downloading dependency: " + dependencyName);
	}

}
