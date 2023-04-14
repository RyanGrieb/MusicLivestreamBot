package org.team_m.mlb;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.json.JSONObject;
import org.team_m.mlb.system.SystemFiles;

public class PlayerFrame extends JFrame {

	private static final long serialVersionUID = -682428546878821527L;
	private JPanel contentPane;

	private JList<String> listAvailableSongs;
	private JList<String> listAvailableImages;
	private Consumer<Void> goLiveCallback;

	private ButtonGroup radioButtonGroup;
	private JRadioButton rbPlatformYouTube;
	private JRadioButton rbPlatformTwitch;
	private String streamKey;
	private JButton btnGoLive;
	private JTextArea txtCurrentlyPlaying;

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
		mnNewMenu.add(mntmNewMenuItem);

		JMenu mnNewMenu_1 = new JMenu("Add Song");
		mnNewMenu.add(mnNewMenu_1);

		JMenuItem mntmNewMenuItem_2 = new JMenuItem("From Files...");
		mnNewMenu_1.add(mntmNewMenuItem_2);

		JMenuItem mntmNewMenuItem_1 = new JMenuItem("From Youtube");
		mnNewMenu_1.add(mntmNewMenuItem_1);

		JMenu mnNewMenu_2 = new JMenu("Options");
		menuBar.add(mnNewMenu_2);

		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Edit Stream Keys...");
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
		btnNewButton.setBounds(153, 7, 116, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Remove Song");
		btnNewButton_1.setBounds(291, 7, 116, 23);
		contentPane.add(btnNewButton_1);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 189, 397, 103);
		contentPane.add(scrollPane_1);

		JList<?> listLivestreamPlaylist = new JList<Object>();
		scrollPane_1.setViewportView(listLivestreamPlaylist);
		listLivestreamPlaylist.setValueIsAdjusting(true);
		listLivestreamPlaylist.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listLivestreamPlaylist.setBackground(SystemColor.menu);

		JLabel lblNewLabel_1 = new JLabel("Song Playlist:");
		lblNewLabel_1.setBounds(20, 159, 123, 14);
		contentPane.add(lblNewLabel_1);

		JButton btnNewButton_1_1 = new JButton("Remove Song");
		btnNewButton_1_1.setBounds(291, 155, 116, 23);
		contentPane.add(btnNewButton_1_1);

		JButton btnNewButton_2 = new JButton("Add Song");
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
		scrollPane_2.setBounds(427, 36, 333, 256);
		contentPane.add(scrollPane_2);

		listAvailableImages = new JList<String>();
		scrollPane_2.setViewportView(listAvailableImages);
		listAvailableImages.setValueIsAdjusting(true);
		listAvailableImages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listAvailableImages.setBackground(SystemColor.menu);

		JLabel lblAvailableImages = new JLabel("Available Images:");
		lblAvailableImages.setBounds(427, 11, 143, 14);
		contentPane.add(lblAvailableImages);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)),
				"Stream Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(427, 308, 333, 155);
		contentPane.add(panel);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setToolTipText("");
		panel_2.setBorder(new TitledBorder(null, "Platforms", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		Panel panel_1 = new Panel();
		panel_2.add(panel_1);

		radioButtonGroup = new ButtonGroup();
		rbPlatformYouTube = new JRadioButton("Youtube");
		rbPlatformYouTube.setSelected(true);
		panel_1.add(rbPlatformYouTube);
		radioButtonGroup.add(rbPlatformYouTube);

		rbPlatformTwitch = new JRadioButton("Twitch");
		panel_1.add(rbPlatformTwitch);
		radioButtonGroup.add(rbPlatformTwitch);

		JSeparator separator = new JSeparator();
		panel.add(separator);

		btnGoLive = new JButton("Start Stream");
		btnGoLive.setBounds(10, 404, 116, 54);
		contentPane.add(btnGoLive);

		JButton btnNewButton_1_1_1 = new JButton("Remove");
		btnNewButton_1_1_1.setBounds(680, 7, 80, 23);
		contentPane.add(btnNewButton_1_1_1);

		JButton btnNewButton_2_1 = new JButton("Add");
		btnNewButton_2_1.setBounds(590, 7, 80, 23);
		contentPane.add(btnNewButton_2_1);
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
					JOptionPane.showMessageDialog(null, "Please select a streaming platform.", "Error",
							JOptionPane.ERROR_MESSAGE);
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
					btnGoLive.setText("End Stream");
				} else {
					btnGoLive.setText("Start Stream");
				}

			}
		});
		setVisible(true);
		setLocationRelativeTo(null); // Center window to center of screen.
	}

	public boolean promptForStreamKey() {
		streamKey = JOptionPane.showInputDialog(null, "Enter your stream key for " + getSelectedPlatform(),
				"No stream key found", JOptionPane.WARNING_MESSAGE);
		if (streamKey == null) {
			return false;
		}
		JSONObject platformJsonObj = new JSONObject();
		JSONObject streamKeyJsonObj = new JSONObject();
		streamKeyJsonObj.put("key", streamKey);
		platformJsonObj.put(getSelectedPlatform().toLowerCase(), streamKeyJsonObj);
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

	@SuppressWarnings("serial")
	public void setAvailableSongsList(ArrayList<String> songNames) {
		String[] values = songNames.toArray(new String[songNames.size()]); // convert ArrayList to array
		listAvailableSongs.setModel(new AbstractListModel<String>() {
			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
	}

	public void setAvailableImages(ArrayList<String> imageNames) {
		String[] values = imageNames.toArray(new String[imageNames.size()]); // convert ArrayList to array
		listAvailableImages.setModel(new AbstractListModel<String>() {
			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
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
		txtCurrentlyPlaying.setText("Currently Playing:\r\n" + songName);
	}
}
