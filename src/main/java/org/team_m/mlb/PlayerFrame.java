package org.team_m.mlb;

import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

public class PlayerFrame extends JFrame {

	private static final long serialVersionUID = -682428546878821527L;
	private JPanel contentPane;

	private JList<String> listAvailableSongs;
	private JList<String> listAvailableImages;
	private Consumer<Void> goLiveCallback;

	public PlayerFrame() {
		setResizable(false);
		setTitle("Music Livestream Bot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 679, 530);
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

		JTextArea txtrCurrentlyPlaying = new JTextArea();
		txtrCurrentlyPlaying.setBackground(UIManager.getColor("Button.background"));
		txtrCurrentlyPlaying.setEditable(false);
		txtrCurrentlyPlaying.setLineWrap(true);
		txtrCurrentlyPlaying.setText("Currently Playing:\r\nN/A");
		txtrCurrentlyPlaying.setBounds(10, 303, 397, 70);
		contentPane.add(txtrCurrentlyPlaying);

		JLabel label = new JLabel("New label");
		label.setBounds(394, 384, -241, 39);
		contentPane.add(label);

		JButton btnGoLive = new JButton("Go Live");
		btnGoLive.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				goLiveCallback.accept(null);
			}
		});
		
		btnGoLive.setBounds(10, 418, 133, 62);
		contentPane.add(btnGoLive);

		JLabel lblNewLabel_2 = new JLabel("Currently: Not Live");
		lblNewLabel_2.setBounds(153, 442, 123, 14);
		contentPane.add(lblNewLabel_2);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(417, 36, 236, 256);
		contentPane.add(scrollPane_2);

		listAvailableImages = new JList<String>();
		scrollPane_2.setViewportView(listAvailableImages);
		listAvailableImages.setValueIsAdjusting(true);
		listAvailableImages.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listAvailableImages.setBackground(SystemColor.menu);

		JLabel lblAvailableImages = new JLabel("Available Images:");
		lblAvailableImages.setBounds(417, 11, 143, 14);
		contentPane.add(lblAvailableImages);

		JButton btnNewButton_3 = new JButton("+");
		btnNewButton_3.setBounds(537, 4, 41, 29);
		contentPane.add(btnNewButton_3);

		JButton btnNewButton_3_1 = new JButton("-");
		btnNewButton_3_1.setBounds(588, 4, 41, 29);
		contentPane.add(btnNewButton_3_1);
		setVisible(true);
		setLocationRelativeTo(null); // Center window to center of screen.
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
}
