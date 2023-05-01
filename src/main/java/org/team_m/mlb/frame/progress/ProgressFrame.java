package org.team_m.mlb.frame.progress;

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

public abstract class ProgressFrame extends JFrame {

	private JPanel contentPane;
	private JLabel lblProgress;
	private JProgressBar progressBar;
	private JTextArea txtrDisplayText;
	private Consumer<Void> cancelCallback;

	public ProgressFrame(String frameTitle, String displayText) {
		setResizable(false);
		setTitle(frameTitle);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 188);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 75, 202, 26);
		contentPane.add(progressBar);

		txtrDisplayText = new JTextArea();
		txtrDisplayText.setEditable(false);
		txtrDisplayText.setLineWrap(true);
		txtrDisplayText.setText(displayText);
		txtrDisplayText.setBackground(SystemColor.control);
		txtrDisplayText.setBounds(10, 11, 237, 53);
		contentPane.add(txtrDisplayText);

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

	public void setPrecentDone(float precentDone) {
		lblProgress.setText(String.format("%.0f", precentDone) + "%");
		progressBar.setValue((int) precentDone);
	}

	public void setDisplayText(String displayText) {
		txtrDisplayText.setText(displayText);
	}

	public void onCancel(Consumer<Void> callback) {
		cancelCallback = callback;
	}
}
