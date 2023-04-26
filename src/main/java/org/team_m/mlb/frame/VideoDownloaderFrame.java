package org.team_m.mlb.frame;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class VideoDownloaderFrame extends JFrame {

	private JPanel contentPane;
	private JLabel lblProgress;
	private JProgressBar progressBar;
	private JTextArea txtrSongName;
	private Consumer<Void> cancelCallback;

	/**
	 * Create the frame.
	 */
	public VideoDownloaderFrame() {
		setResizable(false);
		setTitle("Converting Song");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 188);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 75, 202, 26);
		contentPane.add(progressBar);

		txtrSongName = new JTextArea();
		txtrSongName.setEditable(false);
		txtrSongName.setLineWrap(true);
		txtrSongName.setText("Converting Song: ");
		txtrSongName.setBackground(SystemColor.control);
		txtrSongName.setBounds(10, 11, 237, 53);
		contentPane.add(txtrSongName);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (cancelCallback != null) {
					cancelCallback.accept(null);
				}
			}
		});
		btnCancel.setBounds(103, 122, 89, 23);
		contentPane.add(btnCancel);

		lblProgress = new JLabel("0%");
		lblProgress.setBounds(222, 83, 95, 14);
		contentPane.add(lblProgress);

		Border blackline = BorderFactory.createLineBorder(Color.black);

		contentPane.setBorder(blackline);

		setUndecorated(true);
		setLocationRelativeTo(null); // Center window to center of screen.
		setVisible(true);
	}

	public void setPrecentDone(String precentDone) {

		lblProgress.setText(precentDone + "%");
		progressBar.setValue((int) Float.parseFloat(precentDone));
	}

	public void setSongName(String songName) {
		txtrSongName.setText("Converting song: " + songName);
	}

	public void onCancel(Consumer<Void> callback) {
		cancelCallback = callback;
	}
}
