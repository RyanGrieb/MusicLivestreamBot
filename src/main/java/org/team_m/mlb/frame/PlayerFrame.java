package org.team_m.mlb.frame;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;
import org.team_m.mlb.LivestreamPlayer;
import org.team_m.mlb.VideoConverter;
import org.team_m.mlb.system.AudioFileFilter;
import org.team_m.mlb.system.ImageFileFilter;
import org.team_m.mlb.system.SystemFiles;

/**
 * The PlayerFrame class provides a UI for the end-user to create and modify
 * song playlists and images. This UI also displays helpful information relating
 * to available songs, and what is currently playing.
 */
public class PlayerFrame extends JFrame {

	private static final long serialVersionUID = -682428546878821527L;
	private JPanel contentPane;

	private JList<String> listAvailableSongs;
	private JList<String> listAvailableImages;
	private JList<String> listLivestreamPlaylist;
	private Consumer<Void> goLiveCallback;

	private ButtonGroup radioButtonGroup;
	private String streamKey;
	private JButton btnGoLive;
	private JTextArea txtCurrentlyPlaying;
	private JLabel volumeLabel;

	public PlayerFrame() {
		setResizable(false);
		setTitle("Music Livestream Bot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 786, 530);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmNewMenuItem = new JMenuItem("Add Image...");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importImageFromFiles();
			}
		});
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnNewMenu_1 = new JMenu("Add Song");
		mnNewMenu.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("From Files...");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importSongFromFiles();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("From Youtube");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importSongFromYouTube();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_1);

		JMenu mnNewMenu_2 = new JMenu("Options");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Edit Stream Keys...");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Create dialog with two buttons, YouTube or Twitch

				String[] options = { "Youtube", "Twitch" };
				int selectedOption = JOptionPane.showOptionDialog(null, "Select a platform", "Edit Stream Keys",
						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (selectedOption == 0) {
					// YouTube option selected
					promptForStreamKey("Youtube");

				} else if (selectedOption == 1) {
					// Twitch option selected
					promptForStreamKey("Twitch");
				}
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_3);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 397, 103);
		contentPane.add(scrollPane);

		listAvailableSongs = new JList<String>();
		scrollPane.setViewportView(listAvailableSongs);
		listAvailableSongs.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listAvailableSongs.setBackground(UIManager.getColor("Button.background"));
		listAvailableSongs.setValueIsAdjusting(true);

		JLabel lblNewLabel = new JLabel("Available Songs:");
		lblNewLabel.setBounds(10, 11, 143, 14);
		contentPane.add(lblNewLabel);

		JButton btnNewButton = new JButton("Import Song");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String[] options = { "From Files", "From YouTube" };
				int selectedOption = JOptionPane.showOptionDialog(null, "Select an import option", "Import Song",
						JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				if (selectedOption == 0) {
					importSongFromFiles();

				} else if (selectedOption == 1) {
					importSongFromYouTube();
				}

			}
		});
		btnNewButton.setBounds(153, 7, 116, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Remove Song");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listAvailableSongs.getSelectedIndex() < 0) {
					JOptionPane.showMessageDialog(null, "Please select/click a song in 'available songs' to remove.",
							"Error: No song selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete:\n" + listAvailableSongs.getSelectedValue(), "Confirmation",
						JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					SystemFiles.removeFileByName("./songs", listAvailableSongs.getSelectedValue());
					updateAvailableSongs();
				}
			}
		});
		btnNewButton_1.setBounds(291, 7, 116, 23);
		contentPane.add(btnNewButton_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 189, 397, 103);
		contentPane.add(scrollPane_1);

		listLivestreamPlaylist = new JList<String>();
		scrollPane_1.setViewportView(listLivestreamPlaylist);
		listLivestreamPlaylist.setValueIsAdjusting(true);
		listLivestreamPlaylist.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listLivestreamPlaylist.setBackground(SystemColor.menu);

		JLabel lblNewLabel_1 = new JLabel("Song Playlist:");
		lblNewLabel_1.setBounds(20, 159, 123, 14);
		contentPane.add(lblNewLabel_1);

		JButton btnNewButton_1_1 = new JButton("Remove Song");
		btnNewButton_1_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (listLivestreamPlaylist.getSelectedIndex() < 0) {
					// Alert user to select a song from available songs
					JOptionPane.showMessageDialog(null, "Please select/click a song in 'song playlist' to remove.",
							"Error: No song selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				removeJListElement(listLivestreamPlaylist, listLivestreamPlaylist.getSelectedIndex());
				listLivestreamPlaylist.updateUI();
			}
		});
		btnNewButton_1_1.setBounds(291, 155, 116, 23);
		contentPane.add(btnNewButton_1_1);

		JButton btnNewButton_2 = new JButton("Add Song");
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (listAvailableSongs.getSelectedIndex() < 0) {
					// Alert user to select a song from available songs
					JOptionPane.showMessageDialog(null, "Please select/click an available song.",
							"Error: No song selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (jListHasValue(listLivestreamPlaylist, listAvailableSongs.getSelectedValue())) {
					JOptionPane.showMessageDialog(null, "This song is already added.", "Error: Song already exists",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				ArrayList<String> songs = new ArrayList<String>();

				ListModel<String> model = listLivestreamPlaylist.getModel();
				for (int i = 0; i < model.getSize(); i++) {
					songs.add(model.getElementAt(i));
				}

				songs.add(listAvailableSongs.getSelectedValue());

				setJListValues(listLivestreamPlaylist, songs);
			}
		});
		btnNewButton_2.setBounds(153, 155, 116, 23);
		contentPane.add(btnNewButton_2);

		txtCurrentlyPlaying = new JTextArea();
		txtCurrentlyPlaying.setBackground(UIManager.getColor("Button.background"));
		txtCurrentlyPlaying.setEditable(false);
		txtCurrentlyPlaying.setLineWrap(true);
		txtCurrentlyPlaying.setText("Currently Playing:\r\nN/A");
		txtCurrentlyPlaying.setBounds(10, 303, 326, 70);
		contentPane.add(txtCurrentlyPlaying);

		JLabel label = new JLabel("New label");
		label.setBounds(394, 384, -241, 39);
		contentPane.add(label);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(477, 36, 283, 256);
		contentPane.add(scrollPane_2);

		listAvailableImages = new JList<String>();
		scrollPane_2.setViewportView(listAvailableImages);
		listAvailableImages.setValueIsAdjusting(true);
		listAvailableImages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listAvailableImages.setBackground(SystemColor.menu);

		JLabel lblAvailableImages = new JLabel("Images:");
		lblAvailableImages.setBounds(477, 11, 80, 14);
		contentPane.add(lblAvailableImages);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Stream Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(427, 308, 333, 155);
		contentPane.add(panel);
		panel.setLayout(null);

		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBounds(6, 16, 159, 133);
		panel_2_1.setToolTipText("");
		panel_2_1.setBorder(new TitledBorder(null, "Platforms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panel_2_1);
		panel_2_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Panel panel_1_1 = new Panel();
		panel_2_1.add(panel_1_1);

		radioButtonGroup = new ButtonGroup();

		JRadioButton rbPlatformYouTube_1 = new JRadioButton("Youtube");
		rbPlatformYouTube_1.setSelected(true);
		panel_1_1.add(rbPlatformYouTube_1);

		JRadioButton rbPlatformTwitch_1 = new JRadioButton("Twitch");
		panel_1_1.add(rbPlatformTwitch_1);
		radioButtonGroup.add(rbPlatformYouTube_1);
		radioButtonGroup.add(rbPlatformTwitch_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(165, 16, 161, 133);
		panel.add(panel_2);
		panel_2.setToolTipText("");
		panel_2.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Audio",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setLayout(null);

		JSlider volumeSlider = new JSlider();
		volumeSlider.setValue(100);
		volumeSlider.setBounds(10, 40, 141, 26);

		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (!source.getValueIsAdjusting()) {
				int value = source.getValue();
				volumeLabel.setText("Volume: " + Integer.toString(value) + "%");
				LivestreamPlayer.getInstance().setOption("volume", (float) value / 100);
				// }
			}
		});
		panel_2.add(volumeSlider);

		volumeLabel = new JLabel("Volume: 100%");
		volumeLabel.setBounds(10, 26, 141, 14);
		panel_2.add(volumeLabel);

		JSeparator separator = new JSeparator();
		separator.setBounds(326, 16, 0, 133);
		panel.add(separator);

		btnGoLive = new JButton("Start Stream");
		btnGoLive.setBounds(10, 404, 116, 54);
		contentPane.add(btnGoLive);

		JButton btnNewButton_1_1_1 = new JButton("Remove");
		btnNewButton_1_1_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listAvailableImages.getSelectedIndex() < 0) {
					JOptionPane.showMessageDialog(null, "Please select/click an image in 'available images' to remove.",
							"Error: No image selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete:\n" + listAvailableImages.getSelectedValue(), "Confirmation",
						JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					SystemFiles.removeFileByName("./images", listAvailableImages.getSelectedValue());
					updateAvailableImages();
				}
			}
		});
		btnNewButton_1_1_1.setBounds(680, 7, 80, 23);
		contentPane.add(btnNewButton_1_1_1);

		JButton btnNewButton_2_1 = new JButton("Add");
		btnNewButton_2_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				importImageFromFiles();
			}
		});
		btnNewButton_2_1.setBounds(590, 7, 80, 23);
		contentPane.add(btnNewButton_2_1);

		JButton btnPlaylistUp = new JButton("↑");
		btnPlaylistUp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listLivestreamPlaylist.getSelectedIndex() < 1)
					return;

				int newIndex = listLivestreamPlaylist.getSelectedIndex() - 1;
				// So we just need to swap the two string here...
				ArrayList<String> updatedSongs = new ArrayList<String>();
				ListModel<String> model = listLivestreamPlaylist.getModel();
				String oldSong = model.getElementAt(newIndex);
				String newSong = model.getElementAt(listLivestreamPlaylist.getSelectedIndex());

				for (int i = 0; i < model.getSize(); i++) {
					if (i == newIndex) {
						updatedSongs.add(newSong);
					} else if (i == listLivestreamPlaylist.getSelectedIndex()) {
						updatedSongs.add(oldSong);
					} else {
						updatedSongs.add(model.getElementAt(i));
					}
				}

				setJListValues(listLivestreamPlaylist, updatedSongs);
				listLivestreamPlaylist.setSelectedIndex(newIndex);
			}
		});
		btnPlaylistUp.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnPlaylistUp.setBounds(417, 189, 42, 42);
		contentPane.add(btnPlaylistUp);

		JButton btnPlaylistDown = new JButton("↓");
		btnPlaylistDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listLivestreamPlaylist.getSelectedIndex() >= listLivestreamPlaylist.getModel().getSize() - 1)
					return;

				int newIndex = listLivestreamPlaylist.getSelectedIndex() + 1;
				// So we just need to swap the two string here...
				ArrayList<String> updatedSongs = new ArrayList<String>();
				ListModel<String> model = listLivestreamPlaylist.getModel();
				String oldSong = model.getElementAt(newIndex);
				String newSong = model.getElementAt(listLivestreamPlaylist.getSelectedIndex());

				for (int i = 0; i < model.getSize(); i++) {
					if (i == newIndex) {
						updatedSongs.add(newSong);
					} else if (i == listLivestreamPlaylist.getSelectedIndex()) {
						updatedSongs.add(oldSong);
					} else {
						updatedSongs.add(model.getElementAt(i));
					}
				}

				setJListValues(listLivestreamPlaylist, updatedSongs);
				listLivestreamPlaylist.setSelectedIndex(newIndex);
			}
		});
		btnPlaylistDown.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnPlaylistDown.setBounds(417, 250, 42, 42);
		contentPane.add(btnPlaylistDown);

		JButton btnPrevSong = new JButton("< Previous Song");
		btnPrevSong.setEnabled(false);
		btnPrevSong.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LivestreamPlayer.getInstance().skipToPreviousSong();
			}
		});
		btnPrevSong.setBounds(136, 404, 133, 23);
		contentPane.add(btnPrevSong);

		JButton btnNextSong = new JButton("Next Song >");
		btnNextSong.setEnabled(false);
		btnNextSong.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LivestreamPlayer.getInstance().skipToNextSong();
			}
		});
		btnNextSong.setBounds(136, 435, 133, 23);
		contentPane.add(btnNextSong);
		btnGoLive.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				boolean noPlatform = true;
				Iterator<AbstractButton> buttonIterator = radioButtonGroup.getElements().asIterator();
				while (buttonIterator.hasNext()) {
					JRadioButton radioButton = (JRadioButton) buttonIterator.next();
					if (radioButton.isSelected()) {
						noPlatform = false;
					}
				}

				if (noPlatform) {
					JOptionPane.showMessageDialog(null, "Please select a streaming platform.",
							"Error: No stream platform", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (listLivestreamPlaylist.getModel().getSize() < 1) {
					JOptionPane.showMessageDialog(null, "Please add songs to the playlist.",
							"Error: No songs in playlist", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (!SystemFiles.folderExists("./data")) {
					SystemFiles.createFolder("./data");
				}

				try {
					JSONObject jsonFile = SystemFiles.getJSONFromFile("./data/streams.json");

					if (!jsonFile.has(getSelectedPlatform().toLowerCase())) {
						if (!promptForStreamKey())
							return;
					}

				} catch (FileNotFoundException e1) {
					if (!promptForStreamKey())
						return;
				}

				goLiveCallback.accept(null);
				if (btnGoLive.getText().equals("Start Stream")) {
					btnNextSong.setEnabled(true);
					btnPrevSong.setEnabled(true);

					volumeSlider.setEnabled(false);
					btnPlaylistUp.setEnabled(false);
					btnPlaylistDown.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					btnNewButton_1_1.setEnabled(false);

					btnGoLive.setText("End Stream");
				} else {
					btnNextSong.setEnabled(false);
					btnPrevSong.setEnabled(false);

					volumeSlider.setEnabled(true);
					btnPlaylistUp.setEnabled(true);
					btnPlaylistDown.setEnabled(true);
					btnNewButton_2.setEnabled(true);
					btnNewButton_1_1.setEnabled(true);

					btnGoLive.setText("Start Stream");
				}

			}
		});

		// Update available songs & images
		updateAvailableSongs();
		updateAvailableImages();

		setVisible(true);
		setLocationRelativeTo(null); // Center window to center of screen.
	}

	private void updateAvailableImages() {
		String imagesDirectory = System.getProperty("user.dir") + "/images";
		ArrayList<String> imageNames = SystemFiles.getFileNameList(imagesDirectory);
		setJListValues(this.listAvailableImages, imageNames);
	}

	private void updateAvailableSongs() {
		String songsDirectory = System.getProperty("user.dir") + "/songs";
		SystemFiles.createFolder(songsDirectory);
		ArrayList<String> songNames = SystemFiles.getFileNameList(songsDirectory, "mp3");
		setJListValues(this.listAvailableSongs, songNames);
	}

	public boolean promptForStreamKey(String platform) {
		streamKey = JOptionPane.showInputDialog(null, "Enter your stream key for " + platform, "Stream key",
				JOptionPane.WARNING_MESSAGE);
		if (streamKey == null) {
			return false;
		}
		JSONObject platformJsonObj = new JSONObject();
		JSONObject streamKeyJsonObj = new JSONObject();
		streamKeyJsonObj.put("key", streamKey);
		platformJsonObj.put(platform.toLowerCase(), streamKeyJsonObj);
		if (!new File("./data/streams.json").exists()) {
			SystemFiles.createJSONFile("./data/streams.json", platformJsonObj);
		} else {
			try {
				SystemFiles.updateJSONFile("./data/streams.json", platformJsonObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public boolean promptForStreamKey() {
		return promptForStreamKey(getSelectedPlatform());
	}

	@SuppressWarnings("serial")
	public void setJListValues(JList<String> jList, ArrayList<String> stringArrayList) {
		String[] values = stringArrayList.toArray(new String[stringArrayList.size()]); // convert ArrayList to array
		jList.setModel(new AbstractListModel<String>() {
			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
	}

	public void removeJListElement(JList<String> jList, int index) {
		ArrayList<String> stringArrayList = new ArrayList<String>();
		for (int i = 0; i < jList.getModel().getSize(); i++) {
			if (i == index)
				continue;

			stringArrayList.add(jList.getModel().getElementAt(i));
		}

		setJListValues(jList, stringArrayList);
	}

	public boolean jListHasValue(JList<String> jList, String value) {
		ListModel<String> model = jList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i).equals(value)) {
				return true;
			}
		}
		return false;
	}

	public void onGoLiveButtonClicked(Consumer<Void> callback) {
		this.goLiveCallback = callback;
	}

	public String getSelectedPlatform() {
		Iterator<AbstractButton> buttonIterator = radioButtonGroup.getElements().asIterator();
		while (buttonIterator.hasNext()) {
			JRadioButton radioButton = (JRadioButton) buttonIterator.next();
			if (radioButton.isSelected()) {
				return radioButton.getText();
			}
		}

		return null;
	}

	public String getStreamKey() {
		if (streamKey == null) {
			try {
				streamKey = SystemFiles.getJSONFromFile("./data/streams.json")
						.getJSONObject(getSelectedPlatform().toLowerCase()).getString("key");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return streamKey;
	}

	public boolean getStreamState() {
		if (btnGoLive.getText().equals("Start Stream")) {
			return true;
		}

		return false;
	}

	public void setPlayingSong(String songName) {
		listLivestreamPlaylist.setCellRenderer(new DefaultListCellRenderer());
		txtCurrentlyPlaying.setText("Currently Playing:\r\n" + songName);

		String rawName = songName + ".mp3";
		ArrayList<String> updatedNames = new ArrayList<String>();
		ListModel<String> model = listLivestreamPlaylist.getModel();

		// Update * on songPlaylist
		if (!songName.equals("N/A")) {

			for (int i = 0; i < model.getSize(); i++) {
				String elementSong = model.getElementAt(i);
				if (elementSong.charAt(0) == '>' && !elementSong.equals(rawName)) {
					updatedNames.add(elementSong.substring(1));
				} else if (elementSong.charAt(0) != '>' && elementSong.equals(rawName)) {
					updatedNames.add(">" + elementSong);
					listLivestreamPlaylist.setCellRenderer(new BoldListCellRenderer(i));
				} else {
					updatedNames.add(model.getElementAt(i));
				}
			}

			setJListValues(listLivestreamPlaylist, updatedNames);
		} else {
			for (int i = 0; i < model.getSize(); i++) {
				String elementSong = model.getElementAt(i);
				if (elementSong.charAt(0) == '>') {
					updatedNames.add(elementSong.substring(1));
				} else {
					updatedNames.add(model.getElementAt(i));
				}
			}

			setJListValues(listLivestreamPlaylist, updatedNames);
		}
	}

	private void importSongFromFiles() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new AudioFileFilter());

		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			// The user selected a file
			File selectedFile = fileChooser.getSelectedFile();

			try {
				SystemFiles.copyFileToPath(selectedFile.getAbsolutePath(), "./songs");
				updateAvailableSongs();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void importSongFromYouTube() {
		// Provide a pop-up box where user can enter video URL
		// Buttons should be OK, Cancel
		String videoURL = JOptionPane.showInputDialog(null, "Enter YouTube video URL: ", "Import YouTube song",
				JOptionPane.INFORMATION_MESSAGE);
		if (videoURL == null) {
			return;
		}

		Thread thread = new Thread(() -> {
			setEnabled(false);
			VideoDownloaderFrame downloaderFrame = new VideoDownloaderFrame();

			VideoConverter.getInstance().onConvertCommadnOutput((output) -> {
				System.out.println("!" + output);
				String songName = null;

				if (output.contains("Destination:")) {
					songName = output.substring(output.lastIndexOf("\\") + 1, output.lastIndexOf("."));
					downloaderFrame.setSongName(songName);
				}

				if (output.contains("download") && output.contains("% of")) {
					String precentDone = output.substring(output.indexOf(']') + 1, output.indexOf("of"));
					precentDone = precentDone.trim();
					precentDone = precentDone.replace("%", "");
					downloaderFrame.setPrecentDone(precentDone);
				}
			});

			downloaderFrame.onCancel((Void) -> {
				VideoConverter.getInstance().cancelConversion();
			});

			VideoConverter.getInstance().videoURLToMp3(videoURL);

			downloaderFrame.setVisible(false);
			downloaderFrame.dispose();

			setAlwaysOnTop(true);
			setAlwaysOnTop(false);

			setEnabled(true);
			updateAvailableSongs();
		});

		thread.start();
	}

	private void importImageFromFiles() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new ImageFileFilter());

		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			// The user selected a file
			File selectedFile = fileChooser.getSelectedFile();

			try {
				SystemFiles.copyFileToPath(selectedFile.getAbsolutePath(), "./images");
				updateAvailableImages();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public ArrayList<String> getSongPlaylist() {
		ArrayList<String> songs = new ArrayList<String>();
		ListModel<String> model = listLivestreamPlaylist.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			songs.add(model.getElementAt(i));
		}

		return songs;
	}

	private static class BoldListCellRenderer extends DefaultListCellRenderer {
		private final int boldIndex;

		public BoldListCellRenderer(int boldIndex) {
			this.boldIndex = boldIndex;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (index == boldIndex) {
				c.setFont(c.getFont().deriveFont(Font.ITALIC));
			}
			return c;
		}
	}

}
