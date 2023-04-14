package org.team_m.mlb.system;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class AudioFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		return file.getName().toLowerCase().endsWith(".mp3") || file.isDirectory();
	}

	@Override
	public String getDescription() {
		return "MP3 files (*.mp3)";
	}

}
