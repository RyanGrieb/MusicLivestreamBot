package org.team_m.mlb.system;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/*
 * File-filter class for JFileChooser, where we specify only jpg and png images.
 */
public class ImageFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		String fileName = file.getName().toLowerCase();
		return fileName.endsWith(".jpg") || fileName.endsWith(".png") || file.isDirectory();
	}

	@Override
	public String getDescription() {
		return "JPG and PNG files (*.jpg, *.png)";
	}
}
