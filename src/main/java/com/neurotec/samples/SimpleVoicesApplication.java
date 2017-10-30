package com.neurotec.samples;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.neurotec.lang.NCore;
import com.neurotec.samples.util.LibraryManager;
import com.neurotec.samples.util.Utils;

public final class SimpleVoicesApplication {

	// ===========================================================
	// Private constructor
	// ===========================================================

	private SimpleVoicesApplication() {
	}

	// ===========================================================
	// Main method
	// ===========================================================

	public static void main(String[] args) {
		/*
		LibraryManager.initLibraryPath();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame();
				frame.setTitle("Simple Voices Sample");
				frame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						NCore.shutdown();
					}
				});
				frame.add(new MainPanel(), BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
*/

		//clean gui
		JFrame recorderFrame = new JFrame();
		recorderFrame.setTitle("Voice registeration");
		recorderFrame.setIconImage(Utils.createIconImage("images/Logo16x16.png"));
		recorderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		recorderFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				NCore.shutdown();
			}
		});

		JPanel recorderHomePanel = new JPanel( new GridLayout(4, 1));

		JLabel userRegistrationLabel = new JLabel("User registration");
		userRegistrationLabel.setHorizontalAlignment(SwingConstants.CENTER);;
		recorderHomePanel.add(userRegistrationLabel);

		JPanel namePanel = new JPanel( new GridLayout(1, 2));
		namePanel.add( new JLabel("Name: "));
		JTextField nameField = new JTextField();
		//nameField.setSize(10, 3);
		namePanel.add(nameField);
		recorderHomePanel.add(namePanel);

		JButton startBtn = new JButton("Start");
		JLabel textLabel = new JLabel("After clicking Start. Read the generated text here.");

		recorderHomePanel.add(startBtn);
		recorderHomePanel.add(textLabel);

		recorderFrame.add(recorderHomePanel);
		recorderFrame.pack();
		recorderFrame.setLocationRelativeTo(null);
		recorderFrame.setVisible(true);

		//add action listeners.
	}
}